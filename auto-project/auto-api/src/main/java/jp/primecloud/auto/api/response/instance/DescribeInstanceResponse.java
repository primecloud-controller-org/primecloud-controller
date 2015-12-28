package jp.primecloud.auto.api.response.instance;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.api.response.AbstractResponse;

public class DescribeInstanceResponse extends AbstractResponse {

    @JsonProperty("Instance")
    private InstanceResponse instance;

    public DescribeInstanceResponse() {}

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