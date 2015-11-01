package jp.primecloud.auto.api.response.instance;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.entity.crud.VcloudInstance;


public class VcloudInstanceResponse {

    /**
     * VM名
     */
    @JsonProperty("VmName")
    private String vmName;

    /**
     * ストレージタイプ名
     */
    @JsonProperty("StorageTypeName")
    private String storageTypeName;

    /**
     * インスタンスタイプ
     */
    @JsonProperty("InstanceType")
    private String instanceType;

    /**
     * キーペア名
     */
    @JsonProperty("KeyName")
    private String keyName;

    /**
     * ステータス
     */
    @JsonProperty("Status")
    private String status;

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

    /**
     * VCloudネットワーク情報の一覧
     */
    @JsonProperty("VcloudNetworks")
    private List<VcloudInstanceNetworkResponse> vcloudNetwoks;

    public VcloudInstanceResponse() {}

    public VcloudInstanceResponse(VcloudInstance vcloudInstance) {
        this.vmName = vcloudInstance.getVmName();
        this.instanceType = vcloudInstance.getInstanceType();
        this.status = vcloudInstance.getStatus();
        this.ipAddress = vcloudInstance.getIpAddress();
        this.privateIpAddress = vcloudInstance.getPrivateIpAddress();
    }

   /**
    *
    * vmNameを取得します。
    *
    * @return vmName
    */
    public String getVmName() {
        return vmName;
    }

   /**
    *
    * vmNameを設定します。
    *
    * @param vmName
    */
    public void setVmName(String vmName) {
        this.vmName = vmName;
    }

   /**
    *
    * storageTypeNameを取得します。
    *
    * @return storageTypeName
    */
    public String getStorageTypeName() {
        return storageTypeName;
    }

   /**
    *
    * storageTypeNameを設定します。
    *
    * @param storageTypeName
    */
    public void setStorageTypeName(String storageTypeName) {
        this.storageTypeName = storageTypeName;
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
     * VCloudネットワーク情報の一覧を取得します。
     * @return VCloudネットワーク情報の一覧
    */
    public List<VcloudInstanceNetworkResponse> getVcloudNetwoks() {
        return vcloudNetwoks;
    }

    /**
     * VCloudネットワーク情報の一覧を設定します。
     * @param vcloudNetwoks VCloudネットワーク情報の一覧
     */
    public void setVcloudNetwoks(List<VcloudInstanceNetworkResponse> vcloudNetwoks) {
        this.vcloudNetwoks = vcloudNetwoks;
    }

   /**
    *
    * VcloudNetwok を VcloudNetwoksに追加します。
    *
    * @param vcloudNetwok
    */
   public void addVcloudNetwok(VcloudInstanceNetworkResponse vcloudNetwok) {
       if (vcloudNetwoks == null) {
           vcloudNetwoks = new ArrayList<VcloudInstanceNetworkResponse>();
       }
       vcloudNetwoks.add(vcloudNetwok);
    }
}