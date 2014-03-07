package jp.primecloud.auto.api.response.instance;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import jp.primecloud.auto.entity.crud.Instance;



@XmlRootElement(name="InstanceResponse")
@XmlType(propOrder = { "instanceNo", "instanceName", "fqdn", "publicIp", "privateIp", "status" })
public class InstanceResponse {

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

    public InstanceResponse() {}

    public InstanceResponse(Instance instance) {
        this.instanceNo = instance.getInstanceNo();
        this.instanceName = instance.getInstanceName();
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
}