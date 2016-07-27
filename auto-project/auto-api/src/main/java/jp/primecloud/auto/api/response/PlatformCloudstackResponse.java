package jp.primecloud.auto.api.response;

import jp.primecloud.auto.entity.crud.PlatformCloudstack;

import org.apache.commons.lang.BooleanUtils;
import org.codehaus.jackson.annotate.JsonProperty;

public class PlatformCloudstackResponse {

    @JsonProperty("Hsost")
    private String host;

    @JsonProperty("Path")
    private String path;

    @JsonProperty("Port")
    private Integer port;

    @JsonProperty("Secure")
    private Boolean secure;

    @JsonProperty("ZoneId")
    private String zoneId;

    @JsonProperty("NetworkId")
    private String networkId;

    @JsonProperty("DefKeyPair")
    private String defKeyPair;

    public PlatformCloudstackResponse() {
    }

    public PlatformCloudstackResponse(PlatformCloudstack cloudstack) {
        this.host = cloudstack.getHost();
        this.path = cloudstack.getPath();
        this.port = cloudstack.getPort();
        this.secure = BooleanUtils.isTrue(cloudstack.getSecure());
        this.zoneId = cloudstack.getZoneId();
        this.networkId = cloudstack.getNetworkId();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Boolean getSecure() {
        return secure;
    }

    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public String getNetworkId() {
        return networkId;
    }

    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    public String getDefKeyPair() {
        return defKeyPair;
    }

    public void setDefKeyPair(String defKeyPair) {
        this.defKeyPair = defKeyPair;
    }

}
