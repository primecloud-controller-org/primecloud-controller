package jp.primecloud.auto.api.response.instance;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import jp.primecloud.auto.entity.crud.OpenstackInstance;


@XmlRootElement(name="NiftyInstanceResponse")
@XmlType(propOrder = {"keyName","instanceType","securityGroups","availabilityZone","instanceId","status", "keyPair","clientIpAddress" , "privateIpAddress","networkId" })
public class OpenstackInstanceResponse {

    /**
     * キーペア名
     */
    private String keyName;

    /**
     * インスタンスタイプ
     */
    private String instanceType;

    /**
     * セキュリティグループ
     */
    private String securityGroups;

    /**
     * ゾーン
     */
    private String availabilityZone;

    /**
     * インスタンスID
     */
    private String instanceId;

    /**
     * ステータス
     */
    private String status;

    /**
     * キーペア名
     */
    private String keyPair;

    /**
     * クライアントIPアドレス
     */
    private String clientIpAddress;

    /**
     * プライベートIPアドレス
     */
    private String privateIpAddress;

    /**
     * ネットワークID
     */
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
    @XmlElement(name="KeyName")
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
    @XmlElement(name="InstnaceType")
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
    @XmlElement(name="SecurityGroups")
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
    @XmlElement(name="AvailabilityZone")
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
    @XmlElement(name="InstanceID")
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
    @XmlElement(name="Status")
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
    @XmlElement(name="KeyPair")
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
    @XmlElement(name="ClientIpAddress")
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
    @XmlElement(name="PrivateIpAddress")
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
    @XmlElement(name="NetworkId")
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