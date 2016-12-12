package jp.primecloud.auto.api.response.platform;

import org.codehaus.jackson.annotate.JsonProperty;

public class PlatformCloudstackResponse {

    @JsonProperty("DefKeyPair")
    private String defKeyPair;

    public String getDefKeyPair() {
        return defKeyPair;
    }

    public void setDefKeyPair(String defKeyPair) {
        this.defKeyPair = defKeyPair;
    }

}
