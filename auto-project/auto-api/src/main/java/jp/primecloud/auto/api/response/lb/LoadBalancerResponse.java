package jp.primecloud.auto.api.response.lb;

import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.entity.crud.LoadBalancer;

import org.codehaus.jackson.annotate.JsonProperty;

public class LoadBalancerResponse {

    @JsonProperty("LoadBalancerNo")
    private Long loadBalancerNo;

    @JsonProperty("LoadBalancerName")
    private String loadBalancerName;

    @JsonProperty("FarmNo")
    private Long farmNo;

    @JsonProperty("Comment")
    private String comment;

    @JsonProperty("FQDN")
    private String fqdn;

    @JsonProperty("PlatformNo")
    private Long platformNo;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("ComponentNo")
    private Long componentNo;

    @JsonProperty("ComponentName")
    private String componentName;

    @JsonProperty("Listeners")
    private List<LoadBalancerListenerResponse> listeners = new ArrayList<LoadBalancerListenerResponse>();

    @JsonProperty("HealthCheck")
    private LoadBalancerHealthCheckResponse healthCheck;

    @JsonProperty("Instances")
    private List<LoadBalancerInstanceResponse> instances = new ArrayList<LoadBalancerInstanceResponse>();

    @JsonProperty("AutoScaling")
    private AutoScalingConfResponse autoScaling;

    @JsonProperty("AWS")
    private AwsLoadBalancerResponse aws;

    public LoadBalancerResponse(LoadBalancer loadBalancer) {
        this.loadBalancerNo = loadBalancer.getLoadBalancerNo();
        this.loadBalancerName = loadBalancer.getLoadBalancerName();
        this.farmNo = loadBalancer.getFarmNo();
        this.comment = loadBalancer.getComment();
        this.fqdn = loadBalancer.getFqdn();
        this.platformNo = loadBalancer.getPlatformNo();
        this.type = loadBalancer.getType();
        this.status = loadBalancer.getStatus();
        this.componentNo = loadBalancer.getComponentNo();
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

    public Long getFarmNo() {
        return farmNo;
    }

    public void setFarmNo(Long farmNo) {
        this.farmNo = farmNo;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFqdn() {
        return fqdn;
    }

    public void setFqdn(String fqdn) {
        this.fqdn = fqdn;
    }

    public Long getPlatformNo() {
        return platformNo;
    }

    public void setPlatformNo(Long platformNo) {
        this.platformNo = platformNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getComponentNo() {
        return componentNo;
    }

    public void setComponentNo(Long componentNo) {
        this.componentNo = componentNo;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public List<LoadBalancerListenerResponse> getListeners() {
        return listeners;
    }

    public void setListeners(List<LoadBalancerListenerResponse> listeners) {
        this.listeners = listeners;
    }

    public LoadBalancerHealthCheckResponse getHealthCheck() {
        return healthCheck;
    }

    public void setHealthCheck(LoadBalancerHealthCheckResponse healthCheck) {
        this.healthCheck = healthCheck;
    }

    public List<LoadBalancerInstanceResponse> getInstances() {
        return instances;
    }

    public void setInstances(List<LoadBalancerInstanceResponse> instances) {
        this.instances = instances;
    }

    public AutoScalingConfResponse getAutoScaling() {
        return autoScaling;
    }

    public void setAutoScaling(AutoScalingConfResponse autoScaling) {
        this.autoScaling = autoScaling;
    }

    public AwsLoadBalancerResponse getAws() {
        return aws;
    }

    public void setAws(AwsLoadBalancerResponse aws) {
        this.aws = aws;
    }

}
