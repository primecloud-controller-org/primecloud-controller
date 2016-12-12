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
 * EVENT_LOGに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseEventLog implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** LOG_NO [BIGINT(19,0)] */
    private Long logNo;

    /** LOG_DATE [DATETIME(0,0)] */
    private Date logDate;

    /** LOG_LEVEL [SMALLINT(5,0)] */
    private Integer logLevel;

    /** USER_NO [BIGINT(19,0)] */
    private Long userNo;

    /** USER_NAME [VARCHAR(100,0)] */
    private String userName;

    /** FARM_NO [BIGINT(19,0)] */
    private Long farmNo;

    /** FARM_NAME [VARCHAR(100,0)] */
    private String farmName;

    /** COMPONENT_NO [BIGINT(19,0)] */
    private Long componentNo;

    /** COMPONENT_NAME [VARCHAR(100,0)] */
    private String componentName;

    /** INSTANCE_NO [BIGINT(19,0)] */
    private Long instanceNo;

    /** INSTANCE_NAME [VARCHAR(100,0)] */
    private String instanceName;

    /** MESSAGE_CODE [VARCHAR(100,0)] */
    private String messageCode;

    /** MESSAGE [VARCHAR(1,000,0)] */
    private String message;

    /** INSTANCE_TYPE [VARCHAR(30,0)] */
    private String instanceType;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /**
     * logNoを取得します。
     *
     * @return logNo
     */
    public Long getLogNo() {
        return logNo;
    }

    /**
     * logNoを設定します。
     *
     * @param logNo logNo
     */
    public void setLogNo(Long logNo) {
        this.logNo = logNo;
    }

    /**
     * logDateを取得します。
     *
     * @return logDate
     */
    public Date getLogDate() {
        return logDate;
    }

    /**
     * logDateを設定します。
     *
     * @param logDate logDate
     */
    public void setLogDate(Date logDate) {
        this.logDate = logDate;
    }

    /**
     * logLevelを取得します。
     *
     * @return logLevel
     */
    public Integer getLogLevel() {
        return logLevel;
    }

    /**
     * logLevelを設定します。
     *
     * @param logLevel logLevel
     */
    public void setLogLevel(Integer logLevel) {
        this.logLevel = logLevel;
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
     * farmNameを取得します。
     *
     * @return farmName
     */
    public String getFarmName() {
        return farmName;
    }

    /**
     * farmNameを設定します。
     *
     * @param farmName farmName
     */
    public void setFarmName(String farmName) {
        this.farmName = farmName;
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
     * componentNameを取得します。
     *
     * @return componentName
     */
    public String getComponentName() {
        return componentName;
    }

    /**
     * componentNameを設定します。
     *
     * @param componentName componentName
     */
    public void setComponentName(String componentName) {
        this.componentName = componentName;
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
     * instanceNameを取得します。
     *
     * @return instanceName
     */
    public String getInstanceName() {
        return instanceName;
    }

    /**
     * instanceNameを設定します。
     *
     * @param instanceName instanceName
     */
    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    /**
     * messageCodeを取得します。
     *
     * @return messageCode
     */
    public String getMessageCode() {
        return messageCode;
    }

    /**
     * messageCodeを設定します。
     *
     * @param messageCode messageCode
     */
    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    /**
     * messageを取得します。
     *
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * messageを設定します。
     *
     * @param message message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * instanceTypeを取得します。
     *
     * @return instanceType
     */
    public String getInstanceType() {
        return instanceType;
    }

    /**
     * instanceTypeを設定します。
     *
     * @param instanceType instanceType
     */
    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    /**
     * platformNoを取得します。
     *
     * @return platformNo
     */
    public Long getPlatformNo() {
        return platformNo;
    }

    /**
     * platformNoを設定します。
     *
     * @param platformNo platformNo
     */
    public void setPlatformNo(Long platformNo) {
        this.platformNo = platformNo;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((logNo == null) ? 0 : logNo.hashCode());
        result = prime * result + ((logDate == null) ? 0 : logDate.hashCode());
        result = prime * result + ((logLevel == null) ? 0 : logLevel.hashCode());
        result = prime * result + ((userNo == null) ? 0 : userNo.hashCode());
        result = prime * result + ((userName == null) ? 0 : userName.hashCode());
        result = prime * result + ((farmNo == null) ? 0 : farmNo.hashCode());
        result = prime * result + ((farmName == null) ? 0 : farmName.hashCode());
        result = prime * result + ((componentNo == null) ? 0 : componentNo.hashCode());
        result = prime * result + ((componentName == null) ? 0 : componentName.hashCode());
        result = prime * result + ((instanceNo == null) ? 0 : instanceNo.hashCode());
        result = prime * result + ((instanceName == null) ? 0 : instanceName.hashCode());
        result = prime * result + ((messageCode == null) ? 0 : messageCode.hashCode());
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + ((instanceType == null) ? 0 : instanceType.hashCode());
        result = prime * result + ((platformNo == null) ? 0 : platformNo.hashCode());

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

        final BaseEventLog other = (BaseEventLog) obj;
        if (logNo == null) {
            if (other.logNo != null) { return false; }
        } else if (!logNo.equals(other.logNo)) {
            return false;
        }
        if (logDate == null) {
            if (other.logDate != null) { return false; }
        } else if (!logDate.equals(other.logDate)) {
            return false;
        }
        if (logLevel == null) {
            if (other.logLevel != null) { return false; }
        } else if (!logLevel.equals(other.logLevel)) {
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
        if (farmNo == null) {
            if (other.farmNo != null) { return false; }
        } else if (!farmNo.equals(other.farmNo)) {
            return false;
        }
        if (farmName == null) {
            if (other.farmName != null) { return false; }
        } else if (!farmName.equals(other.farmName)) {
            return false;
        }
        if (componentNo == null) {
            if (other.componentNo != null) { return false; }
        } else if (!componentNo.equals(other.componentNo)) {
            return false;
        }
        if (componentName == null) {
            if (other.componentName != null) { return false; }
        } else if (!componentName.equals(other.componentName)) {
            return false;
        }
        if (instanceNo == null) {
            if (other.instanceNo != null) { return false; }
        } else if (!instanceNo.equals(other.instanceNo)) {
            return false;
        }
        if (instanceName == null) {
            if (other.instanceName != null) { return false; }
        } else if (!instanceName.equals(other.instanceName)) {
            return false;
        }
        if (messageCode == null) {
            if (other.messageCode != null) { return false; }
        } else if (!messageCode.equals(other.messageCode)) {
            return false;
        }
        if (message == null) {
            if (other.message != null) { return false; }
        } else if (!message.equals(other.message)) {
            return false;
        }
        if (instanceType == null) {
            if (other.instanceType != null) { return false; }
        } else if (!instanceType.equals(other.instanceType)) {
            return false;
        }
        if (platformNo == null) {
            if (other.platformNo != null) { return false; }
        } else if (!platformNo.equals(other.platformNo)) {
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
        sb.append("EventLog").append(" [");
        sb.append("logNo=").append(logNo).append(", ");
        sb.append("logDate=").append(logDate).append(", ");
        sb.append("logLevel=").append(logLevel).append(", ");
        sb.append("userNo=").append(userNo).append(", ");
        sb.append("userName=").append(userName).append(", ");
        sb.append("farmNo=").append(farmNo).append(", ");
        sb.append("farmName=").append(farmName).append(", ");
        sb.append("componentNo=").append(componentNo).append(", ");
        sb.append("componentName=").append(componentName).append(", ");
        sb.append("instanceNo=").append(instanceNo).append(", ");
        sb.append("instanceName=").append(instanceName).append(", ");
        sb.append("messageCode=").append(messageCode).append(", ");
        sb.append("message=").append(message).append(", ");
        sb.append("instanceType=").append(instanceType).append(", ");
        sb.append("platformNo=").append(platformNo);
        sb.append("]");
        return sb.toString();
    }

}
