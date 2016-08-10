package jp.primecloud.auto.api.response.instance;

import jp.primecloud.auto.entity.crud.Instance;

import org.codehaus.jackson.annotate.JsonProperty;

public class InstanceResponse {

    @JsonProperty("InstanceNo")
    private Long instanceNo;

    @JsonProperty("InstanceName")
    private String instanceName;

    @JsonProperty("FarmNo")
    private Long farmNo;

    @JsonProperty("PlatformNo")
    private Long platformNo;

    @JsonProperty("ImageNo")
    private Long imageNo;

    @JsonProperty("Comment")
    private String comment;

    @JsonProperty("Fqdn")
    private String fqdn;

    @JsonProperty("PublicIp")
    private String publicIp;

    @JsonProperty("PrivateIp")
    private String privateIp;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("Aws")
    private AwsInstanceResponse aws;

    @JsonProperty("Nifty")
    private NiftyInstanceResponse nifty;

    @JsonProperty("Vmware")
    private VmwareInstanceResponse vmware;

    @JsonProperty("Cloudstack")
    private CloudstackInstanceResponse cloudstack;

    @JsonProperty("Vcloud")
    private VcloudInstanceResponse vcloud;

    @JsonProperty("Openstack")
    private OpenstackInstanceResponse openstack;

    @JsonProperty("Azure")
    private AzureInstanceResponse azure;

    public InstanceResponse(Instance instance) {
        this.instanceNo = instance.getInstanceNo();
        this.instanceName = instance.getInstanceName();
        this.farmNo = instance.getFarmNo();
        this.platformNo = instance.getPlatformNo();
        this.imageNo = instance.getImageNo();
        this.comment = instance.getComment();
        this.fqdn = instance.getFqdn();
        this.publicIp = instance.getPublicIp();
        this.privateIp = instance.getPrivateIp();
        this.status = instance.getStatus();
    }

    public Long getInstanceNo() {
        return instanceNo;
    }

    public void setInstanceNo(Long instanceNo) {
        this.instanceNo = instanceNo;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public Long getFarmNo() {
        return farmNo;
    }

    public void setFarmNo(Long farmNo) {
        this.farmNo = farmNo;
    }

    public Long getPlatformNo() {
        return platformNo;
    }

    public void setPlatformNo(Long platformNo) {
        this.platformNo = platformNo;
    }

    public Long getImageNo() {
        return imageNo;
    }

    public void setImageNo(Long imageNo) {
        this.imageNo = imageNo;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFqdn() {
        return fqdn;
    }

    public void setFqdn(String fqdn) {
        this.fqdn = fqdn;
    }

    public String getPublicIp() {
        return publicIp;
    }

    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    public String getPrivateIp() {
        return privateIp;
    }

    public void setPrivateIp(String privateIp) {
        this.privateIp = privateIp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AwsInstanceResponse getAws() {
        return aws;
    }

    public void setAws(AwsInstanceResponse aws) {
        this.aws = aws;
    }

    public NiftyInstanceResponse getNifty() {
        return nifty;
    }

    public void setNifty(NiftyInstanceResponse nifty) {
        this.nifty = nifty;
    }

    public VmwareInstanceResponse getVmware() {
        return vmware;
    }

    public void setVmware(VmwareInstanceResponse vmware) {
        this.vmware = vmware;
    }

    public CloudstackInstanceResponse getCloudstack() {
        return cloudstack;
    }

    public void setCloudstack(CloudstackInstanceResponse cloudstack) {
        this.cloudstack = cloudstack;
    }

    public VcloudInstanceResponse getVcloud() {
        return vcloud;
    }

    public void setVcloud(VcloudInstanceResponse vcloud) {
        this.vcloud = vcloud;
    }

    public OpenstackInstanceResponse getOpenstack() {
        return openstack;
    }

    public void setOpenstack(OpenstackInstanceResponse openstack) {
        this.openstack = openstack;
    }

    public AzureInstanceResponse getAzure() {
        return azure;
    }

    public void setAzure(AzureInstanceResponse azure) {
        this.azure = azure;
    }

}
