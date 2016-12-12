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
 * ZABBIX_DATAに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseZabbixData implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** INSTANCE_NO [BIGINT(19,0)] */
    private Long instanceNo;

    /** HOSTID [VARCHAR(20,0)] */
    private String hostid;

    /** IDLE_TIME [BIGINT(19,0)] */
    private Long idleTime;

    /** FIRST_CLOCK [BIGINT(19,0)] */
    private Long firstClock;

    /** LAST_CLOCK [BIGINT(19,0)] */
    private Long lastClock;

    /** CONTINUE_CLOCK [BIGINT(19,0)] */
    private Long continueClock;

    /** ALART [BIGINT(19,0)] */
    private Long alart;

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
     * idleTimeを取得します。
     *
     * @return idleTime
     */
    public Long getIdleTime() {
        return idleTime;
    }

    /**
     * idleTimeを設定します。
     *
     * @param idleTime idleTime
     */
    public void setIdleTime(Long idleTime) {
        this.idleTime = idleTime;
    }

    /**
     * firstClockを取得します。
     *
     * @return firstClock
     */
    public Long getFirstClock() {
        return firstClock;
    }

    /**
     * firstClockを設定します。
     *
     * @param firstClock firstClock
     */
    public void setFirstClock(Long firstClock) {
        this.firstClock = firstClock;
    }

    /**
     * lastClockを取得します。
     *
     * @return lastClock
     */
    public Long getLastClock() {
        return lastClock;
    }

    /**
     * lastClockを設定します。
     *
     * @param lastClock lastClock
     */
    public void setLastClock(Long lastClock) {
        this.lastClock = lastClock;
    }

    /**
     * continueClockを取得します。
     *
     * @return continueClock
     */
    public Long getContinueClock() {
        return continueClock;
    }

    /**
     * continueClockを設定します。
     *
     * @param continueClock continueClock
     */
    public void setContinueClock(Long continueClock) {
        this.continueClock = continueClock;
    }

    /**
     * alartを取得します。
     *
     * @return alart
     */
    public Long getAlart() {
        return alart;
    }

    /**
     * alartを設定します。
     *
     * @param alart alart
     */
    public void setAlart(Long alart) {
        this.alart = alart;
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
        result = prime * result + ((idleTime == null) ? 0 : idleTime.hashCode());
        result = prime * result + ((firstClock == null) ? 0 : firstClock.hashCode());
        result = prime * result + ((lastClock == null) ? 0 : lastClock.hashCode());
        result = prime * result + ((continueClock == null) ? 0 : continueClock.hashCode());
        result = prime * result + ((alart == null) ? 0 : alart.hashCode());

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

        final BaseZabbixData other = (BaseZabbixData) obj;
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
        if (idleTime == null) {
            if (other.idleTime != null) { return false; }
        } else if (!idleTime.equals(other.idleTime)) {
            return false;
        }
        if (firstClock == null) {
            if (other.firstClock != null) { return false; }
        } else if (!firstClock.equals(other.firstClock)) {
            return false;
        }
        if (lastClock == null) {
            if (other.lastClock != null) { return false; }
        } else if (!lastClock.equals(other.lastClock)) {
            return false;
        }
        if (continueClock == null) {
            if (other.continueClock != null) { return false; }
        } else if (!continueClock.equals(other.continueClock)) {
            return false;
        }
        if (alart == null) {
            if (other.alart != null) { return false; }
        } else if (!alart.equals(other.alart)) {
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
        sb.append("ZabbixData").append(" [");
        sb.append("instanceNo=").append(instanceNo).append(", ");
        sb.append("hostid=").append(hostid).append(", ");
        sb.append("idleTime=").append(idleTime).append(", ");
        sb.append("firstClock=").append(firstClock).append(", ");
        sb.append("lastClock=").append(lastClock).append(", ");
        sb.append("continueClock=").append(continueClock).append(", ");
        sb.append("alart=").append(alart);
        sb.append("]");
        return sb.toString();
    }

}
