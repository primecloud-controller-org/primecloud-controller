package jp.primecloud.auto.api.response.lb;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.api.response.AbstractResponse;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.LoadBalancerHealthCheck;


public class DescribeLoadBalancerHealthCheckResponse extends AbstractResponse {

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
     * 監視プロトコル
     */
    @JsonProperty("Protocol")
    private String protocol;

    /**
     * 監視ポート
     */
    @JsonProperty("Port")
    private Integer port;

    /**
     * 監視パス
     */
    @JsonProperty("Path")
    private String path;

    /**
     * タイムアウト時間
     */
    @JsonProperty("Timeout")
    private Integer timeout;

    /**
     * チェック間隔
     */
    @JsonProperty("Interval")
    private Integer interval;

    /**
     * 復帰しきい値
     */
    @JsonProperty("HealthyThreshold")
    private Integer healthyThreshold;

    /**
     * 障害しきい値
     */
    @JsonProperty("UnhealthyThreshold")
    private Integer unhealthyThreshold;

    public DescribeLoadBalancerHealthCheckResponse() {}

    public DescribeLoadBalancerHealthCheckResponse(LoadBalancer loadBalancer, LoadBalancerHealthCheck healthCheck) {
        this.loadBalancerNo = healthCheck.getLoadBalancerNo();
        this.loadBalancerName = loadBalancer.getLoadBalancerName();
        this.protocol = healthCheck.getCheckProtocol();
        this.port = healthCheck.getCheckPort();
        this.path = healthCheck.getCheckPath();
        this.timeout = healthCheck.getCheckTimeout();
        this.interval = healthCheck.getCheckInterval();
        this.healthyThreshold = healthCheck.getHealthyThreshold();
        this.unhealthyThreshold = healthCheck.getUnhealthyThreshold();
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
    * protocolを取得します。
    *
    * @return protocol
    */
    public String getProtocol() {
        return protocol;
    }

   /**
    *
    * protocolを設定します。
    *
    * @param protocol
    */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

   /**
    *
    * portを取得します。
    *
    * @return port
    */
    public Integer getPort() {
        return port;
    }

   /**
    *
    * portを設定します。
    *
    * @param port
    */
    public void setPort(Integer port) {
        this.port = port;
    }

   /**
    *
    * pathを取得します。
    *
    * @return path
    */
    public String getPath() {
        return path;
    }

   /**
    *
    * pathを設定します。
    *
    * @param path
    */
    public void setPath(String path) {
        this.path = path;
    }

   /**
    *
    * timeoutを取得します。
    *
    * @return timeout
    */
    public Integer getTimeout() {
        return timeout;
    }

   /**
    *
    * timeoutを設定します。
    *
    * @param timeout
    */
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

   /**
    *
    * intervalを取得します。
    *
    * @return interval
    */
    public Integer getInterval() {
        return interval;
    }

   /**
    *
    * intervalを設定します。
    *
    * @param interval
    */
    public void setInterval(Integer interval) {
        this.interval = interval;
    }

   /**
    *
    * healthyThresholdを取得します。
    *
    * @return healthyThreshold
    */
    public Integer getHealthyThreshold() {
        return healthyThreshold;
    }

   /**
    *
    * healthyThresholdを設定します。
    *
    * @param healthyThreshold
    */
    public void setHealthyThreshold(Integer healthyThreshold) {
        this.healthyThreshold = healthyThreshold;
    }

   /**
    *
    * unhealthyThresholdを取得します。
    *
    * @return unhealthyThreshold
    */
    public Integer getUnhealthyThreshold() {
        return unhealthyThreshold;
    }

   /**
    *
    * unhealthyThresholdを設定します。
    *
    * @param unhealthyThreshold
    */
    public void setUnhealthyThreshold(Integer unhealthyThreshold) {
        this.unhealthyThreshold = unhealthyThreshold;
    }
}