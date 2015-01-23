package jp.primecloud.auto.api.response.instance;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import jp.primecloud.auto.entity.crud.Instance;


@XmlRootElement(name="DescribeInstanceResponse")
@XmlType(propOrder = { "success", "message", "instanceNo", "instanceName", "fqdn", "publicIp", "privateIp", "status", "aws", "nifty", "vmware", "cloudstack", "vcloud" , "openstack" , "azure"})
public class DescribeInstanceResponse {

    /**
     * 処理の成否 true:正常終了、false:エラー
     */
    private boolean success;

    /**
     * メッセージ 正常終了の場合：Null、エラーの場合：エラーメッセージ
     */
    private String message;

    /**
     * インスタンス番号
     */
    private Long instanceNo;

    /**
     * インスタンス名
     */
    private String instanceName;

    /**
     * FQDN
     */
    private String fqdn;

    /**
     * PublicIp
     */
    private String publicIp;

    /**
     * PrivateIp
     */
    private String privateIp;

    /**
     * Status サーバのステータス
     */
    private String status;

    /**
     * AWSインスタンス情報
     */
    private AwsInstanceResponse aws;

    /**
     * Niftyインスタンス情報
     */
    private NiftyInstanceResponse nifty;

    /**
     * VMWareインスタンス情報
     */
    private VmwareInstanceResponse vmware;

    /**
     * CloudStackインスタンス情報
     */
    private CloudstackInstanceResponse cloudstack;

    /**
     * VCloudインスタンス情報
     */
    private VcloudInstanceResponse vcloud;

    /**
     * Openstackインスタンス情報
     */
    private OpenstackInstanceResponse openstack;

    /**
     * Azureインスタンス情報
     */
    private AzureInstanceResponse azure;

    public DescribeInstanceResponse() {}

    public DescribeInstanceResponse(Instance instance) {
        this.instanceNo = instance.getInstanceNo();
        this.instanceName = instance.getInstanceName();
        this.fqdn = instance.getFqdn();
        this.publicIp = instance.getPublicIp();
        this.privateIp = instance.getPrivateIp();
        this.status = instance.getStatus();
    }

   /**
    *
    * successを取得します。
    *
    * @return success
    */
    @XmlElement(name="SUCCESS")
    public boolean isSuccess() {
        return success;
    }

   /**
    *
    * successを設定します。
    *
    * @param success
    */
    public void setSuccess(boolean success) {
        this.success = success;
    }

   /**
    *
    * messageを取得します。
    *
    * @return success
    */
    @XmlElement(name="Message")
    public String getMessage() {
        return message;
    }

   /**
    *
    * messageを設定します。
    *
    * @param message
    */
    public void setMessage(String message) {
        this.message = message;
    }

   /**
    *
    * instanceNoを取得します。
    *
    * @return instanceNo
    */
    @XmlElement(name="InstanceNo")
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
    @XmlElement(name="InstanceName")
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

   /**
    *
    * fqdnを取得します。
    *
    * @return fqdn
    */
    @XmlElement(name="Fqdn")
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
    @XmlElement(name="PublicIp")
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
    @XmlElement(name="PrivateIp")
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
    @XmlElement(name="Status")
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
    @XmlElement(name="AWS")
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
    @XmlElement(name="NIFTY")
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
    @XmlElement(name="VMWARE")
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
    @XmlElement(name="CLOUDSTACK")
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
    @XmlElement(name="VCLOUD")
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
    @XmlElement(name="OPENSTACK")
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
    @XmlElement(name="AZURE")
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