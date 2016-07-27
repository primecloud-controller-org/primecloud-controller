package jp.primecloud.auto.api.response.lb;

import jp.primecloud.auto.entity.crud.AutoScalingConf;

import org.apache.commons.lang.BooleanUtils;
import org.codehaus.jackson.annotate.JsonProperty;

public class AutoScalingConfResponse {

    @JsonProperty("Enabled")
    private Boolean enabled;

    @JsonProperty("PlatformNo")
    private Long platformNo;

    @JsonProperty("ImageNo")
    private Long imageNo;

    @JsonProperty("InstanceType")
    private String instanceType;

    @JsonProperty("NamingRule")
    private String namingRule;

    @JsonProperty("IdleTimeMax")
    private Long idleTimeMax;

    @JsonProperty("IdleTimeMin")
    private Long idleTimeMin;

    @JsonProperty("ContinueLimit")
    private Long continueLimit;

    @JsonProperty("AddCount")
    private Long addCount;

    @JsonProperty("DelCount")
    private Long delCount;

    public AutoScalingConfResponse() {
    }

    public AutoScalingConfResponse(AutoScalingConf autoScalingConf) {
        this.enabled = BooleanUtils.isTrue(autoScalingConf.getEnabled());
        this.platformNo = autoScalingConf.getPlatformNo();
        this.imageNo = autoScalingConf.getImageNo();
        this.instanceType = autoScalingConf.getInstanceType();
        this.namingRule = autoScalingConf.getNamingRule();
        this.idleTimeMax = autoScalingConf.getIdleTimeMax();
        this.idleTimeMin = autoScalingConf.getIdleTimeMin();
        this.continueLimit = autoScalingConf.getContinueLimit();
        this.addCount = autoScalingConf.getAddCount();
        this.delCount = autoScalingConf.getDelCount();
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getPlatformNo() {
        return platformNo;
    }

    public void setPlatformNo(Long platformNo) {
        this.platformNo = platformNo;
    }

    public Long getImageNo() {
        return imageNo;
    }

    public void setImageNo(Long imageNo) {
        this.imageNo = imageNo;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public String getNamingRule() {
        return namingRule;
    }

    public void setNamingRule(String namingRule) {
        this.namingRule = namingRule;
    }

    public Long getIdleTimeMax() {
        return idleTimeMax;
    }

    public void setIdleTimeMax(Long idleTimeMax) {
        this.idleTimeMax = idleTimeMax;
    }

    public Long getIdleTimeMin() {
        return idleTimeMin;
    }

    public void setIdleTimeMin(Long idleTimeMin) {
        this.idleTimeMin = idleTimeMin;
    }

    public Long getContinueLimit() {
        return continueLimit;
    }

    public void setContinueLimit(Long continueLimit) {
        this.continueLimit = continueLimit;
    }

    public Long getAddCount() {
        return addCount;
    }

    public void setAddCount(Long addCount) {
        this.addCount = addCount;
    }

    public Long getDelCount() {
        return delCount;
    }

    public void setDelCount(Long delCount) {
        this.delCount = delCount;
    }

}
