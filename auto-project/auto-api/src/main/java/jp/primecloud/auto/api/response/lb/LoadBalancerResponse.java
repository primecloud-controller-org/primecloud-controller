package jp.primecloud.auto.api.response.lb;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.entity.crud.LoadBalancer;


public class LoadBalancerResponse {

    /**
     * ロードバランサ番号
     */
    @JsonProperty("LoadBalancerNo")
    private Long loadBalancerNo;

    /**
     * ロードバランサ名
     */
    @JsonProperty("LoadBalancerName")
    private String loadBalancerName;

    /**
     * FQDN
     */
    @JsonProperty("FQDN")
    private String fqdn;

    /**
     * ロードバランサのステータス
     */
    @JsonProperty("Status")
    private String status;

    /**
     * コンポーネント番号
     */
    @JsonProperty("ComponentNo")
    private Long componentNo;

    /**
     * コンポーネント名
     */
    @JsonProperty("ComponentName")
    private String componentName;

    public LoadBalancerResponse() {}

    public LoadBalancerResponse(LoadBalancer loadBalancer) {
        this.loadBalancerNo = loadBalancer.getLoadBalancerNo();
        this.loadBalancerName = loadBalancer.getLoadBalancerName();
        this.fqdn = loadBalancer.getFqdn();
        this.status = loadBalancer.getStatus();
        this.componentNo = loadBalancer.getComponentNo();
    }

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

   /**
    *
    * loadBalancerNameを取得します。
    *
    * @return loadBalancerName
    */
    public String getLoadBalancerName() {
        return loadBalancerName;
    }

   /**
    *
    * loadBalancerNameを設定します。
    *
    * @param loadBalancerName
    */
    public void setLoadBalancerName(String loadBalancerName) {
        this.loadBalancerName = loadBalancerName;
    }

   /**
    *
    * fqdnを取得します。
    *
    * @return fqdn
    */
    public String getFqdn() {
        return fqdn;
    }

   /**
    *
    * fqdnを設定します。
    *
    * @param fqdn
    */
    public void setFqdn(String fqdn) {
        this.fqdn = fqdn;
    }

   /**
    *
    * statusを取得します。
    *
    * @return status
    */
    public String getStatus() {
        return status;
    }

   /**
    *
    * statusを設定します。
    *
    * @param status
    */
    public void setStatus(String status) {
        this.status = status;
    }

   /**
    *
    * componentNoを取得します。
    *
    * @return componentNo
    */
    public Long getComponentNo() {
        return componentNo;
    }

   /**
    *
    * componentNoを設定します。
    *
    * @param componentNo
    */
    public void setComponentNo(Long componentNo) {
        this.componentNo = componentNo;
    }

   /**
    *
    * componentNameを取得します。
    *
    * @return componentName
    */
    public String getComponentName() {
        return componentName;
    }

   /**
    *
    * componentNameを設定します。
    *
    * @param componentName
    */
    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }
}