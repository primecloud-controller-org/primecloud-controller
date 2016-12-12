package jp.primecloud.auto.api.response.instance;

import jp.primecloud.auto.entity.crud.NiftyInstance;

import org.apache.commons.lang.BooleanUtils;
import org.codehaus.jackson.annotate.JsonProperty;

public class NiftyInstanceResponse {

    @JsonProperty("KeyName")
    private String keyName;

    @JsonProperty("InstanceType")
    private String instanceType;

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

    @JsonProperty("Initialized")
    private Boolean initialized;

    public NiftyInstanceResponse(NiftyInstance niftyInstance) {
        this.instanceType = niftyInstance.getInstanceType();
        this.status = niftyInstance.getStatus();
        this.dnsName = niftyInstance.getDnsName();
        this.privateDnsName = niftyInstance.getPrivateDnsName();
        this.ipAddress = niftyInstance.getIpAddress();
        this.privateIpAddress = niftyInstance.getPrivateIpAddress();
        this.initialized = BooleanUtils.isTrue(niftyInstance.getInitialized());
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

    public Boolean getInitialized() {
        return initialized;
    }

    public void setInitialized(Boolean initialized) {
        this.initialized = initialized;
    }

}
