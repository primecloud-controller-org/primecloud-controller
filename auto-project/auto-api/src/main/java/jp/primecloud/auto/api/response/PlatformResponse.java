package jp.primecloud.auto.api.response;

import org.apache.commons.lang.BooleanUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.entity.crud.Platform;


public class PlatformResponse {

    /**
     * プラットフォーム番号
     */
    @JsonProperty("PlatformNo")
    private Long platformNo;

    /**
     * プラットフォーム名
     */
    @JsonProperty("PlatformName")
    private String platformName;

    /**
     * 内外区分
     */
    @JsonProperty("Internal")
    private Boolean internal;

    /**
     * プラットフォーム区分
     */
    @JsonProperty("PlatformType")
    private String platformType;

    /**
     * プラットフォーム詳細情報(AWS)
     */
    @JsonProperty("Aws")
    private PlatformAwsResponse aws;

    /**
     * プラットフォーム詳細情報(VMware)
     */
    @JsonProperty("Vmware")
    private PlatformVmwareResponse vmware;

    /**
     * プラットフォーム詳細情報(Nifty)
     */
    @JsonProperty("Nifty")
    private PlatformNiftyResponse nifty;

    /**
     * プラットフォーム詳細情報(Cloudstack)
     */
    @JsonProperty("Cloudstack")
    private PlatformCloudstackResponse cloudstack;

    /**
     * プラットフォーム詳細情報(VCloud)
     */
    @JsonProperty("Vcloud")
    private PlatformVcloudResponse vcloud;

    /**
     * プラットフォーム詳細情報(Openstack)
     */
    @JsonProperty("Openstack")
    private PlatformOpenstackResponse openstack;

    /**
     * プラットフォーム詳細情報(Azure)
     */
    @JsonProperty("Azure")
    private PlatformAzureResponse azure;

    public PlatformResponse() {}

    public PlatformResponse(Platform platform) {
        this.platformNo = platform.getPlatformNo();
        this.platformName = platform.getPlatformNameDisp();
        this.internal = BooleanUtils.isTrue(platform.getInternal());
        this.platformType = platform.getPlatformType();
    }

   /**
    *
    * platformNoを取得します。
    *
    * @return platformNo
    */
    public Long getPlatformNo() {
        return platformNo;
    }

   /**
    *
    * platformNoを設定
    *
    * @param platformNo
    */
    public void setPlatformNo(Long platformNo) {
        this.platformNo = platformNo;
    }

   /**
    *
    * platformNameを取得します。
    *
    * @return platformName
    */
    public String getPlatformName() {
        return platformName;
    }

   /**
    *
    * platformNameを設定
    *
    * @param platformName
    */
    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

   /**
    *
    * internalを取得します。
    *
    * @return internal
    */
    public Boolean getInternal() {
        return internal;
    }

   /**
    *
    * internalを設定
    *
    * @param internal
    */
    public void setInternal(Boolean internal) {
        this.internal = internal;
    }

   /**
    *
    * platformTypeを取得します。
    *
    * @return platformType
    */
    public String getPlatformType() {
        return platformType;
    }

   /**
    *
    * platformTypeを設定
    *
    * @param platformType
    */
    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

   /**
    *
    * awsを取得します。
    *
    * @return aws
    */
    public PlatformAwsResponse getAws() {
        return aws;
    }

   /**
    *
    * awsを設定
    *
    * @param aws
    */
    public void setAws(PlatformAwsResponse aws) {
        this.aws = aws;
    }

   /**
    *
    * vmwareを取得します。
    *
    * @return vmware
    */
    public PlatformVmwareResponse getVmware() {
        return vmware;
    }

   /**
    *
    * vmwareを設定
    *
    * @param vmware
    */
    public void setVmware(PlatformVmwareResponse vmware) {
        this.vmware = vmware;
    }

   /**
    *
    * niftyを取得します。
    *
    * @return nifty
    */
    public PlatformNiftyResponse getNifty() {
        return nifty;
    }

   /**
    *
    * niftyを設定
    *
    * @param nifty
    */
    public void setNifty(PlatformNiftyResponse nifty) {
        this.nifty = nifty;
    }

   /**
    *
    * cloudstackを取得します。
    *
    * @return cloudstack
    */
    public PlatformCloudstackResponse getCloudstack() {
        return cloudstack;
    }

   /**
    *
    * cloudstackを設定
    *
    * @param cloudstack
    */
    public void setCloudstack(PlatformCloudstackResponse cloudstack) {
        this.cloudstack = cloudstack;
    }

   /**
    *
    * vcloudを取得します。
    *
    * @return vcloud
    */
    public PlatformVcloudResponse getVcloud() {
        return vcloud;
    }

   /**
    *
    * vcloudを設定
    *
    * @param vcloud
    */
    public void setVcloud(PlatformVcloudResponse vcloud) {
        this.vcloud = vcloud;
    }

        /**
        *
        * openstackを取得します。
        *
        * @return openstack
        */
        public PlatformOpenstackResponse getOpenstack() {
            return openstack;
        }

       /**
        *
        * openstackを設定
        *
        * @param openstack
        */
        public void setOpenstack(PlatformOpenstackResponse openstack) {
            this.openstack = openstack;
        }

        /**
        *
        * azureを取得します。
        *
        * @return azure
        */
        public PlatformAzureResponse getAzure() {
            return azure;
        }

       /**
        *
        * azureを設定
        *
        * @param azure
        */
        public void setAzure(PlatformAzureResponse azure) {
            this.azure = azure;
        }
}