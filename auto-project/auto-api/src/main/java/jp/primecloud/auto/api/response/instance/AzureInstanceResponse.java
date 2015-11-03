package jp.primecloud.auto.api.response.instance;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.entity.crud.AzureInstance;


public class AzureInstanceResponse {

    /**
     * インスタンス名
     */
    @JsonProperty("InstanceName")
    private String instanceName;

    /**
     * アフィニティグループ名
     */
    @JsonProperty("AffinityGruopName")
    private String affinityGroupName;

    /**
     * クラウドサービス名
     */
    @JsonProperty("CloudServiceName")
    private String cloudServiceName;

    /**
     * ストレージアカウント名
     */
    @JsonProperty("StorageAccountName")
    private String storageAccountName;

    /**
     * ネットワーク名
     */
    @JsonProperty("NetworkName")
    private String networkName;

    /**
     * インスタンスタイプ
     */
    @JsonProperty("InstanceType")
    private String instanceType;

    /**
     * ステータス
     */
    @JsonProperty("Status")
    private String status;

    /**
     * サブネットID
     */
    @JsonProperty("SubnetId")
    private String subnetId;

    /**
     * プライベートIPアドレス
     */
    @JsonProperty("PrivateIpAddress")
    private String privateIpAddress;

    /**
     * ロケーション名
     */
    @JsonProperty("LocationName")
    private String locationName;

    /**
     * ゾーン
     */
    @JsonProperty("AvailabilitySet")
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