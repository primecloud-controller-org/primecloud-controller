package jp.primecloud.auto.api.response.component;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import jp.primecloud.auto.entity.crud.Component;


@XmlRootElement(name="DescribeComponentResponse")
@XmlType(propOrder = {"success", "message", "componentNo", "componentName", "componentTypeNo", "comment", "instances"})
public class DescribeComponentResponse {

    /**
     * 処理の成否 true:正常終了、false:エラー
     */
    private boolean success;

    /**
     * メッセージ 正常終了の場合：Null、エラーの場合：エラーメッセージ
     */
    private String message;

    /**
     * コンポーネント番号
     */
    private Long componentNo;

    /**
     * コンポーネント名
     */
    private String componentName;

    /**
     * コンポーネントタイプ番号
     */
    private Long componentTypeNo;

    /**
     * コメント
     */
    private String comment;

    /**
     * コンポーネントインスタンス情報のリスト
     */
    private List<ComponentInstanceResponse> instances;


    public DescribeComponentResponse() {}

    public DescribeComponentResponse(Component component) {
        this.componentNo = component.getComponentNo();
        this.componentName = component.getComponentName();
        this.componentTypeNo = component.getComponentTypeNo();
        this.comment = component.getComment();
    }

   /**
    *
    * successを取得します。
    *
    * @return success
    */
    @XmlElement(name="SUCCESS")
    public boolean isSuccess() {
        return success;
    }

   /**
    *
    * successを設定します。
    *
    * @param success
    */
    public void setSuccess(boolean success) {
        this.success = success;
    }

   /**
    *
    * messageを取得します。
    *
    * @return success
    */
    @XmlElement(name="Message")
    public String getMessage() {
        return message;
    }

   /**
    *
    * messageを設定します。
    *
    * @param message
    */
    public void setMessage(String message) {
        this.message = message;
    }

   /**
    *
    * componentNoを取得します。
    *
    * @return componentNo
    */
    @XmlElement(name="ComponentNo")
    public Long getComponentNo() {
        return componentNo;
    }

   /**
    *
    * componentNoを設定します。
    *
    * @param componentNo
    */
    public void setComponentNo(Long componentNo) {
        this.componentNo = componentNo;
    }

   /**
    *
    * componentNameを取得します。
    *
    * @return componentName
    */
    @XmlElement(name="ComponentName")
    public String getComponentName() {
        return componentName;
    }

   /**
    *
    * componentNameを設定します。
    *
    * @param componentName
    */
    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    /**
    *
    * componentTypeNoを取得します。
    *
    * @return componentTypeNo
    */
    @XmlElement(name="ComponentTypeNo")
    public Long getComponentTypeNo() {
        return componentTypeNo;
    }

   /**
    *
    * componentTypeNoを設定します。
    *
    * @param componentTypeNo
    */
    public void setComponentTypeNo(Long componentTypeNo) {
        this.componentTypeNo = componentTypeNo;
    }

   /**
    *
    * commentを取得します。
    *
    * @return comment
    */
    @XmlElement(name="Comment")
    public String getComment() {
        return comment;
    }

   /**
    *
    * commentを設定します。
    *
    * @param comment
    */
    public void setComment(String comment) {
        this.comment = comment;
    }

   /**
    *
    * instancesを取得します。
    *
    * @return instances
    */
    @XmlElementWrapper(name="Instances")
    @XmlElement(name="Instance")
    public List<ComponentInstanceResponse> getInstances() {
        return instances;
    }

   /**
    *
    * instancesを設定します。
    *
    * @param instances
    */
    public void setInstances(List<ComponentInstanceResponse> instances) {
        this.instances = instances;
    }

    /**
    *
    * instances に instanceを追加します。
    *
    * @param instances
    */
    public void addInstance(ComponentInstanceResponse instance) {
        if (instances == null) {
            instances = new ArrayList<ComponentInstanceResponse>();
        }
        instances.add(instance);
    }
}