package jp.primecloud.auto.api.response.component;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import jp.primecloud.auto.entity.crud.Component;



@XmlRootElement(name="Component")
@XmlType(propOrder = { "componentNo", "componentName", "componentTypeNo", "instanceCount", "loadBalancerName", "status"})
public class ComponentResponse {

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
     * インスタンス数(紐付いているインスタンスの数)
     */
    private Integer instanceCount;

    /**
     * ロードバランサ名(設定されているLBの名称(1件目))
     */
    private String loadBalancerName;

    /**
     * ステータス
     */
    private String status;

    public ComponentResponse() {}

    public ComponentResponse(Component component) {
        this.componentNo = component.getComponentNo();
        this.componentName = component.getComponentName();
        this.componentTypeNo = component.getComponentTypeNo();
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
    * instanceCountを取得します。
    *
    * @return instanceCount
    */
    @XmlElement(name="InstanceCount")
    public Integer getInstanceCount() {
        return instanceCount;
    }

   /**
    *
    * instanceCountを設定します。
    *
    * @param instanceCount
    */
    public void setInstanceCount(Integer instanceCount) {
        this.instanceCount = instanceCount;
    }

   /**
    *
    * loadBalancerNameを取得します。
    *
    * @return loadBalancerName
    */
    @XmlElement(name="LoadBalancerName")
    public String getLoadBalancerName() {
        return loadBalancerName;
    }

   /**
    *
    * loadBalancerNameを設定します。
    *
    * @param loadBalancerName
    */
    public void setLoadBalancerName(String loadBalancerName) {
        this.loadBalancerName = loadBalancerName;
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