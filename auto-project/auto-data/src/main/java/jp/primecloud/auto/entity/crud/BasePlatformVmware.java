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
 * PLATFORM_VMWAREに対応したエンティティのベースクラスです。
 * </p>
 *
 */
public abstract class BasePlatformVmware implements Serializable {

    /** SerialVersionUID */
    private static final long serialVersionUID = 1L;

    /** PLATFORM_NO [BIGINT(19,0)] */
    private Long platformNo;

    /** URL [VARCHAR(500,0)] */
    private String url;

    /** USERNAME [VARCHAR(100,0)] */
    private String username;

    /** PASSWORD [VARCHAR(100,0)] */
    private String password;

    /** DATACENTER [VARCHAR(300,0)] */
    private String datacenter;

    /** PUBLIC_NETWORK [VARCHAR(300,0)] */
    private String publicNetwork;

    /** PRIVATE_NETWORK [VARCHAR(300,0)] */
    private String privateNetwork;

    /** COMPUTE_RESOURCE [VARCHAR(300,0)] */
    private String computeResource;

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
     * urlを取得します。
     *
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * urlを設定します。
     *
     * @param url url
     */
    public void setUrl(String url) {
        this.url = url;
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
     * datacenterを取得します。
     *
     * @return datacenter
     */
    public String getDatacenter() {
        return datacenter;
    }

    /**
     * datacenterを設定します。
     *
     * @param datacenter datacenter
     */
    public void setDatacenter(String datacenter) {
        this.datacenter = datacenter;
    }

    /**
     * publicNetworkを取得します。
     *
     * @return publicNetwork
     */
    public String getPublicNetwork() {
        return publicNetwork;
    }

    /**
     * publicNetworkを設定します。
     *
     * @param publicNetwork publicNetwork
     */
    public void setPublicNetwork(String publicNetwork) {
        this.publicNetwork = publicNetwork;
    }

    /**
     * privateNetworkを取得します。
     *
     * @return privateNetwork
     */
    public String getPrivateNetwork() {
        return privateNetwork;
    }

    /**
     * privateNetworkを設定します。
     *
     * @param privateNetwork privateNetwork
     */
    public void setPrivateNetwork(String privateNetwork) {
        this.privateNetwork = privateNetwork;
    }

    /**
     * computeResourceを取得します。
     *
     * @return computeResource
     */
    public String getComputeResource() {
        return computeResource;
    }

    /**
     * computeResourceを設定します。
     *
     * @param computeResource computeResource
     */
    public void setComputeResource(String computeResource) {
        this.computeResource = computeResource;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;

        final int prime = 31;
        result = prime * result + ((platformNo == null) ? 0 : platformNo.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((datacenter == null) ? 0 : datacenter.hashCode());
        result = prime * result + ((publicNetwork == null) ? 0 : publicNetwork.hashCode());
        result = prime * result + ((privateNetwork == null) ? 0 : privateNetwork.hashCode());
        result = prime * result + ((computeResource == null) ? 0 : computeResource.hashCode());

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

        final BasePlatformVmware other = (BasePlatformVmware) obj;
        if (platformNo == null) {
            if (other.platformNo != null) { return false; }
        } else if (!platformNo.equals(other.platformNo)) {
            return false;
        }
        if (url == null) {
            if (other.url != null) { return false; }
        } else if (!url.equals(other.url)) {
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
        if (datacenter == null) {
            if (other.datacenter != null) { return false; }
        } else if (!datacenter.equals(other.datacenter)) {
            return false;
        }
        if (publicNetwork == null) {
            if (other.publicNetwork != null) { return false; }
        } else if (!publicNetwork.equals(other.publicNetwork)) {
            return false;
        }
        if (privateNetwork == null) {
            if (other.privateNetwork != null) { return false; }
        } else if (!privateNetwork.equals(other.privateNetwork)) {
            return false;
        }
        if (computeResource == null) {
            if (other.computeResource != null) { return false; }
        } else if (!computeResource.equals(other.computeResource)) {
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
        sb.append("PlatformVmware").append(" [");
        sb.append("platformNo=").append(platformNo).append(", ");
        sb.append("url=").append(url).append(", ");
        sb.append("username=").append(username).append(", ");
        sb.append("password=").append(password).append(", ");
        sb.append("datacenter=").append(datacenter).append(", ");
        sb.append("publicNetwork=").append(publicNetwork).append(", ");
        sb.append("privateNetwork=").append(privateNetwork).append(", ");
        sb.append("computeResource=").append(computeResource);
        sb.append("]");
        return sb.toString();
    }

}
