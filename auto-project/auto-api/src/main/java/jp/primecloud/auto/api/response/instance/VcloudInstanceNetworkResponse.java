package jp.primecloud.auto.api.response.instance;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.entity.crud.VcloudInstanceNetwork;

public class VcloudInstanceNetworkResponse {

    /**
     * ネットワーク名
     */
    @JsonProperty("NetworkName")
    private String networkName;

    /**
     * ネットワークINDEX
     */
    @JsonProperty("NetworkIndex")
    private String networkIndex;

    /**
     * IPモード
     */
    @JsonProperty("IpMode")
    private String ipMode;

    /**
     * IPアドレス
     */
    @JsonProperty("IpAddress")
    private String ipAddress;

    /**
     * プライマリ判定
     */
    @JsonProperty("IsPrimary")
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