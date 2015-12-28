package jp.primecloud.auto.api.response;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.entity.crud.PlatformOpenstack;


public class PlatformOpenstackResponse {

    /**
     * ホストURL
     */
    @JsonProperty("URL")
    private String url;

    /**
     * ネットワークID
     */
    @JsonProperty("NetworkID")
    private String networkId;

    /**
     * テナントID
     */
    @JsonProperty("TenantID")
    private String tenantId;

    /**
     * テナント名
     */
    @JsonProperty("TenantNm")
    private String tenantNm;

    /**
     * ゾーン名
     */
    @JsonProperty("AvailavilityZone")
    private String availabilityZone;

    /**
     * デフォルトキーペア
     */
    @JsonProperty("DefKeyPair")
    private String defKeyPair;


    public PlatformOpenstackResponse(){}

    public PlatformOpenstackResponse(PlatformOpenstack openstack) {
        this.url = openstack.getUrl();
        this.networkId = openstack.getNetworkId();
        this.tenantId = openstack.getTenantId();
        this.tenantNm = openstack.getTenantNm();
        this.availabilityZone = openstack.getAvailabilityZone();
    }


    /**
    *
    * URLを取得します。
    *
    * @return url
    */
    public String getUrl() {
        return url;
    }

    /**
    *
    * URLを設定
    *
    * @param url
    */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
    *
    * networkIdを取得します。
    *
    * @return networkId
    */
    public String getNetworkId() {
        return networkId;
    }

    /**
    *
    * networkIdを設定
    *
    * @param networkId
    */
    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    /**
    *
    * tenantIdを取得します。
    *
    * @return tenantId
    */
    public String getTenantId() {
        return tenantId;
    }

    /**
    *
    * tenantIdを設定
    *
    * @param tenantId
    */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
    *
    * tenantNmを取得します。
    *
    * @return tenantNm
    */
    public String getTenantNm() {
        return tenantNm;
    }

    /**
    *
    * tenantNmを設定
    *
    * @param tenantNm
    */
    public void setTenantNm(String tenantNm) {
        this.tenantNm = tenantNm;
    }

    /**
    *
    * AvailabilityZoneを取得します。
    *
    * @return AvailabilityZone
    */
    public String getAvailabilityZone() {
        return availabilityZone;
    }

    /**
    *
    * AvailabilityZoneを設定
    *
    * @param AvailabilityZone
    */
    public void setAvailabilityZone(String availabilityZone) {
        this.availabilityZone = availabilityZone;
    }

    /**
    *
    * defKeyPairを取得します。
    *
    * @return DefKeyPair
    */
    public String getDefKeyPair() {
        return defKeyPair;
    }

    /**
    *
    * defKeyPairを設定
    *
    * @param DefKeyPair
    */
    public void setDefKeyPair(String defKeyPair) {
        this.defKeyPair = defKeyPair;
    }


}