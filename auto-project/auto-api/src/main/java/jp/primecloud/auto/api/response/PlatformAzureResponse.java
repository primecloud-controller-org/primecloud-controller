package jp.primecloud.auto.api.response;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.entity.crud.PlatformAzure;



public class PlatformAzureResponse {

    /**
     * ロケーション名
     */
    @JsonProperty("LocationName")
    private String locationName;

    /**
     * アフィニティグループ名
     */
    @JsonProperty("AffinityGroupName")
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
    @JsonProperty("DefNetworkName")
    private String networkName;

    /**
     * ゾーン名
     */
    @JsonProperty("AvailabilitySets")
    private String availabilitySets;

    /**
     * デフォルトキーペア
     */
    @JsonProperty("DefKeyPair")
    private String defKeyPair;

    /**
     * デフォルトサブネット(CidrBlock)
     */
    @JsonProperty("DefSubnet")
    private String defSubnet;

    public PlatformAzureResponse() {}

    public PlatformAzureResponse(PlatformAzure azure) {
        this.locationName = azure.getLocationName();
        this.affinityGroupName = azure.getAffinityGroupName();
        this.cloudServiceName = azure.getCloudServiceName();
        this.storageAccountName = azure.getStorageAccountName();
        this.networkName = azure.getNetworkName();
        this.availabilitySets = azure.getAvailabilitySets();
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
    * LocationNameを設定
    *
    * @param DefKeyPair
    */
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    /**
    *
    * affinityGroupNameを取得します。
    *
    * @return AffinityGroupName
    */
    public String getAffinityGroupName() {
        return affinityGroupName;
    }

    /**
    *
    * affinityGroupNameを設定
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
    * cloudServiceNameを設定
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
    * storageAccountNameを設定
    *
    * @param StorageAccountName
    */
    public void setStorageAccountName(String storageAccountName) {
        this.storageAccountName = storageAccountName;
    }

    /**
    *
    * defNetworkNameを取得します。
    *
    * @return DefNetworkName
    */
    public String getNetworkName() {
        return networkName;
    }

    /**
    *
    * networkNameを設定
    *
    * @param NetworkName
    */
    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    /**
    *
    * availabilitySetsを取得します。
    *
    * @return AvailabilitySets
    */
    public String getAvailabilitySets() {
        return availabilitySets;
    }

    /**
    *
    * availabilitySetsを設定
    *
    * @param AvailabilitySets
    */
    public void setAvailabilitySets(String availabilitySets) {
        this.availabilitySets = availabilitySets;
    }

    /**
    *
    * defKeyPairを取得します。
    *
    * @return DefKeyPair
    */
    public String getDefKeyPair() {
        return defKeyPair;
    }

    /**
    *
    * defKeyPairを設定
    *
    * @param DefKeyPair
    */
    public void setDefKeyPair(String defKeyPair) {
        this.defKeyPair = defKeyPair;
    }

    /**
    *
    * defSubnetを取得します。
    *
    * @return DefSubnet
    */
    public String getDefSubnet() {
        return defSubnet;
    }

    /**
    *
    * defSubnetを設定
    *
    * @param DefSubnet
    */
    public void setDefSubnet(String defSubnet) {
        this.defSubnet = defSubnet;
    }
}