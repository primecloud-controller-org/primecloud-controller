package jp.primecloud.auto.api.response.component;

import org.apache.commons.lang.BooleanUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.entity.crud.ComponentInstance;


public class ComponentInstanceResponse {

    /**
     * インスタンス番号
     */
    @JsonProperty("InstanceNo")
    private Long instanceNo;

    /**
     * アソシエイト インスタンスと関連づけられているか否かのフラグ
     */
    @JsonProperty("Associate")
    private Boolean associate;

    /**
     * ステータス インスタンスに割り当てられているコンポーネントのステータス
     */
    @JsonProperty("Status")
    private String status;

    public ComponentInstanceResponse() {}

    public ComponentInstanceResponse(ComponentInstance componentInstance) {
        this.instanceNo = componentInstance.getInstanceNo();
        this.associate = BooleanUtils.isTrue(componentInstance.getAssociate());
        this.status = componentInstance.getStatus();
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
    * associateを取得します。
    *
    * @return associate
    */
    public Boolean getAssociate() {
        return associate;
    }

   /**
    *
    * associateを設定します。
    *
    * @param associate
    */
    public void setAssociate(Boolean associate) {
        this.associate = associate;
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