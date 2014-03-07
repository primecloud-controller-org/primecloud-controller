package jp.primecloud.auto.api.response.instance;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import jp.primecloud.auto.entity.crud.CloudstackInstance;



@XmlRootElement(name="CloudstackInstanceResponse")
@XmlType(propOrder = { "keyName", "instanceType", "displayname", "ipaddress", "state", "zoneid" ,"networkid", "securitygroup" })
public class CloudstackInstanceResponse {

    private String keyName;
    private String instanceType;
    private String displayname;
    private String ipaddress;
    private String state;
    private String zoneid;
    private String networkid;
    private String securitygroup;

    public CloudstackInstanceResponse() {}

    public CloudstackInstanceResponse(CloudstackInstance cloudstackInstance) {
        this.keyName = cloudstackInstance.getKeyName();
        this.instanceType = cloudstackInstance.getInstanceType();
        this.displayname = cloudstackInstance.getDisplayname();
        this.ipaddress = cloudstackInstance.getIpaddress();
        this.state = cloudstackInstance.getState();
        this.zoneid = cloudstackInstance.getZoneid();
        this.networkid = cloudstackInstance.getNetworkid();
        this.securitygroup = cloudstackInstance.getSecuritygroup();
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
    * displaynameを取得します。
    *
    * @return displayname
    */
    @XmlElement(name="Displayname")
    public String getDisplayname() {
        return displayname;
    }

   /**
    *
    * displaynameを設定します。
    *
    * @param displayname
    */
    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

   /**
    *
    * ipaddressを取得します。
    *
    * @return ipaddress
    */
    @XmlElement(name="Ipaddress")
    public String getIpaddress() {
        return ipaddress;
    }

   /**
    *
    * ipaddressを設定します。
    *
    * @param ipaddress
    */
    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

   /**
    *
    * stateを取得します。
    *
    * @return state
    */
    @XmlElement(name="State")
    public String getState() {
        return state;
    }

   /**
    *
    * successを設定します。
    *
    * @param success
    */
    public void setState(String state) {
        this.state = state;
    }

   /**
    *
    * zoneidを取得します。
    *
    * @return zoneid
    */
    @XmlElement(name="Zoneid")
    public String getZoneid() {
        return zoneid;
    }

   /**
    *
    * zoneidを設定します。
    *
    * @param zoneid
    */
    public void setZoneid(String zoneid) {
        this.zoneid = zoneid;
    }

   /**
    *
    * networkidを取得します。
    *
    * @return networkid
    */
    @XmlElement(name="Networkid")
    public String getNetworkid() {
        return networkid;
    }

   /**
    *
    * networkidを設定します。
    *
    * @param networkid
    */
    public void setNetworkid(String networkid) {
        this.networkid = networkid;
    }

   /**
    *
    * securitygroupを取得します。
    *
    * @return securitygroup
    */
    @XmlElement(name="Securitygroup")
    public String getSecuritygroup() {
        return securitygroup;
    }

   /**
    *
    * securitygroupを設定します。
    *
    * @param securitygroup
    */
    public void setSecuritygroup(String securitygroup) {
        this.securitygroup = securitygroup;
    }
}