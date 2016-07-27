package jp.primecloud.auto.api.response.lb;

import jp.primecloud.auto.api.response.AbstractResponse;

import org.codehaus.jackson.annotate.JsonProperty;

public class DescribeLoadBalancerResponse extends AbstractResponse {

    @JsonProperty("LoadBalancer")
    private LoadBalancerResponse loadBalancer;

    public DescribeLoadBalancerResponse(LoadBalancerResponse loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    public LoadBalancerResponse getLoadBalancer() {
        return loadBalancer;
    }

    public void setLoadBalancer(LoadBalancerResponse loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

}
