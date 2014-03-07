package jp.primecloud.auto.api.response.lb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import jp.primecloud.auto.entity.crud.LoadBalancer;



@XmlRootElement(name="DescribeLoadBalancerResponse")
@XmlType(propOrder={ "success", "message", "loadBalancerNo", "loadBalancerName", "fqdn", "platformNo", "type", "status", "componentNo", "componentName", "listeners", "healthCheck", "instances", "autoScaling" })
public class DescribeLoadBalancerResponse {

    /**
     * 処理の成否 true:正常終了、false:エラー
     */
    private boolean success;

    /**
     * メッセージ 正常終了の場合：Null、エラーの場合：エラーメッセージ
     */
    private String message;

    /**
     * ロードバランサ番号
     */
    private Long loadBalancerNo;

    /**
     * ロードバランサ名
     */
    private String loadBalancerName;

    /**
     * FQDN
     */
    private String fqdn;

    /**
     * プラットフォーム番号
     */
    private Long platformNo;

    /**
     * ロードバランサタイプ
     */
    private String type;

    /**
     * ロードバランサのステータス
     */
    private String status;

    /**
     * コンポーネント番号
     */
    private Long componentNo;

    /**
     * コンポーネント名
     */
    private String componentName;

    /**
     * ロードバランサ リスナー情報のリスト
     */
    private List<LoadBalancerListenerResponse> listeners;

    /**
     * ロードバランサ ヘルスチェック情報
     */
    private LoadBalancerHealthCheckResponse healthCheck;

    /**
     * ロードバランサインスタンス情報のリスト
     */
    private List<LoadBalancerInstanceResponse> instances;

    /**
     * ロードバランサ オートスケーリング情報
     */
    private AutoScalingConfResponse autoScaling;

    public DescribeLoadBalancerResponse() {}

    public DescribeLoadBalancerResponse(LoadBalancer loadBalancer) {
        this.loadBalancerNo = loadBalancer.getLoadBalancerNo();
        this.loadBalancerName = loadBalancer.getLoadBalancerName();
        this.fqdn = loadBalancer.getFqdn();
        this.status = loadBalancer.getStatus();
        this.componentNo = loadBalancer.getComponentNo();
    }

   /**
    *
    * successを取得します。
    *
    * @return success
    */
    @XmlElement(name="SUCCESS")
    public boolean isSuccess() {
        return success;
    }

   /**
    *
    * successを設定します。
    *
    * @param success
    */
    public void setSuccess(boolean success) {
        this.success = success;
    }

   /**
    *
    * messageを取得します。
    *
    * @return success
    */
    @XmlElement(name="Message")
    public String getMessage() {
        return message;
    }

   /**
    *
    * messageを設定します。
    *
    * @param message
    */
    public void setMessage(String message) {
        this.message = message;
    }

   /**
    *
    * loadBalancerNoを取得します。
    *
    * @return loadBalancerNo
    */
    @XmlElement(name="LoadBalancerNo")
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
    @XmlElement(name="LoadBalancerName")
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
    * platformNoを取得します。
    *
    * @return platformNo
    */
    @XmlElement(name="PlatformNo")
    public Long getPlatformNo() {
        return platformNo;
    }

   /**
    *
    * platformNoを設定します。
    *
    * @param platformNo
    */
    public void setPlatformNo(Long platformNo) {
        this.platformNo = platformNo;
    }

   /**
    *
    * typeを取得します。
    *
    * @return type
    */
    @XmlElement(name="Type")
    public String getType() {
        return type;
    }

   /**
    *
    * typeを設定します。
    *
    * @param type
    */
    public void setType(String type) {
        this.type = type;
    }

   /**
    *
    * fqdnを取得します。
    *
    * @return fqdn
    */
    @XmlElement(name="FQDN")
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
    @XmlElement(name="Status")
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
    @XmlElement(name="ComponentNo")
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
    @XmlElement(name="ComponentName")
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
    @XmlElementWrapper(name="LISTENERS")
    @XmlElement(name="LISTENER")
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
    * listeners に listener を追加します。
    *
    * @param listener
    */
    public void addListener(LoadBalancerListenerResponse listener) {
        if (listeners == null) {
            listeners = new ArrayList<LoadBalancerListenerResponse>();
        }
        listeners.add(listener);
    }

   /**
    *
    * healthCheckを取得します。
    *
    * @return healthCheck
    */
    @XmlElement(name="HEALTHCHECK")
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
    @XmlElementWrapper(name="INSTANCES")
    @XmlElement(name="INSTANCE")
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
    * instances に instance を追加します。
    *
    * @param instance
    */
    public void addInstance(LoadBalancerInstanceResponse instance) {
        if (instances == null) {
            instances = new ArrayList<LoadBalancerInstanceResponse>();
        }
        instances.add(instance);
    }

   /**
    *
    * autoScalingを取得します。
    *
    * @return autoScaling
    */
    @XmlElement(name="AUTOSCALING")
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