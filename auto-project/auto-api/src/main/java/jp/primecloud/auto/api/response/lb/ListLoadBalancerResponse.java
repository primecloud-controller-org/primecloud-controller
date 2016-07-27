package jp.primecloud.auto.api.response.lb;

import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.api.response.AbstractResponse;

import org.codehaus.jackson.annotate.JsonProperty;

public class ListLoadBalancerResponse extends AbstractResponse {

    @JsonProperty("LoadBalancers")
    private List<LoadBalancerResponse> loadBalancers = new ArrayList<LoadBalancerResponse>();

    public List<LoadBalancerResponse> getLoadBalancers() {
        return loadBalancers;
    }

    public void setLoadBalancers(List<LoadBalancerResponse> loadBalancers) {
        this.loadBalancers = loadBalancers;
    }

}
