package jp.primecloud.auto.api.response;

import org.apache.commons.lang.BooleanUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.entity.crud.PlatformCloudstack;


public class PlatformCloudstackResponse {

    /**
     * ホストURL
     */
    @JsonProperty("Hsost")
    private String host;

    /**
     * パス(ホスト以下のパス)
     */
    @JsonProperty("Path")
    private String path;

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
     * ゾーンID
     */
    @JsonProperty("ZoneId")
    private String zoneId;

    /**
     * ネットワークID
     */
    @JsonProperty("NetworkId")
    private String networkId;

    /**
     * デフォルトキーペア
     */
    @JsonProperty("DefKeyPair")
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
    * zoneIdを取得します。
    *
    * @return zoneId
    */
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