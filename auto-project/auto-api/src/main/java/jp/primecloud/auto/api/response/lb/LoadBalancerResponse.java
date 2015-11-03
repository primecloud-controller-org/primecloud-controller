package jp.primecloud.auto.api.response.lb;

import java.util.ArrayList;
import java.util.List;

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
     * ロードバランサタイプ
     */
    @JsonProperty("Type")
    private String type;

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

    /**
     * ロードバランサ リスナー情報のリスト
     */
    @JsonProperty("Listeners")
    private List<LoadBalancerListenerResponse> listeners = new ArrayList<LoadBalancerListenerResponse>();

    /**
     * ロードバランサ ヘルスチェック情報
     */
    @JsonProperty("HealthCheck")
    private LoadBalancerHealthCheckResponse healthCheck;

    /**
     * ロードバランサインスタンス情報のリスト
     */
    @JsonProperty("Instances")
    private List<LoadBalancerInstanceResponse> instances = new ArrayList<LoadBalancerInstanceResponse>();

    /**
     * ロードバランサ オートスケーリング情報
     */
    @JsonProperty("AutoScaling")
    private AutoScalingConfResponse autoScaling;

    public LoadBalancerResponse() {}

    public LoadBalancerResponse(LoadBalancer loadBalancer) {
        this.loadBalancerNo = loadBalancer.getLoadBalancerNo();
        this.loadBalancerName = loadBalancer.getLoadBalancerName();
        this.fqdn = loadBalancer.getFqdn();
        this.type = loadBalancer.getType();
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    /**
     *
     * listenersを取得します。
     *
     * @return listeners
     */
     public List<LoadBalancerListenerResponse> getListeners() {
         return listeners;
     }

    /**
     *
     * listenersを設定します。
     *
     * @param listeners
     */
     public void setListeners(List<LoadBalancerListenerResponse> listeners) {
         this.listeners = listeners;
     }

    /**
     *
     * healthCheckを取得します。
     *
     * @return healthCheck
     */
     public LoadBalancerHealthCheckResponse getHealthCheck() {
         return healthCheck;
     }

    /**
     *
     * healthCheckを設定します。
     *
     * @param healthCheck
     */
     public void setHealthCheck(LoadBalancerHealthCheckResponse healthCheck) {
         this.healthCheck = healthCheck;
     }

    /**
     *
     * instancesを取得します。
     *
     * @return instances
     */
     public List<LoadBalancerInstanceResponse> getInstances() {
         return instances;
     }

    /**
     *
     * instancesを設定します。
     *
     * @param instances
     */
     public void setInstances(List<LoadBalancerInstanceResponse> instances) {
         this.instances = instances;
     }

    /**
     *
     * autoScalingを取得します。
     *
     * @return autoScaling
     */
     public AutoScalingConfResponse getAutoScaling() {
         return autoScaling;
     }

    /**
     *
     * autoScalingを設定します。
     *
     * @param autoScaling
     */
     public void setAutoScaling(AutoScalingConfResponse autoScaling) {
         this.autoScaling = autoScaling;
     }

}