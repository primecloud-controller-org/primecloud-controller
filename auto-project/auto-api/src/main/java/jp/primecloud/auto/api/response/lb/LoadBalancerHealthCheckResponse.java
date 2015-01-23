package jp.primecloud.auto.api.response.lb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import jp.primecloud.auto.entity.crud.LoadBalancerHealthCheck;


@XmlRootElement(name="LoadBalancerHealthCheckResponse")
@XmlType(propOrder = {"checkProtocol", "checkPort", "checkPath", "checkTimeout", "checkInterval", "healthyThreshold", "unhealthyThreshold"})
public class LoadBalancerHealthCheckResponse {

    /**
     * 監視プロトコル
     */
    private String checkProtocol;

    /**
     * 監視ポート
     */
    private Integer checkPort;

    /**
     * 監視パス
     */
    private String checkPath;

    /**
     * タイムアウト時間
     */
    private Integer checkTimeout;

    /**
     * チェック間隔
     */
    private Integer checkInterval;

    /**
     * 復帰しきい値
     */
    private Integer healthyThreshold;

    /**
     * 障害しきい値
     */
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
    @XmlElement(name="CheckProtocol")
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
    @XmlElement(name="CheckPort")
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
    @XmlElement(name="CheckPath")
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
    @XmlElement(name="CheckTimeout")
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
    @XmlElement(name="CheckInterval")
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
    @XmlElement(name="HealthyThreshold")
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
    @XmlElement(name="UnhealthyThreshold")
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