package jp.primecloud.auto.api.response.instance;

import org.apache.commons.lang.BooleanUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.entity.crud.NiftyInstance;


public class NiftyInstanceResponse {

    /**
     * キーペア名
     */
    @JsonProperty("KeyName")
    private String keyName;

    /**
     * インスタンスタイプ
     */
    @JsonProperty("InstanceType")
    private String instanceType;

    /**
     * ステータス
     */
    @JsonProperty("Status")
    private String status;

    /**
     * パブリックDNS
     */
    @JsonProperty("DnsName")
    private String dnsName;

    /**
     * プライベートDNS
     */
    @JsonProperty("PrivateDnsName")
    private String privateDnsName;

    /**
     * パブリックIPアドレス
     */
    @JsonProperty("IpAddress")
    private String ipAddress;

    /**
     * プライベートIPアドレス
     */
    @JsonProperty("PrivateIpAddress")
    private String privateIpAddress;

    /**
     * 初期化済みかどうか true:初期化済み、false:初期化済みではない
     */
    @JsonProperty("Initialized")
    private Boolean initialized;

    public NiftyInstanceResponse() {}

    public NiftyInstanceResponse(NiftyInstance niftyInstance) {
        this.instanceType = niftyInstance.getInstanceType();
        this.status = niftyInstance.getStatus();
        this.dnsName = niftyInstance.getDnsName();
        this.privateDnsName = niftyInstance.getPrivateDnsName();
        this.ipAddress = niftyInstance.getIpAddress();
        this.privateIpAddress = niftyInstance.getPrivateIpAddress();
        this.initialized = BooleanUtils.isTrue(niftyInstance.getInitialized());
    }

   /**
    *
    * keyNameを取得します。
    *
    * @return keyName
    */
    public String getKeyName() {
        return keyName;
    }

   /**
    *
    * keyNameを設定します。
    *
    * @param keyName
    */
    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    /**
    *
    * instanceTypeを取得します。
    *
    * @return instanceType
    */
    public String getInstanceType() {
        return instanceType;
    }

   /**
    *
    * instanceTypeを設定します。
    *
    * @param instanceType
    */
    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

   /**
    *
    * statusを取得します。
    *
    * @return status
    */
    public String getStatus() {
        return status;
    }

   /**
    *
    * statusを設定します。
    *
    * @param status
    */
    public void setStatus(String status) {
        this.status = status;
    }

   /**
    *
    * dnsNameを取得します。
    *
    * @return dnsName
    */
    public String getDnsName() {
        return dnsName;
    }

   /**
    *
    * dnsNameを設定します。
    *
    * @param dnsName
    */
    public void setDnsName(String dnsName) {
        this.dnsName = dnsName;
    }

   /**
    *
    * privateDnsNameを取得します。
    *
    * @return privateDnsName
    */
    public String getPrivateDnsName() {
        return privateDnsName;
    }

   /**
    *
    * privateDnsNameを設定します。
    *
    * @param privateDnsName
    */
    public void setPrivateDnsName(String privateDnsName) {
        this.privateDnsName = privateDnsName;
    }

   /**
    *
    * ipAddressを取得します。
    *
    * @return ipAddress
    */
    public String getIpAddress() {
        return ipAddress;
    }

   /**
    *
    * ipAddressを設定します。
    *
    * @param ipAddress
    */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

   /**
    *
    * privateIpAddressを取得します。
    *
    * @return privateIpAddress
    */
    public String getPrivateIpAddress() {
        return privateIpAddress;
    }

   /**
    *
    * privateIpAddressを設定します。
    *
    * @param privateIpAddress
    */
    public void setPrivateIpAddress(String privateIpAddress) {
        this.privateIpAddress = privateIpAddress;
    }

   /**
    *
    * initializedを取得します。
    *
    * @return initialized
    */
    public Boolean getInitialized() {
        return initialized;
    }

   /**
    *
    * initializedを設定します。
    *
    * @param initialized
    */
    public void setInitialized(Boolean initialized) {
        this.initialized = initialized;
    }
}