package jp.primecloud.auto.api.response.lb;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.api.response.AbstractResponse;


public class CreateLoadBalancerResponse extends AbstractResponse {

    /**
     * ロードバランサ番号
     */
    @JsonProperty("LoadBalancerNo")
    private Long loadBalancerNo;

    public CreateLoadBalancerResponse() {}


   /**
    *
    * loadBalancerNoを取得します。
    *
    * @return loadBalancerNo
    */
    public Long getLoadBalancerNo() {
        return loadBalancerNo;
    }

   /**
    *
    * loadBalancerNoを設定します。
    *
    * @param loadBalancerNo
    */
    public void setLoadBalancerNo(Long loadBalancerNo) {
        this.loadBalancerNo = loadBalancerNo;
    }
}