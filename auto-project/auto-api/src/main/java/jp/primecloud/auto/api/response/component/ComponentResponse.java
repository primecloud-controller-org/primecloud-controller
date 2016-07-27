package jp.primecloud.auto.api.response.component;

import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.entity.crud.Component;

import org.codehaus.jackson.annotate.JsonProperty;

public class ComponentResponse {

    @JsonProperty("ComponentNo")
    private Long componentNo;

    @JsonProperty("ComponentName")
    private String componentName;

    @JsonProperty("ComponentTypeNo")
    private Long componentTypeNo;

    @JsonProperty("Comment")
    private String comment;

    @JsonProperty("Instances")
    private List<ComponentInstanceResponse> instances = new ArrayList<ComponentInstanceResponse>();

    @JsonProperty("LoadBalancers")
    private List<ComponentLoadBalancerResponse> loadBalancers = new ArrayList<ComponentLoadBalancerResponse>();

    @JsonProperty("Status")
    private String status;

    public ComponentResponse(Component component) {
        this.componentNo = component.getComponentNo();
        this.componentName = component.getComponentName();
        this.componentTypeNo = component.getComponentTypeNo();
        this.comment = component.getComment();
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

    public Long getComponentTypeNo() {
        return componentTypeNo;
    }

    public void setComponentTypeNo(Long componentTypeNo) {
        this.componentTypeNo = componentTypeNo;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<ComponentInstanceResponse> getInstances() {
        return instances;
    }

    public void setInstances(List<ComponentInstanceResponse> instances) {
        this.instances = instances;
    }

    public List<ComponentLoadBalancerResponse> getLoadBalancers() {
        return loadBalancers;
    }

    public void setLoadBalancers(List<ComponentLoadBalancerResponse> loadBalancers) {
        this.loadBalancers = loadBalancers;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
