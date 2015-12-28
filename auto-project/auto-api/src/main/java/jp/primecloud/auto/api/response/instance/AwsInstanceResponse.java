package jp.primecloud.auto.api.response.instance;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.entity.crud.AwsInstance;


public class AwsInstanceResponse {

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
     * セキュリティグループ
     */
    @JsonProperty("SecurityGroups")
    private String securityGroups;

    /**
     * ゾーン名
     */
    @JsonProperty("AvailabilityZone")
    private String availabilityZone;

    /**
     * サブネットID
     */
    @JsonProperty("SubnetId")
    private String subnetId;

    /**
     * サブネット(cidrBlock)
     */
    @JsonProperty("Subnet")
    private String subnet;

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
    * securityGroupsを取得します。
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
}