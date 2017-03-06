/*
 * Copyright 2014 by SCSK Corporation.
 * 
 * This file is part of PrimeCloud Controller(TM).
 * 
 * PrimeCloud Controller(TM) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * PrimeCloud Controller(TM) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PrimeCloud Controller(TM). If not, see <http://www.gnu.org/licenses/>.
 */
package jp.primecloud.auto.api;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import jp.primecloud.auto.common.log.LoggingUtils;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.ApiCertificate;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.util.MessageUtils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

public class ApiFilter extends ApiSupport implements ContainerRequestFilter {

    private static final Integer SECURE_WAIT_TIME = Integer.parseInt(Config.getProperty("pccApi.secureWaitTime"));

    private static final String ALLOW_API = Config.getProperty("api.allowApi");

    @Context
    private HttpServletRequest servletRequest;

    /**
     * {@inheritDoc}
     */
    public ContainerRequest filter(ContainerRequest request) {
        URI uri = request.getRequestUri();

        // API名のチェック
        String apiName = uri.getPath().substring(request.getBaseUri().getPath().length());
        if (StringUtils.isEmpty(apiName)) {
            // API名が存在しない場合
            throw new AutoApplicationException("EAPI-000008", "URL", uri.toString());
        }

        // APIの実行許可チェック
        if (StringUtils.isNotEmpty(ALLOW_API)) {
            Pattern pattern = Pattern.compile(ALLOW_API);
            Matcher matcher = pattern.matcher(apiName);
            if (!matcher.matches()) {
                // APIの実行が許可されていない場合
                throw new AutoApplicationException("EAPI-000023", apiName);
            }
        }

        // URIのパラメータをBase64デコードしてマップにする
        Map<String, String> decodeParamMap = getDecodedParamMap(uri);

        String accessId = decodeParamMap.get(PARAM_NAME_ACCESS_ID);
        String signature = decodeParamMap.get(PARAM_NAME_SIGNATURE);
        String timestamp = decodeParamMap.get(PARAM_NAME_TIMESTAMP);

        // パラメータの存在チェック
        ApiValidate.validateAccessId(accessId);
        ApiValidate.validateSignature(signature);
        ApiValidate.validateTimestamp(timestamp);

        // API認証情報の取得
        ApiCertificate apiCertificate = apiCertificateDao.readByApiAccessId(accessId);
        if (apiCertificate == null) {
            // API認証情報が存在しない場合
            try {
                Thread.sleep(SECURE_WAIT_TIME.intValue() * 1000);
            } catch (InterruptedException ignore) {
            }
            throw new AutoApplicationException("EAPI-000008", PARAM_NAME_ACCESS_ID, accessId);
        }

        if (BooleanUtils.isNotTrue(apiCertificate.getEnabled())) {
            // API認証情報が無効の場合
            throw new AutoApplicationException("EAPI-000024", accessId);
        }

        // ユーザ情報の取得
        User accessUser = userDao.read(apiCertificate.getUserNo());
        if (accessUser == null) {
            // ユーザ情報が存在しない場合
            throw new AutoApplicationException("EAPI-100000", "User", "UserNo", apiCertificate.getUserNo());
        }

        if (BooleanUtils.isNotTrue(accessUser.getEnabled())) {
            // ユーザが無効の場合
            throw new AutoApplicationException("EAPI-000025");
        }

        // Signatureの合致チェック
        String uriText = createUriQueryParams(decodeParamMap);
        String encodeUriText = encodeSHA256(uriText, apiCertificate.getApiSecretKey());
        if (!encodeUriText.equals(signature)) {
            // Signatureが合致しない場合
            try {
                Thread.sleep(SECURE_WAIT_TIME.intValue() * 1000);
            } catch (InterruptedException ignore) {
            }
            throw new AutoApplicationException("EAPI-000008", "URL", uri.toString());
        }

        // ユーザ情報をリクエストに保存
        servletRequest.setAttribute(PARAM_NAME_USER, accessUser);

        // ログ出力用データを設定
        LoggingUtils.setUserNo(accessUser.getUserNo());
        LoggingUtils.setUserName(accessUser.getUsername());

        // デコードしたパラメータを再設定
        for (String key : decodeParamMap.keySet()) {
            request.getQueryParameters().putSingle(key, decodeParamMap.get(key));
        }

        // アクセスログ出力
        log.info(MessageUtils.getMessage("IAPI-000001", accessUser.getUsername(), apiName));

        return request;
    }

    /**
     * Base64デコード済みのリクエストパラメータを取得する。
     *
     * @param url URL
     * @return LinkedHashMap<パラメータ名, パラメータ値>
     */
    @SuppressWarnings("static-access")
    private Map<String, String> getDecodedParamMap(URI uri) {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

        String queryUrlText = uri.getQuery();
        if (StringUtils.isEmpty(queryUrlText)) {
            return map;
        }

        try {
            Base64 base64 = new Base64(true);
            String decodedUri = new String(base64.decodeBase64(queryUrlText.getBytes("UTF-8")), "UTF-8");
            for (String param : StringUtils.split(decodedUri, "&")) {
                String[] array = StringUtils.split(param, "=", 2);
                String key = array[0];
                String value = array[1];
                map.put(key, value);
            }
        } catch (UnsupportedEncodingException e) {
            throw new AutoApplicationException("EAPI-000008", e, "URL", uri.toString());
        }

        return map;
    }

    /**
     * HMAC-SHA256ハッシュアルゴリズムでエンコードする。
     *
     * @param plainText エンコード対象の文字列
     * @param keyText
     * @return
     */
    private static String encodeSHA256(String plainText, String keyText) {
        try {
            SecretKey secretKey = new SecretKeySpec(keyText.getBytes("UTF-8"), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);

            byte[] plainBytes = plainText.getBytes("UTF-8");
            byte[] encodedBytes = mac.doFinal(plainBytes);
            byte[] hexBytes = new Hex().encode(encodedBytes);

            return new String(hexBytes, "UTF-8");
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * URI(Signature含まない)文字列を作成する。
     *
     * @param decodeParamMap base64デコード済みのリクエストパラメータ
     * @return リクエストパラメータを元にしたURL(Signature含まない)
     */
    private String createUriQueryParams(Map<String, String> decodeParamMap) {
        String baseUriText = servletRequest.getServletPath() + servletRequest.getPathInfo() + "?";
        StringBuilder uriText = new StringBuilder(baseUriText);

        for (Entry<String, String> parameter : decodeParamMap.entrySet()) {
            if (PARAM_NAME_SIGNATURE.equals(parameter.getKey())) {
                continue;
            }
            uriText.append(parameter.getKey()).append("=").append(parameter.getValue()).append("&");
        }
        uriText.delete(uriText.length() - 1, uriText.length());

        return uriText.toString();
    }

}
