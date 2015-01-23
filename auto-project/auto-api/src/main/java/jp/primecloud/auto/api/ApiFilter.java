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
import java.util.Arrays;
import java.util.LinkedHashMap;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import jp.primecloud.auto.common.log.LoggingUtils;
import jp.primecloud.auto.config.Config;
import jp.primecloud.auto.entity.crud.ApiCertificate;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.util.MessageUtils;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;



public class ApiFilter extends ApiSupport implements ContainerRequestFilter {

    //オートスケール用ユーザ
    private static final String AUTO_SCALING_USER = Config.getProperty("autoScaling.username");

    //ファーム番号が必須ではないAPI名
    private static final String[] notUseFarmApies = {"CreateFarm", "ListFarm", "Login", "ListTemplate", "ListEventLog"};

    private static final Integer SECURE_WAIT_TIME = Integer.parseInt(Config.getProperty("pccApi.secureWaitTime"));

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

        try {
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
            String farmNo = decodeParamMap.get(PARAM_NAME_FARM_NO);
            String userName = null;
            Long userNo = null;
            Farm farm = null;
            User accessUser = null;
            User autoScaleUser = null;
            User masterUser = null;

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
                Thread.sleep(SECURE_WAIT_TIME.intValue() * 1000);
                throw new AutoApplicationException("EAPI-000008", PARAM_NAME_ACCESS_ID, accessId);
            }

            //ユーザ(APIアクセスユーザ)取得
            accessUser = userDao.read(apiCertificate.getUserNo());
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
            String uriText = createUriQueryParams(decodeParamMap, uri);
            String encodeUriText = encodeSHA256(uriText, apiCertificate.getApiSecretKey());
            if (BooleanUtils.isFalse(encodeUriText.equals(signature))) {
                //Signatureが合致しない場合
                Thread.sleep(SECURE_WAIT_TIME.intValue() * 1000);
                throw new AutoApplicationException("EAPI-000008", "URL", uri.toString());
            }

            if (Arrays.asList(notUseFarmApies).contains(apiName) == false) {
                //FarmNo
                ApiValidate.validateFarmNo(farmNo);
                farm = farmDao.read(Long.parseLong(farmNo));
                if(farm == null) {
                    // ファームが存在しない
                    throw new AutoApplicationException("EAPI-100000", "Farm", PARAM_NAME_FARM_NO, farmNo);
                }
            }

            //ユーザ置き換え処理
            if (farm != null && (StringUtils.isNotEmpty(AUTO_SCALING_USER) && AUTO_SCALING_USER.equals(accessUser.getUsername())
                || accessUser.getPowerUser())) {
                //オートスケーリング用ユーザ、またはPOWER USERからのアクセスの場合
                    //ユーザ名をファームから取得したものに置き換える
                //TODO 暫定処理なので、PCC-APIのロール権限の改修が必要
                autoScaleUser = userDao.read(farm.getUserNo());
                userNo = autoScaleUser.getUserNo();
                userName = autoScaleUser.getUsername();
            } else if (!accessUser.getPowerUser() && accessUser.getUserNo().equals(accessUser.getMasterUser()) == false){
                //通常のPCCユーザ → マスターユーザに置き換え
                if (accessUser.getMasterUser() != null) {
                userNo = accessUser.getMasterUser();
                masterUser = userDao.read(accessUser.getMasterUser());
                userName = masterUser.getUsername();
                }
            } else {
                //マスターユーザ → そのまま
                userNo = accessUser.getUserNo();
                userName = accessUser.getUsername();
                }

            if(farm != null && farm.getUserNo().equals(userNo) == false) {
                    // ファームがユーザと紐づいていない
                throw new AutoApplicationException("EAPI-100026", farmNo, accessUser.getUsername());
            }

            //User(ユーザ名)パラメータをURLに設定
            if (!apiName.equals("Login")) {
            decodeParamMap.put(PARAM_NAME_USER, userName);
            }

            //LoggingUtilsにデータを設定
            LoggingUtils.setUserNo(userNo);
            LoggingUtils.setLoginUserNo(accessUser.getUserNo());
            LoggingUtils.setUserName(accessUser.getUsername());
            if (farm != null) {
                LoggingUtils.setFarmNo(farm.getFarmNo());
                LoggingUtils.setFarmName(farm.getFarmName());
            }

            // デコードしたURLを再設定
            for (String key: decodeParamMap.keySet()) {
                request.getQueryParameters().putSingle(key, decodeParamMap.get(key));
            }

            // アクセスログ出力
            log.info(MessageUtils.getMessage("IAPI-000001", accessUser.getUsername(), apiName));
        } catch (Throwable e) {
            String message = "";
            if (e instanceof AutoException || e instanceof AutoApplicationException) {
                message = e.getMessage();
            } else {
                message = MessageUtils.getMessage("EAPI-000000");
            }
            // エラーログ出力
            log.error(message, e);

            // ErrorApiへリダイレクト
            URI errorUri = URI.create(request.getBaseUri() + "Error");
            request.setUris(request.getBaseUri(), errorUri);
            request.getQueryParameters().putSingle("Message", message);
        }

        return request;
    }

   /**
    *
    * BASE64デコード済みのリクエストパラメータをLinkedHashMapで取得
    * ※リクエストパラメータ全てをBASE64エンコード
    *
    * @param url URL
    * @return LinkedHashMap<パラメータ名, パラメータ値>
    * @throws UnsupportedEncodingException
    */
   @SuppressWarnings("static-access")
private LinkedHashMap<String, String> getDecodedParamMap(URI uri)
   throws UnsupportedEncodingException {
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
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    private static String encodeSHA256(String plainText, String keyText)
    throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        SecretKey secretKey = new SecretKeySpec(keyText.getBytes("UTF-8"),"HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKey);

        byte[] plainBytes = plainText.getBytes("UTF-8");
        byte[] encodedBytes = mac.doFinal(plainBytes);
        byte[] hexBytes = new Hex().encode(encodedBytes);

        return new String(hexBytes, "UTF-8");
    }

    /**
     *
     * URI(Signature含まない)文字列を作成
     *
     * @param decodeParamMap base64デコード済みのリクエストパラメータ
     * @param uri URI(フルパス)文字列
     * @return リクエストパラメータを元にしたURL(Signature含まない)
     */
    private String createUriQueryParams(LinkedHashMap<String, String> decodeParamMap, URI uri) {
        String baseUriText = uri.getScheme() + "://" + uri.getAuthority() + uri.getPath() + "?";
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