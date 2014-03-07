package jp.primecloud.auto.api.response;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import jp.primecloud.auto.entity.crud.PlatformCloudstack;

import org.apache.commons.lang.BooleanUtils;



@XmlRootElement(name="PlatformCloudstackResponse")
@XmlType(propOrder = {"host","path","port","secure","zoneId","networkId","defKeyPair"})
public class PlatformCloudstackResponse {

    /**
     * ホストURL
     */
    private String host;

    /**
     * パス(ホスト以下のパス)
     */
    private String path;

    /**
     * ポート番号
     */
    private Integer port;

    /**
     * セキュア区分
     */
    private Boolean secure;

    /**
     * ゾーンID
     */
    private String zoneId;

    /**
     * ネットワークID
     */
    private String networkId;

    /**
     * デフォルトキーペア
     */
    private String defKeyPair;

    public PlatformCloudstackResponse() {}

    public PlatformCloudstackResponse(PlatformCloudstack cloudstack) {
        this.host = cloudstack.getHost();
        this.path = cloudstack.getPath();
        this.port = cloudstack.getPort();
        this.secure = BooleanUtils.isTrue(cloudstack.getSecure());
        this.zoneId = cloudstack.getZoneId();
        this.networkId = cloudstack.getNetworkId();
    }

   /**
    *
    * messageを取得します。
    *
    * @return host
    */
    @XmlElement(name="Hsost")
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
    * pathを取得します。
    *
    * @return path
    */
    @XmlElement(name="Path")
    public String getPath() {
        return path;
    }

   /**
    *
    * pathを設定します。
    *
    * @param path
    */
    public void setPath(String path) {
        this.path = path;
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
    * zoneIdを取得します。
    *
    * @return zoneId
    */
    @XmlElement(name="ZoneId")
    public String getZoneId() {
        return zoneId;
    }

   /**
    *
    * zoneIdを設定します。
    *
    * @param zoneId
    */
    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

   /**
    *
    * networkIdを取得します。
    *
    * @return networkId
    */
    @XmlElement(name="NetworkId")
    public String getNetworkId() {
        return networkId;
    }

   /**
    *
    * networkIdを設定します。
    *
    * @param networkId
    */
    public void setNetworkId(String networkId) {
        this.networkId = networkId;
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
}