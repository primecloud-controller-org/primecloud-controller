package jp.primecloud.auto.api.response.instance;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import jp.primecloud.auto.entity.crud.AzureInstance;


@XmlRootElement(name="NiftyInstanceResponse")
@XmlType(propOrder = {"instanceName","affinityGroupName","cloudServiceName","storageAccountName","networkName","instanceType", "status" , "subnetId","privateIpAddress" , "locationName" , "availabilitySet" })
public class AzureInstanceResponse {

    /**
     * インスタンス名
     */
    private String instanceName;

    /**
     * アフィニティグループ名
     */
    private String affinityGroupName;

    /**
     * クラウドサービス名
     */
    private String cloudServiceName;

    /**
     * ストレージアカウント名
     */
    private String storageAccountName;

    /**
     * ネットワーク名
     */
    private String networkName;

    /**
     * インスタンスタイプ
     */
    private String instanceType;

    /**
     * ステータス
     */
    private String status;

    /**
     * サブネットID
     */
    private String subnetId;

    /**
     * プライベートIPアドレス
     */
    private String privateIpAddress;

    /**
     * ロケーション名
     */
    private String locationName;

    /**
     * ゾーン
     */
    private String availabilitySet;

    public AzureInstanceResponse() {}

    public AzureInstanceResponse(AzureInstance azureInstance) {
        this.instanceName = azureInstance.getInstanceName();
        this.affinityGroupName = azureInstance.getAffinityGroupName();
        this.cloudServiceName = azureInstance.getCloudServiceName();
        this.storageAccountName = azureInstance.getStorageAccountName();
        this.networkName = azureInstance.getNetworkName();
        this.instanceType = azureInstance.getInstanceType();
        this.status = azureInstance.getStatus();
        this.subnetId = azureInstance.getSubnetId();
        this.privateIpAddress = azureInstance.getPrivateIpAddress();
        this.locationName = azureInstance.getLocationName();
        this.availabilitySet = azureInstance.getAvailabilitySet();
    }

    /**
    *
    * instanceNameを取得します。
    *
    * @return InstanceName
    */
    @XmlElement(name="InstanceName")
    public String getInstanceName() {
        return instanceName;
    }


    /**
    *
    * instanceNameを設定します。
    *
    * @param InstanceName
    */
    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    /**
    *
    * cloudServiceNameを取得します。
    *
    * @return CloudServiceName
    */
    @XmlElement(name="CloudServiceName")
    public String getAffinityGroupName() {
        return affinityGroupName;
    }

    /**
    *
    * affinityGroupNameを設定します。
    *
    * @param AffinityGroupName
    */
    public void setAffinityGroupName(String affinityGroupName) {
        this.affinityGroupName = affinityGroupName;
    }

    /**
    *
    * cloudServiceNameを取得します。
    *
    * @return CloudServiceName
    */
    @XmlElement(name="CloudServiceName")
    public String getCloudServiceName() {
        return cloudServiceName;
    }

    /**
    *
    * cloudServiceNameを設定します。
    *
    * @param CloudServiceName
    */
    public void setCloudServiceName(String cloudServiceName) {
        this.cloudServiceName = cloudServiceName;
    }

    /**
    *
    * storageAccountNameを取得します。
    *
    * @return StorageAccountName
    */
    @XmlElement(name="StorageAccountName")
    public String getStorageAccountName() {
        return storageAccountName;
    }

    /**
    *
    * storageAccountNameを設定します。
    *
    * @param StorageAccountName
    */
    public void setStorageAccountName(String storageAccountName) {
        this.storageAccountName = storageAccountName;
    }

    /**
    *
    * networkNameを取得します。
    *
    * @return NetworkName
    */
    @XmlElement(name="NetworkName")
    public String getNetworkName() {
        return networkName;
    }

    /**
    *
    * networkNameを設定します。
    *
    * @param NetworkName
    */
    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    /**
    *
    * instnaceTypeを取得します。
    *
    * @return InstnaceType
    */
    @XmlElement(name="InstanceType")
    public String getInstanceType() {
        return instanceType;
    }

    /**
    *
    * instnaceTypeを設定します。
    *
    * @param InstnaceType
    */
    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
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
    * subnetIdを取得します。
    *
    * @return SubnetId
    */
    @XmlElement(name="SubnetId")
    public String getSubnetId() {
        return subnetId;
    }

    /**
    *
    * subnetIdを設定します。
    *
    * @param SubnetId
    */
    public void setSubnetId(String subnetId) {
        this.subnetId = subnetId;
    }

    /**
    *
    * privateIpAddressを取得します。
    *
    * @return PrivateIpAddress
    */
    @XmlElement(name="PrivateIpAddress")
    public String getPrivateIpAddress() {
        return privateIpAddress;
    }

    /**
    *
    * privateIpAddressを設定します。
    *
    * @param PrivateIpAddress
    */
    public void setPrivateIpAddress(String privateIpAddress) {
        this.privateIpAddress = privateIpAddress;
    }

    /**
    *
    * locationNameを取得します。
    *
    * @return LocationName
    */
    @XmlElement(name="LocationName")
    public String getLocationName() {
        return locationName;
    }

    /**
    *
    * locationNameを設定します。
    *
    * @param LocationName
    */
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    /**
    *
    * availabilitySetを取得します。
    *
    * @return AvailabilitySet
    */
    @XmlElement(name="AvailabilitySet")
    public String getAvailabilitySet() {
        return availabilitySet;
    }

    /**
    *
    * availabilitySetを設定します。
    *
    * @param AvailabilitySet
    */
    public void setAvailabilitySet(String availabilitySet) {
        this.availabilitySet = availabilitySet;
    }
}