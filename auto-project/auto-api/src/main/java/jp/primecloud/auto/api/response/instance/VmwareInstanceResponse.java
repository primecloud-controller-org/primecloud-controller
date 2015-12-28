package jp.primecloud.auto.api.response.instance;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.entity.crud.VmwareInstance;


public class VmwareInstanceResponse {

    @JsonProperty("MachineName")
    private String machineName;

    /**
     * インスタンスタイプ
     */
    @JsonProperty("InstanceType")
    private String instanceType;

    /**
     * クラスタ名
     */
    @JsonProperty("ComputeResource")
    private String computeResource;

    /**
     * リソースプール
     */
    @JsonProperty("ResourcePool")
    private String resourcePool;

    /**
     * データストア
     */
    @JsonProperty("Datastore")
    private String datastore;

    /**
     * キーペア名
     */
    @JsonProperty("KeyName")
    private String keyName;

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

    @JsonProperty("IsStaticIp")
    private Boolean isStaticIp;

    @JsonProperty("SubnetMask")
    private String subnetMask;

    @JsonProperty("DefaultGateway")
    private String defaultGateway;

    public VmwareInstanceResponse() {}

    public VmwareInstanceResponse(VmwareInstance vmwareInstance) {
        this.machineName = vmwareInstance.getMachineName();
        this.instanceType = vmwareInstance.getInstanceType();
        this.computeResource = vmwareInstance.getComputeResource();
        this.resourcePool = vmwareInstance.getResourcePool();
        this.datastore = vmwareInstance.getDatastore();
        this.ipAddress = vmwareInstance.getIpAddress();
        this.privateIpAddress = vmwareInstance.getPrivateIpAddress();
    }

   /**
    *
    * machineNameを取得します。
    *
    * @return machineName
    */
    public String getMachineName() {
        return machineName;
    }

   /**
    *
    * machineNameを設定します。
    *
    * @param machineName
    */
    public void setMachineName(String machineName) {
        this.machineName = machineName;
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
    * computeResourceを取得します。
    *
    * @return computeResource
    */
    public String getComputeResource() {
        return computeResource;
    }

   /**
    *
    * computeResourceを設定します。
    *
    * @param computeResource
    */
    public void setComputeResource(String computeResource) {
        this.computeResource = computeResource;
    }

   /**
    *
    * resourcePoolを取得します。
    *
    * @return resourcePool
    */
    public String getResourcePool() {
        return resourcePool;
    }

   /**
    *
    * resourcePoolを設定します。
    *
    * @param resourcePool
    */
    public void setResourcePool(String resourcePool) {
        this.resourcePool = resourcePool;
    }

   /**
    *
    * datastoreを取得します。
    *
    * @return datastore
    */
    public String getDatastore() {
        return datastore;
    }

   /**
    *
    * datastoreを設定します。
    *
    * @param datastore
    */
    public void setDatastore(String datastore) {
        this.datastore = datastore;
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
    * isStaticIpを取得します。
    *
    * @return isStaticIp
    */
    public Boolean getIsStaticIp() {
        return isStaticIp;
    }

   /**
    *
    * isStaticIpを設定します。
    *
    * @param isStaticIp
    */
    public void setIsStaticIp(Boolean isStaticIp) {
        this.isStaticIp = isStaticIp;
    }

   /**
    *
    * subnetMaskを取得します。
    *
    * @return subnetMask
    */
    public String getSubnetMask() {
        return subnetMask;
    }

   /**
    *
    * subnetMaskを設定します。
    *
    * @param subnetMask
    */
    public void setSubnetMask(String subnetMask) {
        this.subnetMask = subnetMask;
    }

   /**
    *
    * defaultGatewayを取得します。
    *
    * @return defaultGateway
    */
    public String getDefaultGateway() {
        return defaultGateway;
    }

   /**
    *
    * defaultGatewayを設定します。
    *
    * @param defaultGateway
    */
    public void setDefaultGateway(String defaultGateway) {
        this.defaultGateway = defaultGateway;
    }
}