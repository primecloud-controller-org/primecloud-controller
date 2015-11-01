package jp.primecloud.auto.api.response;

import org.apache.commons.lang.BooleanUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.entity.crud.PlatformAws;


public class PlatformAwsResponse {

    /**
     * ホスト(URL)
     */
    @JsonProperty("Host")
    private String host;

    /**
     * ポート番号
     */
    @JsonProperty("Port")
    private Integer port;

    /**
     * セキュア区分
     */
    @JsonProperty("Secure")
    private Boolean secure;

    /**
     * Eucalyptus区分
     */
    @JsonProperty("Euca")
    private Boolean euca;

    /**
     * VPC区分
     */
    @JsonProperty("Vpc")
    private Boolean vpc;

    /**
     * リージョン名
     */
    @JsonProperty("Region")
    private String region;

    /**
     * ゾーン名
     */
    @JsonProperty("AvailabilityZone")
    private String availabilityZone;

    /**
     * ゾーン名
     */
    @JsonProperty("VpcId")
    private String vpcId;

    /**
     * デフォルトキーペア
     */
    @JsonProperty("DefKeyPair")
    private String defKeyPair;

    /**
     * デフォルトサブネット(CidrBlock)
     */
    @JsonProperty("DefSubnet")
    private String defSubnet;

    /**
     * デフォルトロードバランササブネット(CidrBlock)
     */
    @JsonProperty("DefLbSubnet")
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