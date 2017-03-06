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
package jp.primecloud.auto.service.impl;

import org.apache.commons.lang.BooleanUtils;

import jp.primecloud.auto.common.component.PasswordEncryptor;
import jp.primecloud.auto.entity.crud.PccSystemInfo;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.exception.AutoException;
import jp.primecloud.auto.log.EventLogLevel;
import jp.primecloud.auto.log.EventLogger;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.service.UserService;
import jp.primecloud.auto.service.dto.UserDto;

/**
 * <p>
 * UserServiceインターフェースの実装クラス
 * </p>
 *
 */
public class UserServiceImpl extends ServiceSupport implements UserService {

    protected EventLogger eventLogger;

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDto authenticate(String username, String password) {
        User user = userDao.readByUsername(username);

        if (user == null) {
            // ユーザ情報が見つからない場合
            eventLogger.log(EventLogLevel.INFO, null, null, null, null, null, null, null, null, "AuditLoginFailure",
                    null, null, new Object[] { username });
            throw new AutoApplicationException("ESERVICE-000101", username);
        }

        if (BooleanUtils.isNotTrue(user.getEnabled())) {
            // ユーザが無効の場合
            eventLogger.log(EventLogLevel.INFO, null, null, null, null, null, null, null, null, "AuditLoginFailure",
                    null, null, new Object[] { username });
            throw new AutoApplicationException("ESERVICE-000106", username);
        }

        // ユーザパスワード暗号化キーを取得
        PccSystemInfo pccSystemInfo = pccSystemInfoDao.read();

        if (pccSystemInfo == null) {
            // PCC_SYSTEM_INFOのレコードが存在しない場合
            throw new AutoException("ESERVICE-000103");
        }

        // 入力パスワードを暗号化
        PasswordEncryptor encryptor = new PasswordEncryptor();
        String encryptedPassword = encryptor.encrypt(password, pccSystemInfo.getSecretKey());

        // DBから取得したパスワードを比較
        if (!user.getPassword().equals(encryptedPassword)) {
            // パスワードが異なっていた場合
            eventLogger.log(EventLogLevel.INFO, user.getUserNo(), user.getUsername(), null, null, null, null, null,
                    null, "AuditLoginFailure", null, null, new Object[] { user.getUserNo() });
            throw new AutoApplicationException("ESERVICE-000102", username);
        }

        user.setPassword(password);

        UserDto dto = new UserDto();
        dto.setUser(user);

        eventLogger.log(EventLogLevel.INFO, user.getUserNo(), user.getUsername(), null, null, null, null, null, null,
                "AuditLoginSuccess", null, null, null);

        return dto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDto getUser(Long userNo) {
        User user = userDao.read(userNo);

        if (user == null) {
            // ユーザ情報が見つからない場合
            return null;
        }

        // パスワードを復号
        PasswordEncryptor passEncrypt = new PasswordEncryptor();
        PccSystemInfo systemInfo = pccSystemInfoDao.read();
        user.setPassword(passEncrypt.decrypt(user.getPassword(), systemInfo.getSecretKey()));

        UserDto dto = new UserDto();
        dto.setUser(user);

        return dto;
    }

    /**
     * eventLoggerを設定します。
     *
     * @param eventLogger eventLogger
     */
    public void setEventLogger(EventLogger eventLogger) {
        this.eventLogger = eventLogger;
    }

}
