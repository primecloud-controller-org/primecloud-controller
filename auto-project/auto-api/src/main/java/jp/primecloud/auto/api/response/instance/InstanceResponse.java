package jp.primecloud.auto.api.response.instance;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.entity.crud.Instance;


public class InstanceResponse {

    /**
     * インスタンス番号
     */
    @JsonProperty("InstanceNo")
    private Long instanceNo;

    /**
     * インスタンス名
     */
    @JsonProperty("InstanceName")
    private String instanceName;

    @JsonProperty("Comment")
    private String comment;

    /**
     * FQDN
     */
    @JsonProperty("Fqdn")
    private String fqdn;

    /**
     * PublicIp
     */
    @JsonProperty("PublicIp")
    private String publicIp;

    /**
     * PrivateIp
     */
    @JsonProperty("PrivateIp")
    private String privateIp;

    /**
     * Status サーバのステータス
     */
    @JsonProperty("Status")
    private String status;

    /**
     * AWSインスタンス情報
     */
    @JsonProperty("Aws")
    private AwsInstanceResponse aws;

    /**
     * Niftyインスタンス情報
     */
    @JsonProperty("Nifty")
    private NiftyInstanceResponse nifty;

    /**
     * VMWareインスタンス情報
     */
    @JsonProperty("Vmware")
    private VmwareInstanceResponse vmware;

    /**
     * CloudStackインスタンス情報
     */
    @JsonProperty("Cloudstack")
    private CloudstackInstanceResponse cloudstack;

    /**
     * VCloudインスタンス情報
     */
    @JsonProperty("Vcloud")
    private VcloudInstanceResponse vcloud;

    /**
     * Openstackインスタンス情報
     */
    @JsonProperty("Openstack")
    private OpenstackInstanceResponse openstack;

    /**
     * Azureインスタンス情報
     */
    @JsonProperty("Azure")
    private AzureInstanceResponse azure;

    public InstanceResponse() {}

    public InstanceResponse(Instance instance) {
        this.instanceNo = instance.getInstanceNo();
        this.instanceName = instance.getInstanceName();
        this.comment = instance.getComment();
        this.fqdn = instance.getFqdn();
        this.publicIp = instance.getPublicIp();
        this.privateIp = instance.getPrivateIp();
        this.status = instance.getStatus();
    }

   /**
    *
    * instanceNoを取得します。
    *
    * @return instanceNo
    */
    public Long getInstanceNo() {
        return instanceNo;
    }

   /**
    *
    * instanceNoを設定します。
    *
    * @param instanceNo
    */
    public void setInstanceNo(Long instanceNo) {
        this.instanceNo = instanceNo;
    }

   /**
    *
    * instanceNameを取得します。
    *
    * @return instanceName
    */
    public String getInstanceName() {
        return instanceName;
    }

   /**
    *
    * instanceNameを設定します。
    *
    * @param instanceName
    */
    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

   /**
    *
    * fqdnを取得します。
    *
    * @return fqdn
    */
    public String getFqdn() {
        return fqdn;
    }

   /**
    *
    * fqdnを設定します。
    *
    * @param fqdn
    */
    public void setFqdn(String fqdn) {
        this.fqdn = fqdn;
    }

   /**
    *
    * publicIpを取得します。
    *
    * @return publicIp
    */
    public String getPublicIp() {
        return publicIp;
    }

   /**
    *
    * publicIpを設定します。
    *
    * @param publicIp
    */
    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

   /**
    *
    * privateIpを取得します。
    *
    * @return privateIp
    */
    public String getPrivateIp() {
        return privateIp;
    }

   /**
    *
    * privateIpを設定します。
    *
    * @param privateIp
    */
    public void setPrivateIp(String privateIp) {
        this.privateIp = privateIp;
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
     * awsを取得します。
     *
     * @return aws
     */
     public AwsInstanceResponse getAws() {
         return aws;
     }

    /**
     *
     * awsを設定します。
     *
     * @param aws
     */
     public void setAws(AwsInstanceResponse aws) {
         this.aws = aws;
     }

    /**
     *
     * niftyを取得します。
     *
     * @return nifty
     */
     public NiftyInstanceResponse getNifty() {
         return nifty;
     }

    /**
     *
     * niftyを設定します。
     *
     * @param nifty
     */
     public void setNifty(NiftyInstanceResponse nifty) {
         this.nifty = nifty;
     }

    /**
     *
     * vmwareを取得します。
     *
     * @return vmware
     */
     public VmwareInstanceResponse getVmware() {
         return vmware;
     }

    /**
     *
     * vmwareを設定します。
     *
     * @param vmware
     */
     public void setVmware(VmwareInstanceResponse vmware) {
         this.vmware = vmware;
     }

    /**
     *
     * cloudstackを取得します。
     *
     * @return cloudstack
     */
     public CloudstackInstanceResponse getCloudstack() {
         return cloudstack;
     }

    /**
     *
     * cloudstackを設定します。
     *
     * @param cloudstack
     */
     public void setCloudstack(CloudstackInstanceResponse cloudstack) {
         this.cloudstack = cloudstack;
     }

    /**
     *
     * vcloudを取得します。
     *
     * @return vcloud
     */
     public VcloudInstanceResponse getVcloud() {
         return vcloud;
     }

    /**
     *
     * vcloudを設定します。
     *
     * @param vcloud
     */
     public void setVcloud(VcloudInstanceResponse vcloud) {
         this.vcloud = vcloud;
     }

     /**
     *
     * openstackを取得します。
     *
     * @return openstack
     */
     public OpenstackInstanceResponse getOpenstack() {
         return openstack;
     }

    /**
     *
     * openstackを設定します。
     *
     * @param openstack
     */
     public void setOpenstack(OpenstackInstanceResponse openstack) {
         this.openstack = openstack;
     }

     /**
     *
     * azureを取得します。
     *
     * @return azure
     */
     public AzureInstanceResponse getAzure() {
         return azure;
     }

    /**
     *
     * azureを設定します。
     *
     * @param azure
     */
     public void setAzure(AzureInstanceResponse azure) {
         this.azure = azure;
     }

}