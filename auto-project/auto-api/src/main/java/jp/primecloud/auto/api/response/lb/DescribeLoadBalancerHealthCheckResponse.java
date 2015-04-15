package jp.primecloud.auto.api.response.lb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.LoadBalancerHealthCheck;


@XmlRootElement(name="DescribeLoadBalancerHealthCheckResponse")
@XmlType(propOrder = {"success", "message", "loadBalancerNo", "loadBalancerName", "protocol", "port", "path", "timeout", "interval", "healthyThreshold", "unhealthyThreshold"})
public class DescribeLoadBalancerHealthCheckResponse {

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
     * 監視プロトコル
     */
    private String protocol;

    /**
     * 監視ポート
     */
    private Integer port;

    /**
     * 監視パス
     */
    private String path;

    /**
     * タイムアウト時間
     */
    private Integer timeout;

    /**
     * チェック間隔
     */
    private Integer interval;

    /**
     * 復帰しきい値
     */
    private Integer healthyThreshold;

    /**
     * 障害しきい値
     */
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
    * protocolを取得します。
    *
    * @return protocol
    */
    @XmlElement(name="Protocol")
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
    @XmlElement(name="Port")
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
    @XmlElement(name="Path")
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
    @XmlElement(name="Timeout")
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
    @XmlElement(name="Interval")
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