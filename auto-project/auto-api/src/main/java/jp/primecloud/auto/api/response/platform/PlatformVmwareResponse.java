package jp.primecloud.auto.api.response.platform;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class PlatformVmwareResponse {

    @JsonProperty("KeyNames")
    private List<String> keyNames = new ArrayList<String>();

    @JsonProperty("ComputeResources")
    private List<String> computeResources = new ArrayList<String>();

    public List<String> getKeyNames() {
        return keyNames;
    }

    public void setKeyNames(List<String> keyNames) {
        this.keyNames = keyNames;
    }

    public List<String> getComputeResources() {
        return computeResources;
    }

    public void setComputeResources(List<String> computeResources) {
        this.computeResources = computeResources;
    }

}
