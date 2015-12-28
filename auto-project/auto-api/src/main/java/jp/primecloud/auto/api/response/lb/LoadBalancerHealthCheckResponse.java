package jp.primecloud.auto.api.response.lb;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.entity.crud.LoadBalancerHealthCheck;


public class LoadBalancerHealthCheckResponse {

    /**
     * 監視プロトコル
     */
    @JsonProperty("CheckProtocol")
    private String checkProtocol;

    /**
     * 監視ポート
     */
    @JsonProperty("CheckPort")
    private Integer checkPort;

    /**
     * 監視パス
     */
    @JsonProperty("CheckPath")
    private String checkPath;

    /**
     * タイムアウト時間
     */
    @JsonProperty("CheckTimeout")
    private Integer checkTimeout;

    /**
     * チェック間隔
     */
    @JsonProperty("CheckInterval")
    private Integer checkInterval;

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

    public LoadBalancerHealthCheckResponse() {}

    public LoadBalancerHealthCheckResponse(LoadBalancerHealthCheck healthCheck) {
        this.checkProtocol = healthCheck.getCheckProtocol();
        this.checkPort = healthCheck.getCheckPort();
        this.checkPath = healthCheck.getCheckPath();
        this.checkTimeout = healthCheck.getCheckTimeout();
        this.checkInterval = healthCheck.getCheckInterval();
        this.healthyThreshold = healthCheck.getHealthyThreshold();
        this.unhealthyThreshold = healthCheck.getUnhealthyThreshold();
    }

   /**
    *
    * checkProtocolを取得します。
    *
    * @return checkProtocol
    */
    public String getCheckProtocol() {
        return checkProtocol;
    }

   /**
    *
    * checkProtocolを設定します。
    *
    * @param checkProtocol
    */
    public void setCheckProtocol(String checkProtocol) {
        this.checkProtocol = checkProtocol;
    }

   /**
    *
    * checkPortを取得します。
    *
    * @return checkPort
    */
    public Integer getCheckPort() {
        return checkPort;
    }

   /**
    *
    * checkPortを設定します。
    *
    * @param checkPort
    */
    public void setCheckPort(Integer checkPort) {
        this.checkPort = checkPort;
    }

   /**
    *
    * checkPathを取得します。
    *
    * @return checkPath
    */
    public String getCheckPath() {
        return checkPath;
    }

   /**
    *
    * checkPathを設定します。
    *
    * @param checkPath
    */
    public void setCheckPath(String checkPath) {
        this.checkPath = checkPath;
    }

   /**
    *
    * checkTimeoutを取得します。
    *
    * @return checkTimeout
    */
    public Integer getCheckTimeout() {
        return checkTimeout;
    }

   /**
    *
    * checkTimeoutを設定します。
    *
    * @param checkTimeout
    */
    public void setCheckTimeout(Integer checkTimeout) {
        this.checkTimeout = checkTimeout;
    }

   /**
    *
    * checkIntervalを取得します。
    *
    * @return checkInterval
    */
    public Integer getCheckInterval() {
        return checkInterval;
    }

   /**
    *
    * checkIntervalを設定します。
    *
    * @param checkInterval
    */
    public void setCheckInterval(Integer checkInterval) {
        this.checkInterval = checkInterval;
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