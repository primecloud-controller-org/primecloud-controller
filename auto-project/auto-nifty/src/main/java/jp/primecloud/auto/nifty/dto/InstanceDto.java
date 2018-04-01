package jp.primecloud.auto.nifty.dto;

import java.io.Serializable;
import java.util.Date;

import com.nifty.cloud.sdk.server.model.Instance;

/**
 * <p>
 * nifty sdkのInstanceクラスからのデータ移送用クラス
 * </p>
 *
 */
public class InstanceDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String accountingType;

    private String admin;

    private String amiLaunchIndex;

    private String architecture;

    private String copyInfo;

    private String description;

    private String dnsName;

    private String imageId;

    private String instanceId;

    private String instanceLifecycle;

    private String instanceType;

    private String ipAddress;

    private String ipaddressV6;

    private String ipType;

    private String kernelId;

    private String keyName;

    private Date launchTime;

    private String nextMonthAccountingType;

    private String niftyPrivateIpType;

    private String platform;

    private String privateDnsName;

    private String privateIpAddress;

    private String privateIpAddressV6;

    private String ramdiskId;

    private String reason;

    private String rootDeviceName;

    private String rootDeviceType;

    private String spotInstanceRequestId;

    private InstanceStateDto state;

    private String subnetId;

    private String vpcId;

    /**
     * accountingTypeを取得します。
     * @return accountingType
     */
    public String getAccountingType() {
        return accountingType;
    }

    /**
     * accountingTypeを設定します。
     * @param accountingType accountingType
     */
    public void setAccountingType(String accountingType) {
        this.accountingType = accountingType;
    }

    /**
     * adminを取得します。
     * @return admin
     */
    public String getAdmin() {
        return admin;
    }

    /**
     * adminを設定します。
     * @param admin admin
     */
    public void setAdmin(String admin) {
        this.admin = admin;
    }

    /**
     * amiLaunchIndexを取得します。
     * @return amiLaunchIndex
     */
    public String getAmiLaunchIndex() {
        return amiLaunchIndex;
    }

    /**
     * amiLaunchIndexを設定します。
     * @param amiLaunchIndex amiLaunchIndex
     */
    public void setAmiLaunchIndex(String amiLaunchIndex) {
        this.amiLaunchIndex = amiLaunchIndex;
    }

    /**
     * architectureを取得します。
     * @return architecture
     */
    public String getArchitecture() {
        return architecture;
    }

    /**
     * architectureを設定します。
     * @param architecture architecture
     */
    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    /**
     * copyInfoを取得します。
     * @return copyInfo
     */
    public String getCopyInfo() {
        return copyInfo;
    }

    /**
     * copyInfoを設定します。
     * @param copyInfo copyInfo
     */
    public void setCopyInfo(String copyInfo) {
        this.copyInfo = copyInfo;
    }

    /**
     * descriptionを取得します。
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * descriptionを設定します。
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * dnsNameを取得します。
     * @return dnsName
     */
    public String getDnsName() {
        return dnsName;
    }

    /**
     * dnsNameを設定します。
     * @param dnsName dnsName
     */
    public void setDnsName(String dnsName) {
        this.dnsName = dnsName;
    }

    /**
     * imageIdを取得します。
     * @return imageId
     */
    public String getImageId() {
        return imageId;
    }

    /**
     * imageIdを設定します。
     * @param imageId imageId
     */
    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    /**
     * instanceIdを取得します。
     * @return instanceId
     */
    public String getInstanceId() {
        return instanceId;
    }

    /**
     * instanceIdを設定します。
     * @param instanceId instanceId
     */
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    /**
     * instanceLifecycleを取得します。
     * @return instanceLifecycle
     */
    public String getInstanceLifecycle() {
        return instanceLifecycle;
    }

    /**
     * instanceLifecycleを設定します。
     * @param instanceLifecycle instanceLifecycle
     */
    public void setInstanceLifecycle(String instanceLifecycle) {
        this.instanceLifecycle = instanceLifecycle;
    }

    /**
     * instanceTypeを取得します。
     * @return instanceType
     */
    public String getInstanceType() {
        return instanceType;
    }

    /**
     * instanceTypeを設定します。
     * @param instanceType instanceType
     */
    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    /**
     * ipAddressを取得します。
     * @return ipAddress
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * ipAddressを設定します。
     * @param ipAddress ipAddress
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * ipaddressV6を取得します。
     * @return ipaddressV6
     */
    public String getIpaddressV6() {
        return ipaddressV6;
    }

    /**
     * ipaddressV6を設定します。
     * @param ipaddressV6 ipaddressV6
     */
    public void setIpaddressV6(String ipaddressV6) {
        this.ipaddressV6 = ipaddressV6;
    }

    /**
     * ipTypeを取得します。
     * @return ipType
     */
    public String getIpType() {
        return ipType;
    }

    /**
     * ipTypeを設定します。
     * @param ipType ipType
     */
    public void setIpType(String ipType) {
        this.ipType = ipType;
    }

    /**
     * kernelIdを取得します。
     * @return kernelId
     */
    public String getKernelId() {
        return kernelId;
    }

    /**
     * kernelIdを設定します。
     * @param kernelId kernelId
     */
    public void setKernelId(String kernelId) {
        this.kernelId = kernelId;
    }

    /**
     * keyNameを取得します。
     * @return keyName
     */
    public String getKeyName() {
        return keyName;
    }

    /**
     * keyNameを設定します。
     * @param keyName keyName
     */
    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    /**
     * launchTimeを取得します。
     * @return launchTime
     */
    public Date getLaunchTime() {
        return launchTime;
    }

    /**
     * launchTimeを設定します。
     * @param launchTime launchTime
     */
    public void setLaunchTime(Date launchTime) {
        this.launchTime = launchTime;
    }

    /**
     * nextMonthAccountingTypeを取得します。
     * @return nextMonthAccountingType
     */
    public String getNextMonthAccountingType() {
        return nextMonthAccountingType;
    }

    /**
     * nextMonthAccountingTypeを設定します。
     * @param nextMonthAccountingType nextMonthAccountingType
     */
    public void setNextMonthAccountingType(String nextMonthAccountingType) {
        this.nextMonthAccountingType = nextMonthAccountingType;
    }

    /**
     * niftyPrivateIpTypeを取得します。
     * @return niftyPrivateIpType
     */
    public String getNiftyPrivateIpType() {
        return niftyPrivateIpType;
    }

    /**
     * niftyPrivateIpTypeを設定します。
     * @param niftyPrivateIpType niftyPrivateIpType
     */
    public void setNiftyPrivateIpType(String niftyPrivateIpType) {
        this.niftyPrivateIpType = niftyPrivateIpType;
    }

    /**
     * platformを取得します。
     * @return platform
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * platformを設定します。
     * @param platform platform
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     * privateDnsNameを取得します。
     * @return privateDnsName
     */
    public String getPrivateDnsName() {
        return privateDnsName;
    }

    /**
     * privateDnsNameを設定します。
     * @param privateDnsName privateDnsName
     */
    public void setPrivateDnsName(String privateDnsName) {
        this.privateDnsName = privateDnsName;
    }

    /**
     * privateIpAddressを取得します。
     * @return privateIpAddress
     */
    public String getPrivateIpAddress() {
        return privateIpAddress;
    }

    /**
     * privateIpAddressを設定します。
     * @param privateIpAddress privateIpAddress
     */
    public void setPrivateIpAddress(String privateIpAddress) {
        this.privateIpAddress = privateIpAddress;
    }

    /**
     * privateIpAddressV6を取得します。
     * @return privateIpAddressV6
     */
    public String getPrivateIpAddressV6() {
        return privateIpAddressV6;
    }

    /**
     * privateIpAddressV6を設定します。
     * @param privateIpAddressV6 privateIpAddressV6
     */
    public void setPrivateIpAddressV6(String privateIpAddressV6) {
        this.privateIpAddressV6 = privateIpAddressV6;
    }

    /**
     * ramdiskIdを取得します。
     * @return ramdiskId
     */
    public String getRamdiskId() {
        return ramdiskId;
    }

    /**
     * ramdiskIdを設定します。
     * @param ramdiskId ramdiskId
     */
    public void setRamdiskId(String ramdiskId) {
        this.ramdiskId = ramdiskId;
    }

    /**
     * reasonを取得します。
     * @return reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * reasonを設定します。
     * @param reason reason
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * rootDeviceNameを取得します。
     * @return rootDeviceName
     */
    public String getRootDeviceName() {
        return rootDeviceName;
    }

    /**
     * rootDeviceNameを設定します。
     * @param rootDeviceName rootDeviceName
     */
    public void setRootDeviceName(String rootDeviceName) {
        this.rootDeviceName = rootDeviceName;
    }

    /**
     * rootDeviceTypeを取得します。
     * @return rootDeviceType
     */
    public String getRootDeviceType() {
        return rootDeviceType;
    }

    /**
     * rootDeviceTypeを設定します。
     * @param rootDeviceType rootDeviceType
     */
    public void setRootDeviceType(String rootDeviceType) {
        this.rootDeviceType = rootDeviceType;
    }

    /**
     * spotInstanceRequestIdを取得します。
     * @return spotInstanceRequestId
     */
    public String getSpotInstanceRequestId() {
        return spotInstanceRequestId;
    }

    /**
     * spotInstanceRequestIdを設定します。
     * @param spotInstanceRequestId spotInstanceRequestId
     */
    public void setSpotInstanceRequestId(String spotInstanceRequestId) {
        this.spotInstanceRequestId = spotInstanceRequestId;
    }

    /**
     * stateを取得します。
     * @return state
     */
    public InstanceStateDto getState() {
        return state;
    }

    /**
     * stateを設定します。
     * @param state state
     */
    public void setState(InstanceStateDto state) {
        this.state = state;
    }

    /**
     * subnetIdを取得します。
     * @return subnetId
     */
    public String getSubnetId() {
        return subnetId;
    }

    /**
     * subnetIdを設定します。
     * @param subnetId subnetId
     */
    public void setSubnetId(String subnetId) {
        this.subnetId = subnetId;
    }

    /**
     * vpcIdを取得します。
     * @return vpcId
     */
    public String getVpcId() {
        return vpcId;
    }

    /**
     * vpcIdを設定します。
     * @param vpcId vpcId
     */
    public void setVpcId(String vpcId) {
        this.vpcId = vpcId;
    }

    public InstanceDto(Instance instance) {
        accountingType = instance.getAccountingType();
        admin = instance.getAdmin();
        amiLaunchIndex = instance.getAmiLaunchIndex();
        architecture = instance.getArchitecture();
        copyInfo = instance.getCopyInfo();
        description = instance.getDescription();
        dnsName = instance.getDnsName();
        imageId = instance.getImageId();
        instanceId = instance.getInstanceId();
        instanceLifecycle = instance.getInstanceLifecycle();
        instanceType = instance.getInstanceType();
        ipAddress = instance.getIpAddress();
        ipaddressV6 = instance.getIpAddressV6();
        ipType = instance.getIpType();
        kernelId = instance.getKernelId();
        keyName = instance.getKeyName();
        launchTime = instance.getLaunchTime();
        nextMonthAccountingType = instance.getNextMonthAccountingType();
        niftyPrivateIpType = instance.getNiftyPrivateIpType();
        platform = instance.getPlatform();
        privateDnsName = instance.getPrivateDnsName();
        privateIpAddress = instance.getPrivateIpAddress();
        privateIpAddressV6 = instance.getPrivateIpAddressV6();
        ramdiskId = instance.getRamdiskId();
        reason = instance.getReason();
        rootDeviceName = instance.getRootDeviceName();
        rootDeviceType = instance.getRootDeviceType();
        spotInstanceRequestId = instance.getSpotInstanceRequestId();
        state = new InstanceStateDto(instance.getState());
        subnetId = instance.getSubnetId();
        vpcId = instance.getVpcId();
    }
}
