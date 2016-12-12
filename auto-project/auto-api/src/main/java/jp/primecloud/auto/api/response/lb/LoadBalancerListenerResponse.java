package jp.primecloud.auto.api.response.lb;

import jp.primecloud.auto.entity.crud.LoadBalancerListener;

import org.apache.commons.lang.BooleanUtils;
import org.codehaus.jackson.annotate.JsonProperty;

public class LoadBalancerListenerResponse {

    @JsonProperty("LoadBalancerNo")
    private Long loadBalancerNo;

    @JsonProperty("LoadBalancerPort")
    private Integer loadBalancerPort;

    @JsonProperty("ServicePort")
    private Integer servicePort;

    @JsonProperty("Protocol")
    private String protocol;

    @JsonProperty("Enabled")
    private Boolean enabled;

    @JsonProperty("Status")
    private String status;

    public LoadBalancerListenerResponse(LoadBalancerListener listener) {
        this.loadBalancerNo = listener.getLoadBalancerNo();
        this.loadBalancerPort = listener.getLoadBalancerPort();
        this.servicePort = listener.getServicePort();
        this.protocol = listener.getProtocol();
        this.enabled = BooleanUtils.isTrue(listener.getEnabled());
        this.status = listener.getStatus();
    }

    public Long getLoadBalancerNo() {
        return loadBalancerNo;
    }

    public void setLoadBalancerNo(Long loadBalancerNo) {
        this.loadBalancerNo = loadBalancerNo;
    }

    public Integer getLoadBalancerPort() {
        return loadBalancerPort;
    }

    public void setLoadBalancerPort(Integer loadBalancerPort) {
        this.loadBalancerPort = loadBalancerPort;
    }

    public Integer getServicePort() {
        return servicePort;
    }

    public void setServicePort(Integer servicePort) {
        this.servicePort = servicePort;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
