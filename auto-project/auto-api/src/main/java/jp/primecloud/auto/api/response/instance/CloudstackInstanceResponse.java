package jp.primecloud.auto.api.response.instance;

import jp.primecloud.auto.entity.crud.CloudstackInstance;

import org.codehaus.jackson.annotate.JsonProperty;

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

    public CloudstackInstanceResponse() {
    }

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

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getIpaddress() {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZoneid() {
        return zoneid;
    }

    public void setZoneid(String zoneid) {
        this.zoneid = zoneid;
    }

    public String getNetworkid() {
        return networkid;
    }

    public void setNetworkid(String networkid) {
        this.networkid = networkid;
    }

    public String getSecuritygroup() {
        return securitygroup;
    }

    public void setSecuritygroup(String securitygroup) {
        this.securitygroup = securitygroup;
    }

}
