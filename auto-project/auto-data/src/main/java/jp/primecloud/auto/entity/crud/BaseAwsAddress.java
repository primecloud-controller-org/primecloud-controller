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
 * AWS_ADDRESSに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseAwsAddress implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** ADDRESS_NO [BIGINT(19,0)] */
    private Long addressNo;

    /** USER_NO [BIGINT(19,0)] */
    private Long userNo;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** PUBLIC_IP [VARCHAR(100,0)] */
    private String publicIp;

    /** COMMENT [VARCHAR(100,0)] */
    private String comment;

    /** INSTANCE_NO [BIGINT(19,0)] */
    private Long instanceNo;

    /** INSTANCE_ID [VARCHAR(20,0)] */
    private String instanceId;

    /**
     * addressNoを取得します。
     *
     * @return addressNo
     */
    public Long getAddressNo() {
        return addressNo;
    }

    /**
     * addressNoを設定します。
     *
     * @param addressNo addressNo
     */
    public void setAddressNo(Long addressNo) {
        this.addressNo = addressNo;
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
     * publicIpを取得します。
     *
     * @return publicIp
     */
    public String getPublicIp() {
        return publicIp;
    }

    /**
     * publicIpを設定します。
     *
     * @param publicIp publicIp
     */
    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    /**
     * commentを取得します。
     *
     * @return comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * commentを設定します。
     *
     * @param comment comment
     */
    public void setComment(String comment) {
        this.comment = comment;
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
     * instanceIdを取得します。
     *
     * @return instanceId
     */
    public String getInstanceId() {
        return instanceId;
    }

    /**
     * instanceIdを設定します。
     *
     * @param instanceId instanceId
     */
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((addressNo == null) ? 0 : addressNo.hashCode());
        result = prime * result + ((userNo == null) ? 0 : userNo.hashCode());
        result = prime * result + ((platformNo == null) ? 0 : platformNo.hashCode());
        result = prime * result + ((publicIp == null) ? 0 : publicIp.hashCode());
        result = prime * result + ((comment == null) ? 0 : comment.hashCode());
        result = prime * result + ((instanceNo == null) ? 0 : instanceNo.hashCode());
        result = prime * result + ((instanceId == null) ? 0 : instanceId.hashCode());

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

        final BaseAwsAddress other = (BaseAwsAddress) obj;
        if (addressNo == null) {
            if (other.addressNo != null) { return false; }
        } else if (!addressNo.equals(other.addressNo)) {
            return false;
        }
        if (userNo == null) {
            if (other.userNo != null) { return false; }
        } else if (!userNo.equals(other.userNo)) {
            return false;
        }
        if (platformNo == null) {
            if (other.platformNo != null) { return false; }
        } else if (!platformNo.equals(other.platformNo)) {
            return false;
        }
        if (publicIp == null) {
            if (other.publicIp != null) { return false; }
        } else if (!publicIp.equals(other.publicIp)) {
            return false;
        }
        if (comment == null) {
            if (other.comment != null) { return false; }
        } else if (!comment.equals(other.comment)) {
            return false;
        }
        if (instanceNo == null) {
            if (other.instanceNo != null) { return false; }
        } else if (!instanceNo.equals(other.instanceNo)) {
            return false;
        }
        if (instanceId == null) {
            if (other.instanceId != null) { return false; }
        } else if (!instanceId.equals(other.instanceId)) {
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
        sb.append("AwsAddress").append(" [");
        sb.append("addressNo=").append(addressNo).append(", ");
        sb.append("userNo=").append(userNo).append(", ");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("publicIp=").append(publicIp).append(", ");
        sb.append("comment=").append(comment).append(", ");
        sb.append("instanceNo=").append(instanceNo).append(", ");
        sb.append("instanceId=").append(instanceId);
        sb.append("]");
        return sb.toString();
    }

}
