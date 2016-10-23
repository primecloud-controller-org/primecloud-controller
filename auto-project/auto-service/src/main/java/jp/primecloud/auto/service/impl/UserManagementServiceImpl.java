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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.primecloud.auto.common.component.PasswordEncryptor;
import jp.primecloud.auto.entity.crud.AuthoritySet;
import jp.primecloud.auto.entity.crud.Farm;
import jp.primecloud.auto.entity.crud.PccSystemInfo;
import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.entity.crud.UserAuth;
import jp.primecloud.auto.exception.AutoApplicationException;
import jp.primecloud.auto.service.ServiceSupport;
import jp.primecloud.auto.service.UserManagementService;
import jp.primecloud.auto.service.dto.FarmDto;
import jp.primecloud.auto.service.dto.ManagementUserDto;
import jp.primecloud.auto.service.dto.UserAuthDto;

import org.apache.commons.lang.StringUtils;

public class UserManagementServiceImpl extends ServiceSupport implements UserManagementService {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ManagementUserDto> getManagementUsers(Long userNo) {
        List<ManagementUserDto> dtos = new ArrayList<ManagementUserDto>();

        List<User> users = userDao.readByMasterUser(userNo);

        for (User user : users) {
            Map<Long, UserAuth> authMap = getUserAuthMap(user.getUserNo());

            ManagementUserDto dto = new ManagementUserDto();
            dto.setUser(user);
            dto.setAuthMap(authMap);

            dtos.add(dto);
        }

