package jp.primecloud.auto.api.response.lb;

import jp.primecloud.auto.api.response.AbstractResponse;
import jp.primecloud.auto.entity.crud.LoadBalancer;
import jp.primecloud.auto.entity.crud.LoadBalancerHealthCheck;

import org.codehaus.jackson.annotate.JsonProperty;

public class DescribeLoadBalancerHealthCheckResponse extends AbstractResponse {

    @JsonProperty("LoadBalancerNo")
    private Long loadBalancerNo;

    @JsonProperty("LoadBalancerName")
    private String loadBalancerName;

    @JsonProperty("Protocol")
    private String protocol;

    @JsonProperty("Port")
    private Integer port;

    @JsonProperty("Path")
    private String path;

    @JsonProperty("Timeout")
    private Integer timeout;

    @JsonProperty("Interval")
    private Integer interval;

    @JsonProperty("HealthyThreshold")
    private Integer healthyThreshold;

    @JsonProperty("UnhealthyThreshold")
    private Integer unhealthyThreshold;

    public DescribeLoadBalancerHealthCheckResponse() {
    }

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

    public Long getLoadBalancerNo() {
        return loadBalancerNo;
    }

    public void setLoadBalancerNo(Long loadBalancerNo) {
        this.loadBalancerNo = loadBalancerNo;
    }

    public String getLoadBalancerName() {
        return loadBalancerName;
    }

    public void setLoadBalancerName(String loadBalancerName) {
        this.loadBalancerName = loadBalancerName;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Integer getHealthyThreshold() {
        return healthyThreshold;
    }

    public void setHealthyThreshold(Integer healthyThreshold) {
        this.healthyThreshold = healthyThreshold;
    }

    public Integer getUnhealthyThreshold() {
        return unhealthyThreshold;
    }

    public void setUnhealthyThreshold(Integer unhealthyThreshold) {
        this.unhealthyThreshold = unhealthyThreshold;
    }

}
