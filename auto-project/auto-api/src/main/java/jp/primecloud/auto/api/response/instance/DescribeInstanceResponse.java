package jp.primecloud.auto.api.response.instance;

import jp.primecloud.auto.api.response.AbstractResponse;

import org.codehaus.jackson.annotate.JsonProperty;

public class DescribeInstanceResponse extends AbstractResponse {

    @JsonProperty("Instance")
    private InstanceResponse instance;

    public DescribeInstanceResponse(InstanceResponse instance) {
        this.instance = instance;
    }

    public InstanceResponse getInstance() {
        return instance;
    }

    public void setInstance(InstanceResponse instance) {
        this.instance = instance;
    }

}
