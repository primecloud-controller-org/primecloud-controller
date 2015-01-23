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
package jp.primecloud.auto.zabbix.model.proxy;

import java.io.Serializable;

/**
 * <p>
 * Proxyのエンティティクラスです。
 * </p>
 *
 */
public class Proxy implements Serializable {

    private static final long serialVersionUID = 1L;

    private String proxyid;

    private String host;

    private Integer status;

    /**
     * proxyidを取得します。
     *
     * @return proxyid
     */
    public String getProxyid() {
        return proxyid;
    }

    /**
     * proxyidを設定します。
     *
     * @param proxyid proxyid
     */
    public void setProxyid(String proxyid) {
        this.proxyid = proxyid;
    }

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
     * statusを取得します。
     *
     * @return status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * statusを設定します。
     *
     * @param status status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }
}
