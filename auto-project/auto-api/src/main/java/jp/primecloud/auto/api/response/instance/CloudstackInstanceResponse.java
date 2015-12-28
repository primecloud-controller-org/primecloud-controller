package jp.primecloud.auto.api.response.instance;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.entity.crud.CloudstackInstance;


public class CloudstackInstanceResponse {

    @JsonProperty("KeyName")
    private String keyName;

    @JsonProperty("InstanceType")
    private String instanceType;

    @JsonProperty("Displayname")
    private String displayname;

    @JsonProperty("Ipaddress")
    private String ipaddress;

    @JsonProperty("State")
    private String state;

    @JsonProperty("Zoneid")
    private String zoneid;

    @JsonProperty("Networkid")
    private String networkid;

    @JsonProperty("Securitygroup")
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