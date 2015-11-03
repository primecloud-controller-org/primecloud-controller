package jp.primecloud.auto.api.response.lb;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.api.response.AbstractResponse;

public class DescribeLoadBalancerResponse extends AbstractResponse {

    @JsonProperty("LoadBalancer")
    private LoadBalancerResponse loadBalancer;

    public DescribeLoadBalancerResponse() {}

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