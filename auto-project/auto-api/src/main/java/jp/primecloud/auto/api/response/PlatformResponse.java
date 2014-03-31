package jp.primecloud.auto.api.response;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import jp.primecloud.auto.entity.crud.Platform;

import org.apache.commons.lang.BooleanUtils;



@XmlRootElement(name="PlatformResponse")
@XmlType(propOrder = {"platformNo","platformName","internal","platformType","aws","vmware","nifty","cloudstack"})
public class PlatformResponse {

    /**
     * プラットフォーム番号
     */
    private Long platformNo;

    /**
     * プラットフォーム名
     */
    private String platformName;

    /**
     * 内外区分
     */
    private Boolean internal;

    /**
     * プラットフォーム区分
     */
    private String platformType;

    /**
     * プラットフォーム詳細情報(AWS)
     */
    private PlatformAwsResponse aws;

    /**
     * プラットフォーム詳細情報(VMware)
     */
    private PlatformVmwareResponse vmware;

    /**
     * プラットフォーム詳細情報(Nifty)
     */
    private PlatformNiftyResponse nifty;

    /**
     * プラットフォーム詳細情報(Cloudstack)
     */
    private PlatformCloudstackResponse cloudstack;

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
    @XmlElement(name="PlatformNo")
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
    @XmlElement(name="PlatformName")
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
    @XmlElement(name="Internal")
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
    @XmlElement(name="PlatformType")
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
    @XmlElement(name="AWS")
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
    @XmlElement(name="VMWARE")
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
    @XmlElement(name="NIFTY")
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
    @XmlElement(name="CLOUDSTACK")
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
}