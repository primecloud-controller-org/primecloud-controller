package jp.primecloud.auto.api.response.lb;

import jp.primecloud.auto.api.response.AbstractResponse;

import org.codehaus.jackson.annotate.JsonProperty;

public class CreateLoadBalancerResponse extends AbstractResponse {

    @JsonProperty("LoadBalancerNo")
    private Long loadBalancerNo;

    public CreateLoadBalancerResponse() {
    }

    public Long getLoadBalancerNo() {
        return loadBalancerNo;
    }

    public void setLoadBalancerNo(Long loadBalancerNo) {
        this.loadBalancerNo = loadBalancerNo;
    }

}
