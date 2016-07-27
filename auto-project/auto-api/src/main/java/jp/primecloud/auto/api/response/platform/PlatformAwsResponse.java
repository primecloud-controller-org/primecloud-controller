package jp.primecloud.auto.api.response.platform;

import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.entity.crud.PlatformAws;

import org.apache.commons.lang.BooleanUtils;
import org.codehaus.jackson.annotate.JsonProperty;

public class PlatformAwsResponse {

    @JsonProperty("Host")
    private String host;

    @JsonProperty("Port")
    private Integer port;

    @JsonProperty("Secure")
    private Boolean secure;

    @JsonProperty("Euca")
    private Boolean euca;

    @JsonProperty("Vpc")
    private Boolean vpc;

    @JsonProperty("Region")
    private String region;

    @JsonProperty("AvailabilityZone")
    private String availabilityZone;

    @JsonProperty("VpcId")
    private String vpcId;

    @JsonProperty("KeyNames")
    private List<String> keyNames = new ArrayList<String>();

    @JsonProperty("SecurityGroups")
    private List<String> securityGroups = new ArrayList<String>();

    @JsonProperty("Subnets")
    private List<String> subnets = new ArrayList<String>();

    @JsonProperty("DefKeyPair")
    private String defKeyPair;

    @JsonProperty("DefSubnet")
    private String defSubnet;

    @JsonProperty("DefLbSubnet")
    private String defLbSubnet;

    public PlatformAwsResponse(PlatformAws aws) {
        this.host = aws.getHost();
        this.port = aws.getPort();
        this.secure = BooleanUtils.isTrue(aws.getSecure());
        this.euca = BooleanUtils.isTrue(aws.getEuca());
        this.vpc = BooleanUtils.isTrue(aws.getVpc());
        this.region = aws.getRegion();
        this.availabilityZone = aws.getAvailabilityZone();
        this.vpcId = aws.getVpcId();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Boolean getSecure() {
        return secure;
    }

    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    public Boolean getEuca() {
        return euca;
    }

    public void setEuca(Boolean euca) {
        this.euca = euca;
    }

    public Boolean getVpc() {
        return vpc;
    }

    public void setVpc(Boolean vpc) {
        this.vpc = vpc;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAvailabilityZone() {
        return availabilityZone;
    }

    public void setAvailabilityZone(String availabilityZone) {
        this.availabilityZone = availabilityZone;
    }

    public String getVpcId() {
        return vpcId;
    }

    public void setVpcId(String vpcId) {
        this.vpcId = vpcId;
    }

    public List<String> getKeyNames() {
        return keyNames;
    }

    public void setKeyNames(List<String> keyNames) {
        this.keyNames = keyNames;
    }

    public List<String> getSecurityGroups() {
        return securityGroups;
    }

    public void setSecurityGroups(List<String> securityGroups) {
        this.securityGroups = securityGroups;
    }

    public List<String> getSubnets() {
        return subnets;
    }

    public void setSubnets(List<String> subnets) {
        this.subnets = subnets;
    }

    public String getDefKeyPair() {
        return defKeyPair;
    }

    public void setDefKeyPair(String defKeyPair) {
        this.defKeyPair = defKeyPair;
    }

    public String getDefSubnet() {
        return defSubnet;
    }

    public void setDefSubnet(String defSubnet) {
        this.defSubnet = defSubnet;
    }

    public String getDefLbSubnet() {
        return defLbSubnet;
    }

    public void setDefLbSubnet(String defLbSubnet) {
        this.defLbSubnet = defLbSubnet;
    }

}
