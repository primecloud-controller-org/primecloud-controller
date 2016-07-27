package jp.primecloud.auto.api.response.component;

import jp.primecloud.auto.entity.crud.ComponentInstance;

import org.apache.commons.lang.BooleanUtils;
import org.codehaus.jackson.annotate.JsonProperty;

public class ComponentInstanceResponse {

    @JsonProperty("InstanceNo")
    private Long instanceNo;

    @JsonProperty("Associate")
    private Boolean associate;

    @JsonProperty("Status")
    private String status;

    public ComponentInstanceResponse() {
    }

    public ComponentInstanceResponse(ComponentInstance componentInstance) {
        this.instanceNo = componentInstance.getInstanceNo();
        this.associate = BooleanUtils.isTrue(componentInstance.getAssociate());
        this.status = componentInstance.getStatus();
    }

    public Long getInstanceNo() {
        return instanceNo;
    }

    public void setInstanceNo(Long instanceNo) {
        this.instanceNo = instanceNo;
    }

    public Boolean getAssociate() {
        return associate;
    }

    public void setAssociate(Boolean associate) {
        this.associate = associate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
