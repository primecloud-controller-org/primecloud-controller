package jp.primecloud.auto.api.response.lb;

import org.apache.commons.lang.BooleanUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.entity.crud.LoadBalancerInstance;


public class LoadBalancerInstanceResponse {

    /**
     * インスタンス番号
     */
    @JsonProperty("InstanceNo")
    private Long instanceNo;

    /**
     * 振り分け有無 true:振り分け有効、false:振り分け無効
     */
    @JsonProperty("Enabled")
    private Boolean enabled;

    /**
     * 振り分けステータス
     */
    @JsonProperty("Status")
    private String status;

    public LoadBalancerInstanceResponse() {}

    public LoadBalancerInstanceResponse(LoadBalancerInstance loadBalancerInstance) {
        this.instanceNo = loadBalancerInstance.getInstanceNo();
        this.enabled = BooleanUtils.isTrue(loadBalancerInstance.getEnabled());
        this.status = loadBalancerInstance.getStatus();
    }

   /**
    *
    * instanceNoを取得します。
    *
    * @return instanceNo
    */
    public Long getInstanceNo() {
        return instanceNo;
    }

   /**
    *
    * instanceNoを設定します。
    *
    * @param instanceNo
    */
    public void setInstanceNo(Long instanceNo) {
        this.instanceNo = instanceNo;
    }

   /**
    *
    * enabledを取得します。
    *
    * @return enabled
    */
    public Boolean getEnabled() {
        return enabled;
    }

   /**
    *
    * enabledを設定します。
    *
    * @param enabled
    */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

   /**
    *
    * statusを取得します。
    *
    * @return status
    */
    public String getStatus() {
        return status;
    }

   /**
    *
    * statusを設定します。
    *
    * @param status
    */
    public void setStatus(String status) {
        this.status = status;
    }
}