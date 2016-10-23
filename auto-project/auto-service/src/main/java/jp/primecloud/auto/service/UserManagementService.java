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
package jp.primecloud.auto.service;

import java.util.List;
import java.util.Map;

import jp.primecloud.auto.entity.crud.AuthoritySet;
import jp.primecloud.auto.entity.crud.UserAuth;
import jp.primecloud.auto.service.dto.FarmDto;
import jp.primecloud.auto.service.dto.ManagementUserDto;
import jp.primecloud.auto.service.dto.UserAuthDto;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public interface UserManagementService {

    public List<ManagementUserDto> getManagementUsers(Long userNo);

    public List<FarmDto> getFarms(Long masterUserNo);

    public UserAuth getUserAuth(Long userNo, Long farmNo);

    public List<AuthoritySet> getAuthoritySet();

    public Map<Long, UserAuth> getUserAuthMap(Long userNo);

    public void createUserAndUserAuth(Long masterUserNo, String userName, String password, Map<Long, Long> authMap);

    public void updateUserAndUserAuth(Long userNo, String userName, String password, Map<Long, Long> authMap);

    public void updateUserAuth(Long farmNo, Long userNo, UserAuthDto userAuthDto);

    public void deleteUser(Long userNo);

}
