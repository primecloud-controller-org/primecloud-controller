package jp.primecloud.auto.api.response.component;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.entity.crud.Component;


public class ComponentResponse {

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

    @JsonProperty("Comment")
    private String comment;

    /**
     * コンポーネントインスタンス情報のリスト
     */
    @JsonProperty("Instances")
    private List<ComponentInstanceResponse> instances = new ArrayList<ComponentInstanceResponse>();

    /**
     * ロードバランサ名(設定されているLBの名称(1件目))
     */
    @JsonProperty("LoadBalancers")
    private List<ComponentLoadBalancerResponse> loadBalancers = new ArrayList<ComponentLoadBalancerResponse>();

    /**
     * ステータス
     */
    @JsonProperty("Status")
    private String status;

    public ComponentResponse() {}

    public ComponentResponse(Component component) {
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