package jp.primecloud.auto.api.response.component;

import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.api.response.AbstractResponse;

import org.codehaus.jackson.annotate.JsonProperty;

public class ListComponentResponse extends AbstractResponse {

    @JsonProperty("Components")
    private List<ComponentResponse> components = new ArrayList<ComponentResponse>();

    public ListComponentResponse() {
    }

    public List<ComponentResponse> getComponents() {
        return components;
    }

    public void setComponents(List<ComponentResponse> components) {
        this.components = components;
    }

}
