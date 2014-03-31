package jp.primecloud.auto.service.dto;

import java.io.Serializable;

public class SubnetDto implements Serializable {

    /** TODO: フィールドコメントを記述 */
    private static final long serialVersionUID = 5369535476093296289L;
    private String subnetId;
    private String zoneid;
    private String cidrBlock;

    public String getSubnetId() {
        return subnetId;
    }
    public void setSubnetId(String subnetId) {
        this.subnetId = subnetId;
    }
    public String getZoneid() {
        return zoneid;
    }
    public void setZoneid(String zoneid) {
        this.zoneid = zoneid;
    }
    public String getCidrBlock() {
        return cidrBlock;
    }
    public void setCidrBlock(String cidrBlock) {
        this.cidrBlock = cidrBlock;
    }

    public SubnetDto withSubnetId(String subnetId) {
        this.subnetId = subnetId;
        return this;
    }

    public SubnetDto withZone(String zone) {
        this.zoneid = zone;
        return this;
    }

    public SubnetDto withCidrBlock(String cidrBlock) {
        this.cidrBlock = cidrBlock;
        return this;
    }
}
