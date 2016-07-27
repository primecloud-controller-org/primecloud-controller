package jp.primecloud.auto.api.response.lb;

import jp.primecloud.auto.entity.crud.LoadBalancerInstance;

import org.apache.commons.lang.BooleanUtils;
import org.codehaus.jackson.annotate.JsonProperty;

public class LoadBalancerInstanceResponse {

    @JsonProperty("InstanceNo")
    private Long instanceNo;

    @JsonProperty("Enabled")
    private Boolean enabled;

    @JsonProperty("Status")
    private String status;

    public LoadBalancerInstanceResponse(LoadBalancerInstance loadBalancerInstance) {
        this.instanceNo = loadBalancerInstance.getInstanceNo();
        this.enabled = BooleanUtils.isTrue(loadBalancerInstance.getEnabled());
        this.status = loadBalancerInstance.getStatus();
    }

    public Long getInstanceNo() {
        return instanceNo;
    }

    public void setInstanceNo(Long instanceNo) {
        this.instanceNo = instanceNo;
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
