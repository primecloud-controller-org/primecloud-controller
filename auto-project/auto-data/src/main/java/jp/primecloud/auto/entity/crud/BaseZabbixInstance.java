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
 * ZABBIX_INSTANCEに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseZabbixInstance implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** INSTANCE_NO [BIGINT(19,0)] */
    private Long instanceNo;

    /** HOSTID [VARCHAR(20,0)] */
    private String hostid;

    /** STATUS [VARCHAR(20,0)] */
    private String status;

    /**
     * instanceNoを取得します。
     *
     * @return instanceNo
     */
    public Long getInstanceNo() {
        return instanceNo;
    }

    /**
     * instanceNoを設定します。
     *
     * @param instanceNo instanceNo
     */
    public void setInstanceNo(Long instanceNo) {
        this.instanceNo = instanceNo;
    }

    /**
     * hostidを取得します。
     *
     * @return hostid
     */
    public String getHostid() {
        return hostid;
    }

    /**
     * hostidを設定します。
     *
     * @param hostid hostid
     */
    public void setHostid(String hostid) {
        this.hostid = hostid;
    }

    /**
     * statusを取得します。
     *
     * @return status
     */
    public String getStatus() {
        return status;
    }

    /**
     * statusを設定します。
     *
     * @param status status
     */
    public void setStatus(String status) {
        this.status = status;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((instanceNo == null) ? 0 : instanceNo.hashCode());
        result = prime * result + ((hostid == null) ? 0 : hostid.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());

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

        final BaseZabbixInstance other = (BaseZabbixInstance) obj;
        if (instanceNo == null) {
            if (other.instanceNo != null) { return false; }
        } else if (!instanceNo.equals(other.instanceNo)) {
            return false;
        }
        if (hostid == null) {
            if (other.hostid != null) { return false; }
        } else if (!hostid.equals(other.hostid)) {
            return false;
        }
        if (status == null) {
            if (other.status != null) { return false; }
        } else if (!status.equals(other.status)) {
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
        sb.append("ZabbixInstance").append(" [");
        sb.append("instanceNo=").append(instanceNo).append(", ");
        sb.append("hostid=").append(hostid).append(", ");
        sb.append("status=").append(status);
        sb.append("]");
        return sb.toString();
    }

}
