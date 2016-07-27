package jp.primecloud.auto.api.response.platform;

import jp.primecloud.auto.entity.crud.Platform;

import org.apache.commons.lang.BooleanUtils;
import org.codehaus.jackson.annotate.JsonProperty;

public class PlatformResponse {

    @JsonProperty("PlatformNo")
    private Long platformNo;

    @JsonProperty("PlatformName")
    private String platformName;

    @JsonProperty("Internal")
    private Boolean internal;

    @JsonProperty("PlatformType")
    private String platformType;

    @JsonProperty("Aws")
    private PlatformAwsResponse aws;

    @JsonProperty("Vmware")
    private PlatformVmwareResponse vmware;

    @JsonProperty("Nifty")
    private PlatformNiftyResponse nifty;

    @JsonProperty("Cloudstack")
    private PlatformCloudstackResponse cloudstack;

    @JsonProperty("Vcloud")
    private PlatformVcloudResponse vcloud;

    @JsonProperty("Openstack")
    private PlatformOpenstackResponse openstack;

    @JsonProperty("Azure")
    private PlatformAzureResponse azure;

    public PlatformResponse() {
    }

    public PlatformResponse(Platform platform) {
        this.platformNo = platform.getPlatformNo();
        this.platformName = platform.getPlatformNameDisp();
        this.internal = BooleanUtils.isTrue(platform.getInternal());
        this.platformType = platform.getPlatformType();
    }

    public Long getPlatformNo() {
        return platformNo;
    }

    public void setPlatformNo(Long platformNo) {
        this.platformNo = platformNo;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public Boolean getInternal() {
        return internal;
    }

    public void setInternal(Boolean internal) {
        this.internal = internal;
    }

    public String getPlatformType() {
        return platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    public PlatformAwsResponse getAws() {
        return aws;
    }

    public void setAws(PlatformAwsResponse aws) {
        this.aws = aws;
    }

    public PlatformVmwareResponse getVmware() {
        return vmware;
    }

    public void setVmware(PlatformVmwareResponse vmware) {
        this.vmware = vmware;
    }

    public PlatformNiftyResponse getNifty() {
        return nifty;
    }

    public void setNifty(PlatformNiftyResponse nifty) {
        this.nifty = nifty;
    }

    public PlatformCloudstackResponse getCloudstack() {
        return cloudstack;
    }

    public void setCloudstack(PlatformCloudstackResponse cloudstack) {
        this.cloudstack = cloudstack;
    }

    public PlatformVcloudResponse getVcloud() {
        return vcloud;
    }

    public void setVcloud(PlatformVcloudResponse vcloud) {
        this.vcloud = vcloud;
    }

    public PlatformOpenstackResponse getOpenstack() {
        return openstack;
    }

    public void setOpenstack(PlatformOpenstackResponse openstack) {
        this.openstack = openstack;
    }

    public PlatformAzureResponse getAzure() {
        return azure;
    }

    public void setAzure(PlatformAzureResponse azure) {
        this.azure = azure;
    }

}
