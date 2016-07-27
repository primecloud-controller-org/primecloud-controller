package jp.primecloud.auto.api.response.component;

import jp.primecloud.auto.api.response.AbstractResponse;

import org.codehaus.jackson.annotate.JsonProperty;

public class DescribeComponentResponse extends AbstractResponse {

    @JsonProperty("Component")
    private ComponentResponse component;

    public DescribeComponentResponse() {
    }

    public DescribeComponentResponse(ComponentResponse component) {
        this.component = component;
    }

    public ComponentResponse getComponent() {
        return component;
    }

    public void setComponent(ComponentResponse component) {
        this.component = component;
    }
}