package jp.primecloud.auto.api.response.platform;

import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.api.response.AbstractResponse;

import org.codehaus.jackson.annotate.JsonProperty;

public class ListPlatformResponse extends AbstractResponse {

    @JsonProperty("Platforms")
    private List<PlatformResponse> platforms = new ArrayList<PlatformResponse>();

    public ListPlatformResponse() {
    }

    public List<PlatformResponse> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<PlatformResponse> platforms) {
        this.platforms = platforms;
    }

}
