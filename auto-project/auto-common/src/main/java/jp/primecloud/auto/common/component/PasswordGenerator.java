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
package jp.primecloud.auto.common.component;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * <p>
 * ランダムなパスワードを生成するClassです。
 * </p>
 *
 */
public class PasswordGenerator {

    protected SecureRandom secureRandom;

    protected String algorithm = "SHA1PRNG";

    protected char[] characterSet;

    public PasswordGenerator() {
        initialize();
    }

    protected void initialize() {
        // 乱数の作成
        try {
            secureRandom = SecureRandom.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        // 乱数シードの設定
        secureRandom.setSeed(System.currentTimeMillis());

        // 文字セットの作成
        characterSet = createCharacterSet();
    }

    protected char[] createCharacterSet() {
        char[] chars = new char[36];
        for (int i = 0; i < 26; i++) {
            chars[i] = (char) ('a' + i);
        }
        for (int i = 0; i < 10; i++) {
            chars[26 + i] = (char) ('0' + i);
        }
        return chars;
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param length
     * @return
     */
    public String generate(int length) {
        char[] password = new char[length];
        for (int i = 0; i < length; i++) {
            int n = secureRandom.nextInt(characterSet.length);
            password[i] = characterSet[n];
        }
        return new String(password);
    }

}
