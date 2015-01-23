package jp.primecloud.auto.api.response.instance;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import jp.primecloud.auto.entity.crud.VcloudInstanceNetwork;

@XmlRootElement(name = "VcloudInstanceNetworkResponse")
@XmlType(propOrder = { "networkName", "networkIndex", "ipMode", "ipAddress", "isPrimary" })
public class VcloudInstanceNetworkResponse {

    /**
     * ネットワーク名
     */
    private String networkName;

    /**
     * ネットワークINDEX
     */
    private String networkIndex;

    /**
     * IPモード
     */
    private String ipMode;

    /**
     * IPアドレス
     */
    private String ipAddress;

    /**
     * プライマリ判定
     */
    private Boolean isPrimary;

    public VcloudInstanceNetworkResponse() {
    }

    public VcloudInstanceNetworkResponse(VcloudInstanceNetwork vcloudInstanceNetwork) {
        this.networkName = vcloudInstanceNetwork.getNetworkName();
        if (vcloudInstanceNetwork.getNetworkIndex() == null) {
            this.networkIndex = "";
        } else {
            this.networkIndex = String.valueOf(vcloudInstanceNetwork.getNetworkIndex());
        }
        this.ipMode = vcloudInstanceNetwork.getIpMode();
        this.ipAddress = vcloudInstanceNetwork.getIpAddress();
    }

    /**
     * ネットワーク名を取得します。
     * @return ネットワーク名
     */
    @XmlElement(name = "NetworkName")
    public String getNetworkName() {
        return networkName;
    }

    /**
     * ネットワーク名を設定します。
     * @param networkName ネットワーク名
     */
    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    /**
     * ネットワークINDEXを取得します。
     * @return ネットワークINDEX
     */
    @XmlElement(name = "NetworkIndex")
    public String getNetworkIndex() {
        return networkIndex;
    }

    /**
     * ネットワークINDEXを設定します。
     * @param networkIndex ネットワークINDEX
     */
    public void setNetworkIndex(String networkIndex) {
        this.networkIndex = networkIndex;
    }

    /**
     * IPモードを取得します。
     * @return IPモード
     */
    @XmlElement(name = "IpMode")
    public String getIpMode() {
        return ipMode;
    }

    /**
     * IPモードを設定します。
     * @param ipMode IPモード
     */
    public void setIpMode(String ipMode) {
        this.ipMode = ipMode;
    }

    /**
     * IPアドレスを取得します。
     * @return IPアドレス
     */
    @XmlElement(name = "IpAddress")
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * IPアドレスを設定します。
     * @param ipAddress IPアドレス
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * プライマリ判定を取得します。
     * @return プライマリ判定
     */
    @XmlElement(name = "IsPrimary")
    public Boolean getIsPrimary() {
        return isPrimary;
    }

    /**
     * プライマリ判定を設定します。
     * @param isPrimary プライマリ判定
     */
    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }
}