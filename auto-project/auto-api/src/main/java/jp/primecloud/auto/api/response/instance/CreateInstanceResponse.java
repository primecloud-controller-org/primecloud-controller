package jp.primecloud.auto.api.response.instance;

import jp.primecloud.auto.api.response.AbstractResponse;

import org.codehaus.jackson.annotate.JsonProperty;

public class CreateInstanceResponse extends AbstractResponse {

    @JsonProperty("InstanceNo")
    private Long instanceNo;

    public CreateInstanceResponse() {
    }

    public Long getInstanceNo() {
        return instanceNo;
    }

    public void setInstanceNo(Long instanceNo) {
        this.instanceNo = instanceNo;
    }

}
