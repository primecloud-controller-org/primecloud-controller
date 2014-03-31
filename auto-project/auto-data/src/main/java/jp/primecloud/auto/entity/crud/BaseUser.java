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
package jp.primecloud.auto.entity.crud;

import java.io.Serializable;

/**
 * <p>
 * userに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseUser implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** USER_NO [BIGINT(19,0)] */
    private Long userNo;

    /** USERNAME [VARCHAR(50,0)] */
    private String username;

    /** PASSWORD [VARCHAR(50,0)] */
    private String password;

    /** MASTER_USER [BIGINT(19,0)] */
    private Long masterUser;

    /** POWER_USER [BIT(0,0)] */
    private Boolean powerUser;

    /**
     * userNoを取得します。
     *
     * @return userNo
     */
    public Long getUserNo() {
        return userNo;
    }

    /**
     * userNoを設定します。
     *
     * @param userNo userNo
     */
    public void setUserNo(Long userNo) {
        this.userNo = userNo;
    }

    /**
     * usernameを取得します。
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * usernameを設定します。
     *
     * @param username username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * passwordを取得します。
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * passwordを設定します。
     *
     * @param password password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * masterUserを取得します。
     *
     * @return masterUser
     */
    public Long getMasterUser() {
        return masterUser;
    }

    /**
     * masterUserを設定します。
     *
     * @param masterUser masterUser
     */
    public void setMasterUser(Long masterUser) {
        this.masterUser = masterUser;
    }

    /**
     * powerUserを取得します。
     *
     * @return powerUser
     */
    public Boolean getPowerUser() {
        return powerUser;
    }

    /**
     * powerUserを設定します。
     *
     * @param powerUser powerUser
     */
    public void setPowerUser(Boolean powerUser) {
        this.powerUser = powerUser;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((userNo == null) ? 0 : userNo.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((masterUser == null) ? 0 : masterUser.hashCode());
        result = prime * result + ((powerUser == null) ? 0 : powerUser.hashCode());

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }

        final BaseUser other = (BaseUser) obj;
        if (userNo == null) {
            if (other.userNo != null) { return false; }
        } else if (!userNo.equals(other.userNo)) {
            return false;
        }
        if (username == null) {
            if (other.username != null) { return false; }
        } else if (!username.equals(other.username)) {
            return false;
        }
        if (password == null) {
            if (other.password != null) { return false; }
        } else if (!password.equals(other.password)) {
            return false;
        }
        if (masterUser == null) {
            if (other.masterUser != null) { return false; }
        } else if (!masterUser.equals(other.masterUser)) {
            return false;
        }
        if (powerUser == null) {
            if (other.powerUser != null) { return false; }
        } else if (!powerUser.equals(other.powerUser)) {
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("User").append(" [");
        sb.append("userNo=").append(userNo).append(", ");
        sb.append("username=").append(username).append(", ");
        sb.append("password=").append(password).append(", ");
        sb.append("masterUser=").append(masterUser).append(", ");
        sb.append("powerUser=").append(powerUser);
        sb.append("]");
        return sb.toString();
    }

}
