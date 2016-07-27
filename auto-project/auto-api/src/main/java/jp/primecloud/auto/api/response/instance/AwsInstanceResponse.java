package jp.primecloud.auto.api.response.instance;

import jp.primecloud.auto.entity.crud.AwsInstance;

import org.codehaus.jackson.annotate.JsonProperty;

public class AwsInstanceResponse {

    @JsonProperty("KeyName")
    private String keyName;

    @JsonProperty("InstanceType")
    private String instanceType;

    @JsonProperty("SecurityGroups")
    private String securityGroups;

    @JsonProperty("AvailabilityZone")
    private String availabilityZone;

    @JsonProperty("SubnetId")
    private String subnetId;

    @JsonProperty("Subnet")
    private String subnet;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("DnsName")
    private String dnsName;

    @JsonProperty("PrivateDnsName")
    private String privateDnsName;

    @JsonProperty("IpAddress")
    private String ipAddress;

    @JsonProperty("PrivateIpAddress")
    private String privateIpAddress;

    public AwsInstanceResponse() {
    }

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

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public String getSecurityGroups() {
        return securityGroups;
    }

    public void setSecurityGroups(String securityGroups) {
        this.securityGroups = securityGroups;
    }

    public String getAvailabilityZone() {
        return availabilityZone;
    }

    public void setAvailabilityZone(String availabilityZone) {
        this.availabilityZone = availabilityZone;
    }

    public String getSubnetId() {
        return subnetId;
    }

    public void setSubnetId(String subnetId) {
        this.subnetId = subnetId;
    }

    public String getSubnet() {
        return subnet;
    }

    public void setSubnet(String subnet) {
        this.subnet = subnet;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDnsName() {
        return dnsName;
    }

    public void setDnsName(String dnsName) {
        this.dnsName = dnsName;
    }

    public String getPrivateDnsName() {
        return privateDnsName;
    }

    public void setPrivateDnsName(String privateDnsName) {
        this.privateDnsName = privateDnsName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getPrivateIpAddress() {
        return privateIpAddress;
    }

    public void setPrivateIpAddress(String privateIpAddress) {
        this.privateIpAddress = privateIpAddress;
    }

}
