package jp.primecloud.auto.api.response.lb;

import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.api.response.AbstractResponse;

import org.codehaus.jackson.annotate.JsonProperty;

public class ListLoadBalancerListenerResponse extends AbstractResponse {

    @JsonProperty("LoadBalancerListeners")
    private List<LoadBalancerListenerResponse> loadBalancerListeners = new ArrayList<LoadBalancerListenerResponse>();

    public ListLoadBalancerListenerResponse() {
    }

    public List<LoadBalancerListenerResponse> getLoadBalancerListeners() {
        return loadBalancerListeners;
    }

    public void setLoadBalancerListeners(List<LoadBalancerListenerResponse> loadBalancerListeners) {
        this.loadBalancerListeners = loadBalancerListeners;
    }

}