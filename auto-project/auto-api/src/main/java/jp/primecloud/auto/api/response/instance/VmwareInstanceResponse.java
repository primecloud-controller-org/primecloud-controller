package jp.primecloud.auto.api.response.instance;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import jp.primecloud.auto.entity.crud.VmwareInstance;


@XmlRootElement(name="VmwareInstanceResponse")
@XmlType(propOrder = {"machineName","instanceType","computeResource","resourcePool","datastore","keyName","ipAddress","privateIpAddress","isStaticIp","subnetMask","defaultGateway"})
public class VmwareInstanceResponse {

    private String machineName;

    /**
     * インスタンスタイプ
     */
    private String instanceType;

    /**
     * クラスタ名
     */
    private String computeResource;

    /**
     * リソースプール
     */
    private String resourcePool;

    /**
     * データストア
     */
    private String datastore;

    /**
     * キーペア名
     */
    private String keyName;

    /**
     * パブリックIPアドレス
     */
    private String ipAddress;

    /**
     * プライベートIPアドレス
     */
    private String privateIpAddress;

    private Boolean isStaticIp;

    private String subnetMask;

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
    @XmlElement(name="MachineName")
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
    * computeResourceを取得します。
    *
    * @return computeResource
    */
    @XmlElement(name="ComputeResource")
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
    @XmlElement(name="ResourcePool")
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
    @XmlElement(name="Datastore")
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

   /**
    *
    * isStaticIpを取得します。
    *
    * @return isStaticIp
    */
    @XmlElement(name="IsStaticIp")
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
    @XmlElement(name="SubnetMask")
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
    @XmlElement(name="DefaultGateway")
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