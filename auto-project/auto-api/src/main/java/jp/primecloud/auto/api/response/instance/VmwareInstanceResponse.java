package jp.primecloud.auto.api.response.instance;

import jp.primecloud.auto.entity.crud.VmwareInstance;

import org.codehaus.jackson.annotate.JsonProperty;

public class VmwareInstanceResponse {

    @JsonProperty("MachineName")
    private String machineName;

    @JsonProperty("InstanceType")
    private String instanceType;

    @JsonProperty("ComputeResource")
    private String computeResource;

    @JsonProperty("ResourcePool")
    private String resourcePool;

    @JsonProperty("Datastore")
    private String datastore;

    @JsonProperty("KeyName")
    private String keyName;

    @JsonProperty("RootSize")
    private Integer rootSize;

    @JsonProperty("IpAddress")
    private String ipAddress;

    @JsonProperty("PrivateIpAddress")
    private String privateIpAddress;

    @JsonProperty("IsStaticIp")
    private Boolean isStaticIp;

    @JsonProperty("SubnetMask")
    private String subnetMask;

    @JsonProperty("DefaultGateway")
    private String defaultGateway;

    public VmwareInstanceResponse(VmwareInstance vmwareInstance) {
        this.machineName = vmwareInstance.getMachineName();
        this.instanceType = vmwareInstance.getInstanceType();
        this.computeResource = vmwareInstance.getComputeResource();
        this.resourcePool = vmwareInstance.getResourcePool();
        this.datastore = vmwareInstance.getDatastore();
        this.rootSize = vmwareInstance.getRootSize();
        this.ipAddress = vmwareInstance.getIpAddress();
        this.privateIpAddress = vmwareInstance.getPrivateIpAddress();
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public String getComputeResource() {
        return computeResource;
    }

    public void setComputeResource(String computeResource) {
        this.computeResource = computeResource;
    }

    public String getResourcePool() {
        return resourcePool;
    }

    public void setResourcePool(String resourcePool) {
        this.resourcePool = resourcePool;
    }

    public String getDatastore() {
        return datastore;
    }

    public void setDatastore(String datastore) {
        this.datastore = datastore;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public Integer getRootSize() {
        return rootSize;
    }

    public void setRootSize(Integer rootSize) {
        this.rootSize = rootSize;
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

    public Boolean getIsStaticIp() {
        return isStaticIp;
    }

    public void setIsStaticIp(Boolean isStaticIp) {
        this.isStaticIp = isStaticIp;
    }

    public String getSubnetMask() {
        return subnetMask;
    }

    public void setSubnetMask(String subnetMask) {
        this.subnetMask = subnetMask;
    }

    public String getDefaultGateway() {
        return defaultGateway;
    }

    public void setDefaultGateway(String defaultGateway) {
        this.defaultGateway = defaultGateway;
    }

}
