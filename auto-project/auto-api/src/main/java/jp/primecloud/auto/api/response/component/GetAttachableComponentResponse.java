package jp.primecloud.auto.api.response.component;

import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.api.response.AbstractResponse;

import org.codehaus.jackson.annotate.JsonProperty;

public class GetAttachableComponentResponse extends AbstractResponse {

    @JsonProperty("Instances")
    private List<ComponentInstanceResponse> instances = new ArrayList<ComponentInstanceResponse>();

    public List<ComponentInstanceResponse> getInstances() {
        return instances;
    }

    public void setInstances(List<ComponentInstanceResponse> instances) {
        this.instances = instances;
    }

}
