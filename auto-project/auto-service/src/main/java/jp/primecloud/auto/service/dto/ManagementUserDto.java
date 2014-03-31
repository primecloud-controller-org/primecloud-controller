package jp.primecloud.auto.service.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.primecloud.auto.entity.crud.User;
import jp.primecloud.auto.entity.crud.UserAuth;


public class ManagementUserDto implements Serializable {


    /** TODO: フィールドコメントを記述 */
    private static final long serialVersionUID = 3636683948507828492L;


    private User user;

    private HashMap<Long, UserAuth> authMap;



    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public HashMap<Long, UserAuth> getAuthMap() {
        return authMap;
    }

    public void setAuthMap(HashMap<Long, UserAuth> authMap) {
        this.authMap = authMap;
    }

}
