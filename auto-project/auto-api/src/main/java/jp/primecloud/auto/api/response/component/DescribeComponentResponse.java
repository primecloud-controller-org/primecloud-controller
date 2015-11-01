package jp.primecloud.auto.api.response.component;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.api.response.AbstractResponse;
import jp.primecloud.auto.entity.crud.Component;


public class DescribeComponentResponse extends AbstractResponse {

    /**
     * コンポーネント番号
     */
    @JsonProperty("ComponentNo")
    private Long componentNo;

    /**
     * コンポーネント名
     */
    @JsonProperty("ComponentName")
    private String componentName;

    /**
     * コンポーネントタイプ番号
     */
    @JsonProperty("ComponentTypeNo")
    private Long componentTypeNo;

    /**
     * コメント
     */
    @JsonProperty("Comment")
    private String comment;

    /**
     * コンポーネントインスタンス情報のリスト
     */
    @JsonProperty("Instances")
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
    * componentNoを取得します。
    *
    * @return componentNo
    */
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