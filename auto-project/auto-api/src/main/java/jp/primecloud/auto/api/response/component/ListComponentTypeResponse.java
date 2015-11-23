package jp.primecloud.auto.api.response.component;

import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.api.response.AbstractResponse;

import org.codehaus.jackson.annotate.JsonProperty;

public class ListComponentTypeResponse extends AbstractResponse {

    @JsonProperty("ComponentTypes")
    private List<ComponentTypeResponse> componentTypes = new ArrayList<ComponentTypeResponse>();

    public List<ComponentTypeResponse> getComponentTypes() {
        return componentTypes;
    }

    public void setComponentTypes(List<ComponentTypeResponse> componentTypes) {
        this.componentTypes = componentTypes;
    }

}
