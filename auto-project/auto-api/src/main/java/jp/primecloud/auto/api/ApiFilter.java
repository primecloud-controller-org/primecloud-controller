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

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import jp.primecloud.auto.common.log.LoggingUtils;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.ApiCertificate;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.util.MessageUtils;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;



public class ApiFilter extends ApiSupport implements ContainerRequestFilter {

    private static final Integer SECURE_WAIT_TIME = Integer.parseInt(Config.getProperty("pccApi.secureWaitTime"));

    @Context
    private HttpServletRequest servletRequest;

    /**
     * PCC-API フィルター処理
     *
     * オートスケーリングからの呼び出しの際、
     * notUseFarmApies以外のAPIへのパラメータのユーザ名は
     * ファームと紐づいているユーザにユーザ名称を置き換えて呼び出す。
     * (ファームと紐づいてないユーザはAPIでエラーとなる為)
     * {@inheritDoc}
     */
    public ContainerRequest filter(ContainerRequest request) {

            // URI(フルパス)
            URI uri = request.getRequestUri();

            // BASE64デコードした、URIパラメータ部をマップ(LinkedHashMap)で取得
            LinkedHashMap<String, String> decodeParamMap = getDecodedParamMap(uri);

            String apiName = uri.getPath().substring(request.getBaseUri().getPath().length());
            if (StringUtils.isEmpty(apiName)) {
                //API名が存在しない
                throw new AutoApplicationException("EAPI-000008", "URL", uri.toString());
            }

            //String userName = decodeParamMap.get(PARAM_NAME_KEY);
            String accessId = decodeParamMap.get(PARAM_NAME_ACCESS_ID);
            String signature = decodeParamMap.get(PARAM_NAME_SIGNATURE);
            String timestamp = decodeParamMap.get(PARAM_NAME_TIMESTAMP);

            // 入力チェック
            // Key(ユーザ名)
            ApiValidate.validateAccessId(accessId);
            // Signature
            ApiValidate.validateSignature(signature);
            // Timestamp(yyyy/MM/dd HH:mm:ss)
            ApiValidate.validateTimestamp(timestamp);

            // PCC-API認証情報取得
            ApiCertificate apiCertificate = apiCertificateDao.readByApiAccessId(accessId);
            if (apiCertificate == null) {
                // PCC-API認証情報が存在しない
                try {
                    Thread.sleep(SECURE_WAIT_TIME.intValue() * 1000);
                } catch (InterruptedException ignore) {
                }
                throw new AutoApplicationException("EAPI-000008", PARAM_NAME_ACCESS_ID, accessId);
            }

            //ユーザ(APIアクセスユーザ)取得
            User accessUser = userDao.read(apiCertificate.getUserNo());
            if(accessUser == null) {
                // ユーザが存在しない
                throw new AutoApplicationException("EAPI-100000", "User", "UserNo", apiCertificate.getUserNo());
            }

            //TODO 無効化ユーザの場合の処理
            //ユーザ管理ツール改修のでユーザの有効/無効化ロジックを見直す為、
            //現在は処理は記述していない。ユーザ管理ツール改修後に実装の必要あり
            //if (無効化ユーザなら) {
            //    無効化エラー処理
            //}

            // Signature合致チェック
            String uriText = createUriQueryParams(decodeParamMap);
            String encodeUriText = encodeSHA256(uriText, apiCertificate.getApiSecretKey());
            if (BooleanUtils.isFalse(encodeUriText.equals(signature))) {
                //Signatureが合致しない場合
                try {
                    Thread.sleep(SECURE_WAIT_TIME.intValue() * 1000);
                } catch (InterruptedException ignore) {
                }
                throw new AutoApplicationException("EAPI-000008", "URL", uri.toString());
            }

            //Userをリクエストに保存
            servletRequest.setAttribute(PARAM_NAME_USER, accessUser);

            //LoggingUtilsにデータを設定
            LoggingUtils.setUserNo(accessUser.getUserNo());
            LoggingUtils.setLoginUserNo(accessUser.getUserNo());
            LoggingUtils.setUserName(accessUser.getUsername());

            // デコードしたURLを再設定
            for (String key: decodeParamMap.keySet()) {
                request.getQueryParameters().putSingle(key, decodeParamMap.get(key));
            }

            // アクセスログ出力
            log.info(MessageUtils.getMessage("IAPI-000001", accessUser.getUsername(), apiName));

        return request;
    }

   /**
    *
    * BASE64デコード済みのリクエストパラメータをLinkedHashMapで取得
    * ※リクエストパラメータ全てをBASE64エンコード
    *
    * @param url URL
    * @return LinkedHashMap<パラメータ名, パラメータ値>
    */
   @SuppressWarnings("static-access")
private LinkedHashMap<String, String> getDecodedParamMap(URI uri) {
       LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
       String queryUrlText = uri.getQuery();
       if (StringUtils.isNotEmpty(queryUrlText)) {
           try {
               Base64 base64 = new Base64(true);
               String decodedUri = new String(base64.decodeBase64(queryUrlText.getBytes("UTF-8")), "UTF-8");
               for(String param: decodedUri.split("&")) {
                   String key = param.substring(0, param.indexOf("="));
                   String value = param.substring(param.indexOf("=") + 1, param.length());
                   if(PARAM_NAME_SIGNATURE.equals(key)) {
                       map.put(key, value);
                   } else {
                       map.put(key, value);
                   }
               }
           } catch(Exception e) {
               throw new AutoApplicationException("EAPI-000008", e, "URL", uri.toString());
           }
       }
       return map;
   }

    /**
     *
     * HMAC-SHA256ハッシュアルゴリズムでエンコードを行う。
     *
     * @param plainText エンコード対象の文字列
     * @param keyText
     * @return
     */
    private static String encodeSHA256(String plainText, String keyText) {
        try {
        SecretKey secretKey = new SecretKeySpec(keyText.getBytes("UTF-8"),"HmacSHA256");
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
     *
     * URI(Signature含まない)文字列を作成
     *
     * @param decodeParamMap base64デコード済みのリクエストパラメータ
     * @return リクエストパラメータを元にしたURL(Signature含まない)
     */
    private String createUriQueryParams(LinkedHashMap<String, String> decodeParamMap) {
        String baseUriText = servletRequest.getServletPath() + servletRequest.getPathInfo() + "?";
        StringBuffer uriText = new StringBuffer(baseUriText);
        String splitChar = "";
        for (String key: decodeParamMap.keySet()) {
            if(PARAM_NAME_SIGNATURE.equals(key) == false) {
                uriText.append(splitChar + key + "=" + decodeParamMap.get(key));
                splitChar = "&";
            }
        }
        return uriText.toString();
    }

//    /**
//     *
//     * ユーザパスワードの複合化を行う
//     *
//     * @param encryptPass 暗号化されたPCCユーザパスワード
//     * @param key 複合化の為のキー
//     * @return 複合化されたPCCユーザパスワード
//     */
//    private String decryptUserPassword(String encryptPass, String key) {
//        PasswordEncryptor encryptor = new PasswordEncryptor();
//        String decryptPass;
//        try {
//            decryptPass = encryptor.decrypt(encryptPass, key);
//        } catch(Throwable e) {
//            throw new AutoApplicationException("EAPI-000010", e, "Password");
//        }
//        return decryptPass;
//    }
}