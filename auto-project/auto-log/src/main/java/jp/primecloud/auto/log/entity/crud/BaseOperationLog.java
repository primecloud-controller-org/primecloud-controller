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
package jp.primecloud.auto.log.entity.crud;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * operation_logに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseOperationLog implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** O_LOG_NO [BIGINT(19,0)] */
    private Long OLogNo;

    /** O_LOG_DATE [DATETIME(0,0)] */
    private Date OLogDate;

    /** USER_NO [BIGINT(19,0)] */
    private Long userNo;

    /** USER_NAME [VARCHAR(100,0)] */
    private String userName;

    /** SCREEN [VARCHAR(100,0)] */
    private String screen;

    /** OPERATION [VARCHAR(100,0)] */
    private String operation;

    /** FARM_NO [BIGINT(19,0)] */
    private Long farmNo;

    /** INSTANCE_NO [BIGINT(19,0)] */
    private Long instanceNo;

    /** COMPONENT_NO [BIGINT(19,0)] */
    private Long componentNo;

    /** LOAD_BALANCER_NO [BIGINT(19,0)] */
    private Long loadBalancerNo;

    /** MEMO [VARCHAR(300,0)] */
    private String memo;

    /**
     * OLogNoを取得します。
     *
     * @return OLogNo
     */
    public Long getOLogNo() {
        return OLogNo;
    }

    /**
     * OLogNoを設定します。
     *
     * @param OLogNo OLogNo
     */
    public void setOLogNo(Long OLogNo) {
        this.OLogNo = OLogNo;
    }

    /**
     * OLogDateを取得します。
     *
     * @return OLogDate
     */
    public Date getOLogDate() {
        return OLogDate;
    }

    /**
     * OLogDateを設定します。
     *
     * @param OLogDate OLogDate
     */
    public void setOLogDate(Date OLogDate) {
        this.OLogDate = OLogDate;
    }

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
     * userNameを取得します。
     *
     * @return userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * userNameを設定します。
     *
     * @param userName userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * screenを取得します。
     *
     * @return screen
     */
    public String getScreen() {
        return screen;
    }

    /**
     * screenを設定します。
     *
     * @param screen screen
     */
    public void setScreen(String screen) {
        this.screen = screen;
    }

    /**
     * operationを取得します。
     *
     * @return operation
     */
    public String getOperation() {
        return operation;
    }

    /**
     * operationを設定します。
     *
     * @param operation operation
     */
    public void setOperation(String operation) {
        this.operation = operation;
    }

    /**
     * farmNoを取得します。
     *
     * @return farmNo
     */
    public Long getFarmNo() {
        return farmNo;
    }

    /**
     * farmNoを設定します。
     *
     * @param farmNo farmNo
     */
    public void setFarmNo(Long farmNo) {
        this.farmNo = farmNo;
    }

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
     * componentNoを取得します。
     *
     * @return componentNo
     */
    public Long getComponentNo() {
        return componentNo;
    }

    /**
     * componentNoを設定します。
     *
     * @param componentNo componentNo
     */
    public void setComponentNo(Long componentNo) {
        this.componentNo = componentNo;
    }

    /**
     * loadBalancerNoを取得します。
     *
     * @return loadBalancerNo
     */
    public Long getLoadBalancerNo() {
        return loadBalancerNo;
    }

    /**
     * loadBalancerNoを設定します。
     *
     * @param loadBalancerNo loadBalancerNo
     */
    public void setLoadBalancerNo(Long loadBalancerNo) {
        this.loadBalancerNo = loadBalancerNo;
    }

    /**
     * memoを取得します。
     *
     * @return memo
     */
    public String getMemo() {
        return memo;
    }

    /**
     * memoを設定します。
     *
     * @param memo memo
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((OLogNo == null) ? 0 : OLogNo.hashCode());
        result = prime * result + ((OLogDate == null) ? 0 : OLogDate.hashCode());
        result = prime * result + ((userNo == null) ? 0 : userNo.hashCode());
        result = prime * result + ((userName == null) ? 0 : userName.hashCode());
        result = prime * result + ((screen == null) ? 0 : screen.hashCode());
        result = prime * result + ((operation == null) ? 0 : operation.hashCode());
        result = prime * result + ((farmNo == null) ? 0 : farmNo.hashCode());
        result = prime * result + ((instanceNo == null) ? 0 : instanceNo.hashCode());
        result = prime * result + ((componentNo == null) ? 0 : componentNo.hashCode());
        result = prime * result + ((loadBalancerNo == null) ? 0 : loadBalancerNo.hashCode());
        result = prime * result + ((memo == null) ? 0 : memo.hashCode());

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

        final BaseOperationLog other = (BaseOperationLog) obj;
        if (OLogNo == null) {
            if (other.OLogNo != null) { return false; }
        } else if (!OLogNo.equals(other.OLogNo)) {
            return false;
        }
        if (OLogDate == null) {
            if (other.OLogDate != null) { return false; }
        } else if (!OLogDate.equals(other.OLogDate)) {
            return false;
        }
        if (userNo == null) {
            if (other.userNo != null) { return false; }
        } else if (!userNo.equals(other.userNo)) {
            return false;
        }
        if (userName == null) {
            if (other.userName != null) { return false; }
        } else if (!userName.equals(other.userName)) {
            return false;
        }
        if (screen == null) {
            if (other.screen != null) { return false; }
        } else if (!screen.equals(other.screen)) {
            return false;
        }
        if (operation == null) {
            if (other.operation != null) { return false; }
        } else if (!operation.equals(other.operation)) {
            return false;
        }
        if (farmNo == null) {
            if (other.farmNo != null) { return false; }
        } else if (!farmNo.equals(other.farmNo)) {
            return false;
        }
        if (instanceNo == null) {
            if (other.instanceNo != null) { return false; }
        } else if (!instanceNo.equals(other.instanceNo)) {
            return false;
        }
        if (componentNo == null) {
            if (other.componentNo != null) { return false; }
        } else if (!componentNo.equals(other.componentNo)) {
            return false;
        }
        if (loadBalancerNo == null) {
            if (other.loadBalancerNo != null) { return false; }
        } else if (!loadBalancerNo.equals(other.loadBalancerNo)) {
            return false;
        }
        if (memo == null) {
            if (other.memo != null) { return false; }
        } else if (!memo.equals(other.memo)) {
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
        sb.append("OperationLog").append(" [");
        sb.append("OLogNo=").append(OLogNo).append(", ");
        sb.append("OLogDate=").append(OLogDate).append(", ");
        sb.append("userNo=").append(userNo).append(", ");
        sb.append("userName=").append(userName).append(", ");
        sb.append("screen=").append(screen).append(", ");
        sb.append("operation=").append(operation).append(", ");
        sb.append("farmNo=").append(farmNo).append(", ");
        sb.append("instanceNo=").append(instanceNo).append(", ");
        sb.append("componentNo=").append(componentNo).append(", ");
        sb.append("loadBalancerNo=").append(loadBalancerNo).append(", ");
        sb.append("memo=").append(memo);
        sb.append("]");
        return sb.toString();
    }

}
