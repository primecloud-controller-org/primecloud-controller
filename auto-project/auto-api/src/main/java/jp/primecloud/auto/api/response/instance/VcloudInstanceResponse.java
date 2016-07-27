package jp.primecloud.auto.api.response.instance;

import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.entity.crud.VcloudInstance;

import org.codehaus.jackson.annotate.JsonProperty;

public class VcloudInstanceResponse {

    @JsonProperty("VmName")
    private String vmName;

    @JsonProperty("StorageTypeName")
    private String storageTypeName;

    @JsonProperty("InstanceType")
    private String instanceType;

    @JsonProperty("KeyName")
    private String keyName;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("IpAddress")
    private String ipAddress;

    @JsonProperty("PrivateIpAddress")
    private String privateIpAddress;

    @JsonProperty("VcloudNetworks")
    private List<VcloudInstanceNetworkResponse> vcloudNetwoks = new ArrayList<VcloudInstanceNetworkResponse>();

    public VcloudInstanceResponse() {
    }

    public VcloudInstanceResponse(VcloudInstance vcloudInstance) {
        this.vmName = vcloudInstance.getVmName();
        this.instanceType = vcloudInstance.getInstanceType();
        this.status = vcloudInstance.getStatus();
        this.ipAddress = vcloudInstance.getIpAddress();
        this.privateIpAddress = vcloudInstance.getPrivateIpAddress();
    }

    public String getVmName() {
        return vmName;
    }

    public void setVmName(String vmName) {
        this.vmName = vmName;
    }

    public String getStorageTypeName() {
        return storageTypeName;
    }

    public void setStorageTypeName(String storageTypeName) {
        this.storageTypeName = storageTypeName;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public List<VcloudInstanceNetworkResponse> getVcloudNetwoks() {
        return vcloudNetwoks;
    }

    public void setVcloudNetwoks(List<VcloudInstanceNetworkResponse> vcloudNetwoks) {
        this.vcloudNetwoks = vcloudNetwoks;
    }

}
