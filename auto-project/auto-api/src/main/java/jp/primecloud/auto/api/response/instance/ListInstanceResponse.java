package jp.primecloud.auto.api.response.instance;

import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.api.response.AbstractResponse;

import org.codehaus.jackson.annotate.JsonProperty;

public class ListInstanceResponse extends AbstractResponse {

    @JsonProperty("Instances")
    private List<InstanceResponse> instances = new ArrayList<InstanceResponse>();

    public ListInstanceResponse() {
    }

    public List<InstanceResponse> getInstances() {
        return instances;
    }

    public void setInstances(List<InstanceResponse> instances) {
        this.instances = instances;
    }

}
