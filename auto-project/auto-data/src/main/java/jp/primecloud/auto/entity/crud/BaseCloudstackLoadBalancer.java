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
 * CLOUDSTACK_LOAD_BALANCERに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BaseCloudstackLoadBalancer implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** LOAD_BALANCER_NO [BIGINT(19,0)] */
    private Long loadBalancerNo;

    /** LOAD_BALANCER_ID [VARCHAR(20,0)] */
    private String loadBalancerId;

    /** ALGORITHM [VARCHAR(100,0)] */
    private String algorithm;

    /** DESCRIPTION [VARCHAR(100,0)] */
    private String description;

    /** NAME [VARCHAR(30,0)] */
    private String name;

    /** ADDRESS_ID [VARCHAR(20,0)] */
    private String addressId;

    /** PUBLICIP [VARCHAR(100,0)] */
    private String publicip;

    /** PUBLICPORT [VARCHAR(20,0)] */
    private String publicport;

    /** PRIVATEPORT [VARCHAR(20,0)] */
    private String privateport;

    /** STATE [VARCHAR(20,0)] */
    private String state;

    /** ZONEID [VARCHAR(100,0)] */
    private String zoneid;

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
     * loadBalancerIdを取得します。
     *
     * @return loadBalancerId
     */
    public String getLoadBalancerId() {
        return loadBalancerId;
    }

    /**
     * loadBalancerIdを設定します。
     *
     * @param loadBalancerId loadBalancerId
     */
    public void setLoadBalancerId(String loadBalancerId) {
        this.loadBalancerId = loadBalancerId;
    }

    /**
     * algorithmを取得します。
     *
     * @return algorithm
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * algorithmを設定します。
     *
     * @param algorithm algorithm
     */
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * descriptionを取得します。
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * descriptionを設定します。
     *
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * nameを取得します。
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * nameを設定します。
     *
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * addressIdを取得します。
     *
     * @return addressId
     */
    public String getAddressId() {
        return addressId;
    }

    /**
     * addressIdを設定します。
     *
     * @param addressId addressId
     */
    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    /**
     * publicipを取得します。
     *
     * @return publicip
     */
    public String getPublicip() {
        return publicip;
    }

    /**
     * publicipを設定します。
     *
     * @param publicip publicip
     */
    public void setPublicip(String publicip) {
        this.publicip = publicip;
    }

    /**
     * publicportを取得します。
     *
     * @return publicport
     */
    public String getPublicport() {
        return publicport;
    }

    /**
     * publicportを設定します。
     *
     * @param publicport publicport
     */
    public void setPublicport(String publicport) {
        this.publicport = publicport;
    }

    /**
     * privateportを取得します。
     *
     * @return privateport
     */
    public String getPrivateport() {
        return privateport;
    }

    /**
     * privateportを設定します。
     *
     * @param privateport privateport
     */
    public void setPrivateport(String privateport) {
        this.privateport = privateport;
    }

    /**
     * stateを取得します。
     *
     * @return state
     */
    public String getState() {
        return state;
    }

    /**
     * stateを設定します。
     *
     * @param state state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * zoneidを取得します。
     *
     * @return zoneid
     */
    public String getZoneid() {
        return zoneid;
    }

    /**
     * zoneidを設定します。
     *
     * @param zoneid zoneid
     */
    public void setZoneid(String zoneid) {
        this.zoneid = zoneid;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((loadBalancerNo == null) ? 0 : loadBalancerNo.hashCode());
        result = prime * result + ((loadBalancerId == null) ? 0 : loadBalancerId.hashCode());
        result = prime * result + ((algorithm == null) ? 0 : algorithm.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((addressId == null) ? 0 : addressId.hashCode());
        result = prime * result + ((publicip == null) ? 0 : publicip.hashCode());
        result = prime * result + ((publicport == null) ? 0 : publicport.hashCode());
        result = prime * result + ((privateport == null) ? 0 : privateport.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        result = prime * result + ((zoneid == null) ? 0 : zoneid.hashCode());

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

        final BaseCloudstackLoadBalancer other = (BaseCloudstackLoadBalancer) obj;
        if (loadBalancerNo == null) {
            if (other.loadBalancerNo != null) { return false; }
        } else if (!loadBalancerNo.equals(other.loadBalancerNo)) {
            return false;
        }
        if (loadBalancerId == null) {
            if (other.loadBalancerId != null) { return false; }
        } else if (!loadBalancerId.equals(other.loadBalancerId)) {
            return false;
        }
        if (algorithm == null) {
            if (other.algorithm != null) { return false; }
        } else if (!algorithm.equals(other.algorithm)) {
            return false;
        }
        if (description == null) {
            if (other.description != null) { return false; }
        } else if (!description.equals(other.description)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) { return false; }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (addressId == null) {
            if (other.addressId != null) { return false; }
        } else if (!addressId.equals(other.addressId)) {
            return false;
        }
        if (publicip == null) {
            if (other.publicip != null) { return false; }
        } else if (!publicip.equals(other.publicip)) {
            return false;
        }
        if (publicport == null) {
            if (other.publicport != null) { return false; }
        } else if (!publicport.equals(other.publicport)) {
            return false;
        }
        if (privateport == null) {
            if (other.privateport != null) { return false; }
        } else if (!privateport.equals(other.privateport)) {
            return false;
        }
        if (state == null) {
            if (other.state != null) { return false; }
        } else if (!state.equals(other.state)) {
            return false;
        }
        if (zoneid == null) {
            if (other.zoneid != null) { return false; }
        } else if (!zoneid.equals(other.zoneid)) {
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
        sb.append("CloudstackLoadBalancer").append(" [");
        sb.append("loadBalancerNo=").append(loadBalancerNo).append(", ");
        sb.append("loadBalancerId=").append(loadBalancerId).append(", ");
        sb.append("algorithm=").append(algorithm).append(", ");
        sb.append("description=").append(description).append(", ");
        sb.append("name=").append(name).append(", ");
        sb.append("addressId=").append(addressId).append(", ");
        sb.append("publicip=").append(publicip).append(", ");
        sb.append("publicport=").append(publicport).append(", ");
        sb.append("privateport=").append(privateport).append(", ");
        sb.append("state=").append(state).append(", ");
        sb.append("zoneid=").append(zoneid);
        sb.append("]");
        return sb.toString();
    }

}
