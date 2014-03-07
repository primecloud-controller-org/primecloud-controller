package jp.primecloud.auto.api.response.instance;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import jp.primecloud.auto.entity.crud.NiftyInstance;

import org.apache.commons.lang.BooleanUtils;



@XmlRootElement(name="NiftyInstanceResponse")
@XmlType(propOrder = {"keyName","instanceType","status","dnsName","privateDnsName","ipAddress","privateIpAddress","initialized" })
public class NiftyInstanceResponse {

    /**
     * キーペア名
     */
    private String keyName;

    /**
     * インスタンスタイプ
     */
    private String instanceType;

    /**
     * ステータス
     */
    private String status;

    /**
     * パブリックDNS
     */
    private String dnsName;

    /**
     * プライベートDNS
     */
    private String privateDnsName;

    /**
     * パブリックIPアドレス
     */
    private String ipAddress;

    /**
     * プライベートIPアドレス
     */
    private String privateIpAddress;

    /**
     * 初期化済みかどうか true:初期化済み、false:初期化済みではない
     */
    private Boolean initialized;

    public NiftyInstanceResponse() {}

    public NiftyInstanceResponse(NiftyInstance niftyInstance) {
        this.instanceType = niftyInstance.getInstanceType();
        this.status = niftyInstance.getStatus();
        this.dnsName = niftyInstance.getDnsName();
        this.privateDnsName = niftyInstance.getPrivateDnsName();
        this.ipAddress = niftyInstance.getIpAddress();
        this.privateIpAddress = niftyInstance.getPrivateIpAddress();
        this.initialized = BooleanUtils.isTrue(niftyInstance.getInitialized());
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
    * dnsNameを取得します。
    *
    * @return dnsName
    */
    @XmlElement(name="DnsName")
    public String getDnsName() {
        return dnsName;
    }

   /**
    *
    * dnsNameを設定します。
    *
    * @param dnsName
    */
    public void setDnsName(String dnsName) {
        this.dnsName = dnsName;
    }

   /**
    *
    * privateDnsNameを取得します。
    *
    * @return privateDnsName
    */
    @XmlElement(name="PrivateDnsName")
    public String getPrivateDnsName() {
        return privateDnsName;
    }

   /**
    *
    * privateDnsNameを設定します。
    *
    * @param privateDnsName
    */
    public void setPrivateDnsName(String privateDnsName) {
        this.privateDnsName = privateDnsName;
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
    * initializedを取得します。
    *
    * @return initialized
    */
    @XmlElement(name="Initialized")
    public Boolean getInitialized() {
        return initialized;
    }

   /**
    *
    * initializedを設定します。
    *
    * @param initialized
    */
    public void setInitialized(Boolean initialized) {
        this.initialized = initialized;
    }
}