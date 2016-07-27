package jp.primecloud.auto.api.response.instance;

import jp.primecloud.auto.entity.crud.OpenstackInstance;

import org.codehaus.jackson.annotate.JsonProperty;

public class OpenstackInstanceResponse {

    @JsonProperty("KeyName")
    private String keyName;

    @JsonProperty("InstnaceType")
    private String instanceType;

    @JsonProperty("SecurityGroups")
    private String securityGroups;

    @JsonProperty("AvailabilityZone")
    private String availabilityZone;

    @JsonProperty("InstanceID")
    private String instanceId;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("KeyPair")
    private String keyPair;

    @JsonProperty("ClientIpAddress")
    private String clientIpAddress;

    @JsonProperty("PrivateIpAddress")
    private String privateIpAddress;

    @JsonProperty("NetworkId")
    private String networkId;

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

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKeyPair() {
        return keyPair;
    }

    public void setKeyPair(String keyPair) {
        this.keyPair = keyPair;
    }

    public String getClientIpAddress() {
        return clientIpAddress;
    }

    public void setClientIpAddress(String clientIpAddress) {
        this.clientIpAddress = clientIpAddress;
    }

    public String getPrivateIpAddress() {
        return privateIpAddress;
    }

    public void setPrivateIpAddress(String privateIpAddress) {
        this.privateIpAddress = privateIpAddress;
    }

    public String getNetworkId() {
        return networkId;
    }

    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

}
