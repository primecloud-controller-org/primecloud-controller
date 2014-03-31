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
 * PROXYに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseProxy implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** HOST [VARCHAR(500,0)] */
    private String host;

    /** PORT [INT(10,0)] */
    private Integer port;

    /** USER [VARCHAR(100,0)] */
    private String user;

    /** PASSWORD [VARCHAR(100,0)] */
    private String password;

    /**
     * hostを取得します。
     *
     * @return host
     */
    public String getHost() {
        return host;
    }

    /**
     * hostを設定します。
     *
     * @param host host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * portを取得します。
     *
     * @return port
     */
    public Integer getPort() {
        return port;
    }

    /**
     * portを設定します。
     *
     * @param port port
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * userを取得します。
     *
     * @return user
     */
    public String getUser() {
        return user;
    }

    /**
     * userを設定します。
     *
     * @param user user
     */
    public void setUser(String user) {
        this.user = user;
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
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + ((port == null) ? 0 : port.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());

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

        final BaseProxy other = (BaseProxy) obj;
        if (host == null) {
            if (other.host != null) { return false; }
        } else if (!host.equals(other.host)) {
            return false;
        }
        if (port == null) {
            if (other.port != null) { return false; }
        } else if (!port.equals(other.port)) {
            return false;
        }
        if (user == null) {
            if (other.user != null) { return false; }
        } else if (!user.equals(other.user)) {
            return false;
        }
        if (password == null) {
            if (other.password != null) { return false; }
        } else if (!password.equals(other.password)) {
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
        sb.append("Proxy").append(" [");
        sb.append("host=").append(host).append(", ");
        sb.append("port=").append(port).append(", ");
        sb.append("user=").append(user).append(", ");
        sb.append("password=").append(password);
        sb.append("]");
        return sb.toString();
    }

}
