package jp.primecloud.auto.api.response;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import jp.primecloud.auto.entity.crud.PlatformAws;

import org.apache.commons.lang.BooleanUtils;



@XmlRootElement(name="PlatformAwsResponse")
@XmlType(propOrder = {"host", "port", "secure", "euca", "vpc", "region", "availabilityZone", "vpcId", "defKeyPair", "defSubnet",  "defLbSubnet"})
public class PlatformAwsResponse {

    /**
     * ホスト(URL)
     */
    private String host;

    /**
     * ポート番号
     */
    private Integer port;

    /**
     * セキュア区分
     */
    private Boolean secure;

    /**
     * Eucalyptus区分
     */
    private Boolean euca;

    /**
     * VPC区分
     */
    private Boolean vpc;

    /**
     * リージョン名
     */
    private String region;

    /**
     * ゾーン名
     */
    private String availabilityZone;

    /**
     * ゾーン名
     */
    private String vpcId;

    /**
     * デフォルトキーペア
     */
    private String defKeyPair;

    /**
     * デフォルトサブネット(CidrBlock)
     */
    private String defSubnet;

    /**
     * デフォルトロードバランササブネット(CidrBlock)
     */
    private String defLbSubnet;

    public PlatformAwsResponse(){}

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

   /**
    *
    * hostを取得します。
    *
    * @return host
    */
    @XmlElement(name="Host")
    public String getHost() {
        return host;
    }

   /**
    *
    * hostを設定します。
    *
    * @param host
    */
    public void setHost(String host) {
        this.host = host;
    }

   /**
    *
    * portを取得します。
    *
    * @return port
    */
    @XmlElement(name="Port")
    public Integer getPort() {
        return port;
    }

   /**
    *
    * portを設定します。
    *
    * @param port
    */
    public void setPort(Integer port) {
        this.port = port;
    }

   /**
    *
    * secureを取得します。
    *
    * @return secure
    */
    @XmlElement(name="Secure")
    public Boolean getSecure() {
        return secure;
    }

   /**
    *
    * secureを設定します。
    *
    * @param secure
    */
    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

   /**
    *
    * eucaを取得します。
    *
    * @return euca
    */
    @XmlElement(name="Euca")
    public Boolean getEuca() {
        return euca;
    }

   /**
    *
    * eucaを設定します。
    *
    * @param euca
    */
    public void setEuca(Boolean euca) {
        this.euca = euca;
    }

   /**
    *
    * vpcを取得します。
    *
    * @return vpc
    */
    @XmlElement(name="Vpc")
    public Boolean getVpc() {
        return vpc;
    }

   /**
    *
    * vpcを設定します。
    *
    * @param vpc
    */
    public void setVpc(Boolean vpc) {
        this.vpc = vpc;
    }

   /**
    *
    * regionを取得します。
    *
    * @return region
    */
    @XmlElement(name="Region")
    public String getRegion() {
        return region;
    }

   /**
    *
    * regionを設定します。
    *
    * @param region
    */
    public void setRegion(String region) {
        this.region = region;
    }

   /**
    *
    * availabilityZoneを取得します。
    *
    * @return availabilityZone
    */
    @XmlElement(name="AvailabilityZone")
    public String getAvailabilityZone() {
        return availabilityZone;
    }

   /**
    *
    * availabilityZoneを設定します。
    *
    * @param availabilityZone
    */
    public void setAvailabilityZone(String availabilityZone) {
        this.availabilityZone = availabilityZone;
    }

   /**
    *
    * vpcIdを取得します。
    *
    * @return vpcId
    */
    @XmlElement(name="VpcId")
    public String getVpcId() {
        return vpcId;
    }

   /**
    *
    * vpcIdを設定します。
    *
    * @param vpcId
    */
    public void setVpcId(String vpcId) {
        this.vpcId = vpcId;
    }

   /**
    *
    * defKeyPairを取得します。
    *
    * @return defKeyPair
    */
    @XmlElement(name="DefKeyPair")
    public String getDefKeyPair() {
        return defKeyPair;
    }

   /**
    *
    * defKeyPairを設定します。
    *
    * @param defKeyPair
    */
    public void setDefKeyPair(String defKeyPair) {
        this.defKeyPair = defKeyPair;
    }

   /**
    *
    * defSubnetを取得します。
    *
    * @return defSubnet
    */
    @XmlElement(name="DefSubnet")
    public String getDefSubnet() {
        return defSubnet;
    }

   /**
    *
    * defSubnetを設定します。
    *
    * @param defSubnet
    */
    public void setDefSubnet(String defSubnet) {
        this.defSubnet = defSubnet;
    }

   /**
    *
    * defLbSubnetを取得します。
    *
    * @return defLbSubnet
    */
    @XmlElement(name="DefLbSubnet")
    public String getDefLbSubnet() {
        return defLbSubnet;
    }

   /**
    *
    * defLbSubnetを設定します。
    *
    * @param defLbSubnet
    */
    public void setDefLbSubnet(String defLbSubnet) {
        this.defLbSubnet = defLbSubnet;
    }
}