package jp.primecloud.auto.api.response.instance;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.entity.crud.OpenstackInstance;


public class OpenstackInstanceResponse {

    /**
     * キーペア名
     */
    @JsonProperty("KeyName")
    private String keyName;

    /**
     * インスタンスタイプ
     */
    @JsonProperty("InstnaceType")
    private String instanceType;

    /**
     * セキュリティグループ
     */
    @JsonProperty("SecurityGroups")
    private String securityGroups;

    /**
     * ゾーン
     */
    @JsonProperty("AvailabilityZone")
    private String availabilityZone;

    /**
     * インスタンスID
     */
    @JsonProperty("InstanceID")
    private String instanceId;

    /**
     * ステータス
     */
    @JsonProperty("Status")
    private String status;

    /**
     * キーペア名
     */
    @JsonProperty("KeyPair")
    private String keyPair;

    /**
     * クライアントIPアドレス
     */
    @JsonProperty("ClientIpAddress")
    private String clientIpAddress;

    /**
     * プライベートIPアドレス
     */
    @JsonProperty("PrivateIpAddress")
    private String privateIpAddress;

    /**
     * ネットワークID
     */
    @JsonProperty("NetworkId")
    private String networkId;

    public OpenstackInstanceResponse() {}

    public OpenstackInstanceResponse(OpenstackInstance openstackInstance) {
        this.keyName = openstackInstance.getKeyName();
        this.instanceType = openstackInstance.getInstanceType();
        this.securityGroups = openstackInstance.getSecurityGroups();
        this.availabilityZone = openstackInstance.getAvailabilityZone();
        this.instanceId = openstackInstance.getInstanceId();
        this.status = openstackInstance.getStatus();
        this.clientIpAddress = openstackInstance.getClientIpAddress();
        this.privateIpAddress = openstackInstance.getPrivateIpAddress();
        this.networkId = openstackInstance.getNetworkId();
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
    * instnaceTypeを取得します。
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
    * securityGoupsを取得します。
    *
    * @return securityGroups
    */
    public String getSecurityGroups() {
        return securityGroups;
    }

    /**
    *
    * securityGroupsを設定します。
    *
    * @param sequrityGroups
    */
    public void setSecurityGroups(String securityGroups) {
        this.securityGroups = securityGroups;
    }

    /**
    *
    * availabilityZoneを取得します。
    *
    * @return availabilityZone
    */
    public String getAvailabilityZone() {
        return availabilityZone;
    }

    /**
    *
    * availabilityZoneを設定します。
    *
    * @param availabilityZone
    */
    public void setAvailabilityZone(String availabilityZone) {
        this.availabilityZone = availabilityZone;
    }

    /**
    *
    * instanceIdを取得します。
    *
    * @return instanceId
    */
    public String getInstanceId() {
        return instanceId;
    }

    /**
    *
    * instanceIdを設定します。
    *
    * @param instanceId
    */
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
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
    * keyPairを取得します。
    *
    * @return keyPair
    */
    public String getKeyPair() {
        return keyPair;
    }

    /**
    *
    * keyPairを設定します。
    *
    * @param keyPair
    */
    public void setKeyPair(String keyPair) {
        this.keyPair = keyPair;
    }

    /**
    *
    * clientIpAddressを取得します。
    *
    * @return clientIpAddress
    */
    public String getClientIpAddress() {
        return clientIpAddress;
    }

    /**
    *
    * clientIpAddressを設定します。
    *
    * @param clientIpAddress
    */
    public void setClientIpAddress(String clientIpAddress) {
        this.clientIpAddress = clientIpAddress;
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
    * networkIdを取得します。
    *
    * @return networkId
    */
    public String getNetworkId() {
        return networkId;
    }

    /**
    *
    * networkIdを設定します。
    *
    * @param networkId
    */
    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

}