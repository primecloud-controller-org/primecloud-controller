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
package jp.primecloud.auto.ui.mock.service;

import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.service.UserService;
import jp.primecloud.auto.service.dto.UserAuthDto;
import jp.primecloud.auto.service.dto.UserDto;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class MockUserService implements UserService {

    @Override
    public UserDto authenticate(String username, String password) {
        if ("ng".equals(password)) {
            throw new AutoApplicationException("ESERVICE-000102", username);
        }

        UserDto dto = new UserDto();

        User user = new User();
        user.setUserNo(1L);
        user.setUsername(username);
        user.setPassword(password);
        user.setPowerUser(true);
        user.setMasterUser(1L);
        dto.setUser(user);

        return dto;
    }

    @Override
    public UserDto getUser(Long userNo) {
        UserDto dto = new UserDto();

        User user = new User();
        user.setUserNo(1L);
        user.setUsername("test");
        user.setPassword("test");
        user.setPowerUser(true);
        user.setMasterUser(1L);
        dto.setUser(user);
        return dto;
    }

    @Override
    public UserAuthDto getUserAuth(Long userNo, Long farmNo) {
        return new UserAuthDto(true);
    }

}
