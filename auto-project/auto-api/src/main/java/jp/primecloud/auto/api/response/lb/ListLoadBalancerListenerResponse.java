package jp.primecloud.auto.api.response.lb;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.api.response.AbstractResponse;


public class ListLoadBalancerListenerResponse extends AbstractResponse {

    /**
     * ロードバランサ リスナー情報のリスト
     */
    @JsonProperty("LoadBalancerListeners")
    private List<LoadBalancerListenerResponse> loadBalancerListeners = new ArrayList<LoadBalancerListenerResponse>();

    public ListLoadBalancerListenerResponse() {}

   /**
    *
    * loadBalancerListenersを取得します。
    *
    * @return loadBalancerListeners
    */
    public List<LoadBalancerListenerResponse> getLoadBalancerListeners() {
        return loadBalancerListeners;
    }

   /**
    *
    * loadBalancerListenersを設定します。
    *
    * @param loadBalancerListeners
    */
    public void setLoadBalancerListeners(List<LoadBalancerListenerResponse> loadBalancerListeners) {
        this.loadBalancerListeners = loadBalancerListeners;
    }

}