        return dtos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FarmDto> getFarms(Long masterUserNo) {
        List<FarmDto> dtos = new ArrayList<FarmDto>();

        // マスターユーザに紐づくファームを取得
        List<Farm> farms = farmDao.readByUserNo(masterUserNo);
        for (Farm farm : farms) {
            FarmDto dto = new FarmDto();
            dto.setFarm(farm);
            dtos.add(dto);
        }

        return dtos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAuth getUserAuth(Long userNo, Long farmNo) {
        return userAuthDao.read(farmNo, userNo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AuthoritySet> getAuthoritySet() {
        List<AuthoritySet> sets = authoritySetDao.readAll();

        return sets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Long, UserAuth> getUserAuthMap(Long userNo) {
        Map<Long, UserAuth> authMap = new HashMap<Long, UserAuth>();

        List<UserAuth> userAuths = userAuthDao.readByUserNo(userNo);
        for (UserAuth userAuth : userAuths) {
            authMap.put(userAuth.getFarmNo(), userAuth);
        }

        return authMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createUserAndUserAuth(Long masterUserNo, String userName, String password, Map<Long, Long> authMap) {
        // ユーザ情報取得
        User user = userDao.readByUsername(userName);
        if (user != null) {
            //同じユーザ名のユーザが存在する場合
            throw new AutoApplicationException("ESERVICE-000105", userName);
        }

        // パスワードを暗号化
        PasswordEncryptor encryptor = new PasswordEncryptor();
        PccSystemInfo systemInfo = pccSystemInfoDao.read();
        String encryptedPassword = encryptor.encrypt(password, systemInfo.getSecretKey());

        // ユーザ作成
        User newUser = new User();
        newUser.setUsername(userName);
        newUser.setPassword(encryptedPassword);
        newUser.setMasterUser(masterUserNo);
        newUser.setPowerUser(false);
        userDao.create(newUser);

        // AUTHORITY_SET取得
        List<AuthoritySet> authoritySets = authoritySetDao.readAll();
        Map<Long, AuthoritySet> authSetMap = new HashMap<Long, AuthoritySet>();
        for (AuthoritySet authoritySet : authoritySets) {
            authSetMap.put(authoritySet.getSetNo(), authoritySet);
        }

        // USER_AUTH作成
        for (Long farmNo : authMap.keySet()) {
            Long setNo = authMap.get(farmNo);
            AuthoritySet authoritySet = authSetMap.get(setNo);

            UserAuth userAuth = new UserAuth();
            userAuth.setFarmNo(farmNo);
            userAuth.setUserNo(newUser.getUserNo());

            if (Long.valueOf(0L).equals(setNo)) {
                //SET_NAME →「無し」
                userAuth.setFarmUse(false);
                userAuth.setServerMake(false);
                userAuth.setServerDelete(false);
                userAuth.setServerOperate(false);
                userAuth.setServiceMake(false);
                userAuth.setServiceDelete(false);
                userAuth.setServiceOperate(false);
                userAuth.setLbMake(false);
                userAuth.setLbDelete(false);
                userAuth.setLbOperate(false);
            } else if (authoritySet != null) {
                userAuth.setFarmUse(authoritySet.getFarmUse());
                userAuth.setServerMake(authoritySet.getServerMake());
                userAuth.setServerDelete(authoritySet.getServerDelete());
                userAuth.setServerOperate(authoritySet.getServerOperate());
                userAuth.setServiceMake(authoritySet.getServiceMake());
                userAuth.setServiceDelete(authoritySet.getServiceDelete());
                userAuth.setServiceOperate(authoritySet.getServiceOperate());
                userAuth.setLbMake(authoritySet.getLbMake());
                userAuth.setLbDelete(authoritySet.getLbDelete());
                userAuth.setLbOperate(authoritySet.getLbOperate());
            }

            //ユーザ権限作成
            userAuthDao.create(userAuth);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUserAndUserAuth(Long userNo, String userName, String password, Map<Long, Long> authMap) {
        // ユーザ情報取得
        User user = userDao.read(userNo);
        if (user == null) {
            // ユーザが存在しない場合
            throw new AutoApplicationException("ESERVICE-000104", userNo);
        }

        if (!StringUtils.equals(userName, user.getUsername())) {
            // ユーザ名の変更があった場合
            User sameNameUer = userDao.readByUsername(userName);
            if (sameNameUer != null && !sameNameUer.getUserNo().equals(userNo)) {
                // 同じユーザ名のユーザが存在する場合
                throw new AutoApplicationException("ESERVICE-000105", userName);
            }
        }

        // パスワードを暗号化
        PasswordEncryptor encryptor = new PasswordEncryptor();
        PccSystemInfo systemInfo = pccSystemInfoDao.read();
        String encryptedPassword = encryptor.encrypt(password, systemInfo.getSecretKey());

        // ユーザ更新
        user.setUsername(userName);
        user.setPassword(encryptedPassword);
        userDao.update(user);

        // AUTHORITY_SET取得
        List<AuthoritySet> authoritySets = authoritySetDao.readAll();
        Map<Long, AuthoritySet> authSetMap = new HashMap<Long, AuthoritySet>();
        for (AuthoritySet authoritySet : authoritySets) {
            authSetMap.put(authoritySet.getSetNo(), authoritySet);
        }

        // USER_AUTH更新
        for (Long farmNo : authMap.keySet()) {
            boolean isCreate = false;

            UserAuth userAuth = userAuthDao.read(farmNo, userNo);
            if (userAuth == null) {
                isCreate = true;
                userAuth = new UserAuth();
                userAuth.setFarmNo(farmNo);
                userAuth.setUserNo(userNo);
            }

            Long setNo = authMap.get(farmNo);
            AuthoritySet authoritySet = authSetMap.get(setNo);

            if (Long.valueOf(0L).equals(setNo)) {
                //SET_NAME →「権限無し」
                userAuth.setFarmUse(false);
                userAuth.setServerMake(false);
                userAuth.setServerDelete(false);
                userAuth.setServerOperate(false);
                userAuth.setServiceMake(false);
                userAuth.setServiceDelete(false);
                userAuth.setServiceOperate(false);
                userAuth.setLbMake(false);
                userAuth.setLbDelete(false);
                userAuth.setLbOperate(false);
            } else if (authoritySet != null) {
                userAuth.setFarmUse(authoritySet.getFarmUse());
                userAuth.setServerMake(authoritySet.getServerMake());
                userAuth.setServerDelete(authoritySet.getServerDelete());
                userAuth.setServerOperate(authoritySet.getServerOperate());
                userAuth.setServiceMake(authoritySet.getServiceMake());
                userAuth.setServiceDelete(authoritySet.getServiceDelete());
                userAuth.setServiceOperate(authoritySet.getServiceOperate());
                userAuth.setLbMake(authoritySet.getLbMake());
                userAuth.setLbDelete(authoritySet.getLbDelete());
                userAuth.setLbOperate(authoritySet.getLbOperate());
            }

            if (isCreate) {
                // 作成
                // この分岐に来るのは、ユーザ作成後に新たにmyCloud(ファーム)が作成された場合
                userAuthDao.create(userAuth);
            } else {
                // 更新
                userAuthDao.update(userAuth);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUserAuth(Long farmNo, Long userNo, UserAuthDto userAuthDto) {
        boolean isCreate = false;

        UserAuth userAuth = userAuthDao.read(farmNo, userNo);
        if (userAuth == null) {
            isCreate = true;
            userAuth = new UserAuth();
            userAuth.setFarmNo(farmNo);
            userAuth.setUserNo(userNo);
        }

        userAuth.setFarmUse(userAuthDto.isFarmUse());
        userAuth.setServerMake(userAuthDto.isServerMake());
        userAuth.setServerDelete(userAuthDto.isServerDelete());
        userAuth.setServerOperate(userAuthDto.isServerOperate());
        userAuth.setServiceMake(userAuthDto.isServiceMake());
        userAuth.setServiceDelete(userAuthDto.isServiceDelete());
        userAuth.setServiceOperate(userAuthDto.isServiceOperate());
        userAuth.setLbMake(userAuthDto.isLbMake());
        userAuth.setLbDelete(userAuthDto.isLbDelete());
        userAuth.setLbOperate(userAuthDto.isLbOperate());

        if (isCreate) {
            // 作成
            // この分岐に来るのは、ユーザ作成後に新たにmyCloud(ファーム)が作成された場合
            userAuthDao.create(userAuth);
        } else {
            // 更新
            userAuthDao.update(userAuth);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUser(Long userNo) {
        // ユーザ情報取得
        User user = userDao.read(userNo);
        if (user == null) {
            // ユーザが存在しない場合
            throw new AutoApplicationException("ESERVICE-000104", userNo);
        }

        List<UserAuth> userAuths = userAuthDao.readByUserNo(userNo);
        for (UserAuth userAuth : userAuths) {
            userAuthDao.deleteByFarmNoAndUserNo(userAuth.getFarmNo(), userAuth.getUserNo());
        }

        apiCertificateDao.deleteByUserNo(userNo);
        userDao.deleteByUserNo(userNo);
    }

}
