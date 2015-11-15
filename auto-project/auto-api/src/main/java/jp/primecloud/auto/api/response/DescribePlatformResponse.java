package jp.primecloud.auto.api.response;

import org.codehaus.jackson.annotate.JsonProperty;

public class DescribePlatformResponse extends AbstractResponse {

    @JsonProperty("Platform")
    private PlatformResponse platform;

    public DescribePlatformResponse(PlatformResponse platform) {
        this.platform = platform;
    }

    public PlatformResponse getPlatform() {
        return platform;
    }

    public void setPlatform(PlatformResponse platform) {
        this.platform = platform;
    }

}