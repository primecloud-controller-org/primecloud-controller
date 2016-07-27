package jp.primecloud.auto.api.response.component;

import jp.primecloud.auto.api.response.AbstractResponse;

import org.codehaus.jackson.annotate.JsonProperty;

public class CreateComponentResponse extends AbstractResponse {

    @JsonProperty("ComponentNo")
    private Long componentNo;

    public CreateComponentResponse() {
    }

    public Long getComponentNo() {
        return componentNo;
    }

    public void setComponentNo(Long componentNo) {
        this.componentNo = componentNo;
    }

}
