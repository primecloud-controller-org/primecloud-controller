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

/**
 * <p>
 * PCCユーザのパスワードを暗号化・復号化するClassです。
 * </p>
 *
 */
package jp.primecloud.auto.common.component;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class PasswordEncryptor {

    private static final String ALGORITHM = "AES" ;

    private static final String CIPHER_PARAM = ALGORITHM + "/CBC/PKCS5Padding";

    private static final byte[] IV ={ 0x00, 0x01, 0x02, 0x03,
                                      0x04, 0x05, 0x06, 0x07,
                                      0x08, 0x09, 0x0a, 0x0b,
                                      0x0c, 0x0d, 0x0e, 0x0f };

    private IvParameterSpec ivParameterSpec;

    private SecureRandom secureRandom;

    private char[] characterSet;

    private Cipher chipher;

    /**
     *
     * コンストラクタ
     *
     */
    public PasswordEncryptor() {
        initialize();
    }

    /**
     *
     * 初期化メソッド
     *
     */
    private void initialize() {
        ivParameterSpec = new IvParameterSpec(IV);

        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        // 乱数シードの設定
        secureRandom.setSeed(System.currentTimeMillis());
        // 文字セットの作成
        characterSet = createCharacterSet();

        try {
            chipher = Cipher.getInstance(CIPHER_PARAM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 暗号化
     * @param originalString
     * @param keyString
     * @return
     */
    public String encrypt(String orignalString, String keyString){
        byte[] originalBytes = orignalString.getBytes();
        byte[] keyBytes = keyString.getBytes();

        SecretKey secretKey = new SecretKeySpec(keyBytes, ALGORITHM);

        try {
            chipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }

        byte[] encryptedBytes = null;
        try {
            encryptedBytes = chipher.doFinal(originalBytes);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }

        return new String(Base64.encodeBase64(encryptedBytes));
    }

    /**
     * 復号化
     * @param encryptedString
     * @param keyString
     * @return
     */
    public String decrypt(String encryptedString, String keyString){
        byte[] encryptedBytes = Base64.decodeBase64(encryptedString.getBytes());
        byte[] keyBytes = keyString.getBytes();

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);

        try {
            chipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }

        byte decryptedBytes[] = null;
        try {
            decryptedBytes = chipher.doFinal(encryptedBytes);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }

        return new String(decryptedBytes);
    }

    /**
     * 共通鍵生成<br>
     * 指定の文字からランダム生成され128バイトの文字列を返す
     *
     * @return 共通鍵文字列
     */
    public String keyGenerate(){
        char[] key = new char[16];
        for (int i = 0; i < 16; i++) {
            int n = secureRandom.nextInt(characterSet.length);
            key[i] = characterSet[n];
        }
        return new String(key);
    }

    /**
     * 共通鍵の使用文字設定
     * @return
     */
    private char[] createCharacterSet() {
        char[] chars = new char[62];
        // a-z
        for (int i = 0; i < 26; i++) {
            chars[i] = (char) ('a' + i);
        }
        // A-Z
        for (int i = 0; i < 26; i++) {
            chars[26 + i] = (char) ('A' + i);
        }
        // 0-9
        for (int i = 0; i < 10; i++) {
            chars[52 + i] = (char) ('0' + i);
        }
        return chars;
    }
}
