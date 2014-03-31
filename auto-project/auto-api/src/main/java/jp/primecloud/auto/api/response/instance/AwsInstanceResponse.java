package jp.primecloud.auto.api.response.instance;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import jp.primecloud.auto.entity.crud.AwsInstance;



@XmlRootElement(name="AwsInstanceResponse")
@XmlType(propOrder = { "keyName","instanceType","securityGroups","availabilityZone","subnetId","subnet","status","dnsName","privateDnsName","ipAddress","privateIpAddress"})
public class AwsInstanceResponse {

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
     * ゾーン名
     */
    private String availabilityZone;

    /**
     * サブネットID
     */
    private String subnetId;

    /**
     * サブネット(cidrBlock)
     */
    private String subnet;

    /**
     * ステータス
     */
    private String status;

    /**
     * パブリックDNS
     */
    private String dnsName;

    /**
     * プライベートDNS
     */
    private String privateDnsName;

    /**
     * パブリックIPアドレス
     */
    private String ipAddress;

    /**
     * プライベートIPアドレス
     */
    private String privateIpAddress;

    public AwsInstanceResponse() {}

    public AwsInstanceResponse(AwsInstance awsInstance) {
        this.keyName = awsInstance.getKeyName();
        this.instanceType = awsInstance.getInstanceType();
        this.securityGroups = awsInstance.getSecurityGroups();
        this.availabilityZone = awsInstance.getAvailabilityZone();
        this.subnetId = awsInstance.getSubnetId();
        this.status = awsInstance.getStatus();
        this.dnsName = awsInstance.getDnsName();
        this.privateDnsName = awsInstance.getPrivateDnsName();
        this.ipAddress = awsInstance.getIpAddress();
        this.privateIpAddress = awsInstance.getPrivateIpAddress();
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
    * instanceTypeを取得します。
    *
    * @return instanceType
    */
    @XmlElement(name="InstanceType")
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
    * securityGroupsを取得します。
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
    * @param securityGroups
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
    * subnetIdを取得します。
    *
    * @return subnetId
    */
    @XmlElement(name="SubnetId")
    public String getSubnetId() {
        return subnetId;
    }

   /**
    *
    * subnetIdを設定します。
    *
    * @param subnetId
    */
    public void setSubnetId(String subnetId) {
        this.subnetId = subnetId;
    }

   /**
    *
    * subnetを取得します。
    *
    * @return subnet
    */
    @XmlElement(name="Subnet")
    public String getSubnet() {
        return subnet;
    }

   /**
    *
    * subnetを設定します。
    *
    * @param subnet
    */
    public void setSubnet(String subnet) {
        this.subnet = subnet;
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
    * dnsNameを取得します。
    *
    * @return dnsName
    */
    @XmlElement(name="DnsName")
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
    @XmlElement(name="PrivateDnsName")
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
    @XmlElement(name="IpAddress")
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
}