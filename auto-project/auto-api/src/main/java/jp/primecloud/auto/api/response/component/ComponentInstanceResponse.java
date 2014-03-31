package jp.primecloud.auto.api.response.component;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import jp.primecloud.auto.entity.crud.ComponentInstance;

import org.apache.commons.lang.BooleanUtils;



@XmlRootElement(name="ComponentInstanceResponse")
@XmlType(propOrder = { "instanceNo", "associate", "status"})
public class ComponentInstanceResponse {

    /**
     * インスタンス番号
     */
    private Long instanceNo;

    /**
     * アソシエイト インスタンスと関連づけられているか否かのフラグ
     */
    private Boolean associate;

    /**
     * ステータス インスタンスに割り当てられているコンポーネントのステータス
     */
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
    @XmlElement(name="InstanceNo")
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
    @XmlElement(name="Associate")
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
    @XmlElement(name="Status")
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