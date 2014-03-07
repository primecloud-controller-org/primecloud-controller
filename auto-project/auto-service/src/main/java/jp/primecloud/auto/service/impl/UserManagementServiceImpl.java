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



public class UserManagementServiceImpl extends ServiceSupport implements UserManagementService {

    @Override
    public List<ManagementUserDto> getManagementUsers(Long userNo) {

        List<ManagementUserDto> mUsers = new ArrayList<ManagementUserDto>();

        List<User> users = userDao.readByMasterUser(userNo);

        for (User user:users) {
            ManagementUserDto mUser = new ManagementUserDto();
            HashMap<Long, UserAuth> authMap = new HashMap<Long, UserAuth>();
            List<UserAuth> userAuth = userAuthDao.readByUserNo(user.getUserNo());
            for (UserAuth auth: userAuth) {
                authMap.put(auth.getFarmNo(), auth);
            }

            mUser.setUser(user);
            mUser.setAuthMap(authMap);

            mUsers.add(mUser);
        }

        return mUsers;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FarmDto> getFarms(Long masterUserNo) {
        //戻り値用
        List<FarmDto> dtos = new ArrayList<FarmDto>();

        //マスターユーザに紐づくファームを取得
        List<Farm> farms = farmDao.readByUserNo(masterUserNo);
        for (Farm farm: farms) {
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
        for (UserAuth userAuth: userAuths) {
            authMap.put(userAuth.getFarmNo(), userAuth);
        }
        return authMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createUserAndUserAuth(Long masterUserNo, String userName,
            String password, Map<Long, Long> authMap) {
        final Long SET_NO_NOTHING = new Long(0);

        //ユーザ情報取得
        User user = userDao.readByUsername(userName);
        if (user != null) {
            //同じユーザ名のユーザが存在する場合
            throw new AutoApplicationException("ESERVICE-000105", userName);
        }

        PasswordEncryptor passEncrypt = new PasswordEncryptor();
        //PCCシステム情報取得
        PccSystemInfo systemInfo = pccSystemInfoDao.read();

        //ユーザ作成
        User newUser = new User();
        newUser.setUsername(userName);
        newUser.setPassword(passEncrypt.encrypt(password, systemInfo.getSecretKey()));
        newUser.setMasterUser(masterUserNo);
        newUser.setPowerUser(false);
        userDao.create(newUser);

        //AUTHORITY_SET取得
        List<AuthoritySet> authoritySets = authoritySetDao.readAll();
        Map<Long, AuthoritySet> authSetMap = new HashMap<Long, AuthoritySet>();
        for (AuthoritySet authoritySet: authoritySets) {
            authSetMap.put(authoritySet.getSetNo(), authoritySet);
        }

        //USER_AUTH作成
        for (Long farmNo: authMap.keySet()) {
            Long setNo = authMap.get(farmNo);
            AuthoritySet authoritySet = authSetMap.get(setNo);

            UserAuth userAuth = new UserAuth();
            userAuth.setFarmNo(farmNo);
            userAuth.setUserNo(newUser.getUserNo());
            if (SET_NO_NOTHING.equals(setNo)) {
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
    public void updateUserAndUserAuth(Long userNo, String userName,
            String password, Map<Long, Long> authMap) {
        final Long SET_NO_NOTHING = new Long(0);

        //ユーザ情報取得
        User user = userDao.read(userNo);
        if (user == null) {
            //ユーザが存在しない場合
            throw new AutoApplicationException("ESERVICE-000104", userNo);
        }

        if (!user.getUsername().equals(userName)) {
            //ユーザ名の変更があった場合
            User sameNameUer = userDao.readByUsername(userName);
            if (sameNameUer != null && !sameNameUer.getUserNo().equals(userNo)) {
                //同じユーザ名のユーザが存在する場合
                throw new AutoApplicationException("ESERVICE-000105", userName);
            }
        }

        PasswordEncryptor passEncrypt = new PasswordEncryptor();
        //PCCシステム情報取得
        PccSystemInfo systemInfo = pccSystemInfoDao.read();

        //ユーザ更新
        user.setUsername(userName);
        user.setPassword(passEncrypt.encrypt(password, systemInfo.getSecretKey()));
        userDao.update(user);

        //AUTHORITY_SET取得
        List<AuthoritySet> authoritySets = authoritySetDao.readAll();
        Map<Long, AuthoritySet> authSetMap = new HashMap<Long, AuthoritySet>();
        for (AuthoritySet authoritySet: authoritySets) {
            authSetMap.put(authoritySet.getSetNo(), authoritySet);
        }

        //USER_AUTH更新
        for (Long farmNo: authMap.keySet()) {
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
            if (SET_NO_NOTHING.equals(setNo)) {
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
                //作成
                //この分岐に来るのは、ユーザ作成後に新たにmyCloud(ファーム)が作成された場合
                userAuthDao.create(userAuth);
            } else {
                //更新
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
            //作成
            //この分岐に来るのは、ユーザ作成後に新たにmyCloud(ファーム)が作成された場合
            userAuthDao.create(userAuth);
        } else {
            //更新
            userAuthDao.update(userAuth);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUser(Long userNo) {
        //ユーザ情報取得
        User user = userDao.read(userNo);
        if (user == null) {
            //ユーザが存在しない場合
            throw new AutoApplicationException("ESERVICE-000104", userNo);
        }

        //USER_AUTHテーブル削除
        List<UserAuth> userAuths = userAuthDao.readByUserNo(userNo);
        for (UserAuth userAuth: userAuths) {
            userAuthDao.deleteByFarmNoAndUserNo(userAuth.getFarmNo(), userAuth.getUserNo());
        }

        //API_CERTIFICATEテーブル削除
        apiCertificateDao.deleteByUserNo(userNo);

        //USERテーブル削除
        userDao.deleteByUserNo(userNo);
    }
}
