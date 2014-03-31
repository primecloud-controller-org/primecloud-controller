package jp.primecloud.auto.api.response.lb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import jp.primecloud.auto.entity.crud.LoadBalancer;



@XmlRootElement(name="LoadBalancerResponse")
@XmlType(propOrder = {"loadBalancerNo", "loadBalancerName", "fqdn", "status", "componentNo", "componentName"})
public class LoadBalancerResponse {

    /**
     * ロードバランサ番号
     */
    private Long loadBalancerNo;

    /**
     * ロードバランサ名
     */
    private String loadBalancerName;

    /**
     * FQDN
     */
    private String fqdn;

    /**
     * ロードバランサのステータス
     */
    private String status;

    /**
     * コンポーネント番号
     */
    private Long componentNo;

    /**
     * コンポーネント名
     */
    private String componentName;

    public LoadBalancerResponse() {}

    public LoadBalancerResponse(LoadBalancer loadBalancer) {
        this.loadBalancerNo = loadBalancer.getLoadBalancerNo();
        this.loadBalancerName = loadBalancer.getLoadBalancerName();
        this.fqdn = loadBalancer.getFqdn();
        this.status = loadBalancer.getStatus();
        this.componentNo = loadBalancer.getComponentNo();
    }

   /**
    *
    * loadBalancerNoを取得します。
    *
    * @return loadBalancerNo
    */
    @XmlElement(name="LoadBalancerNo")
    public Long getLoadBalancerNo() {
        return loadBalancerNo;
    }

   /**
    *
    * loadBalancerNoを設定します。
    *
    * @param loadBalancerNo
    */
    public void setLoadBalancerNo(Long loadBalancerNo) {
        this.loadBalancerNo = loadBalancerNo;
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
    * fqdnを取得します。
    *
    * @return fqdn
    */
    @XmlElement(name="FQDN")
    public String getFqdn() {
        return fqdn;
    }

   /**
    *
    * fqdnを設定します。
    *
    * @param fqdn
    */
    public void setFqdn(String fqdn) {
        this.fqdn = fqdn;
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
}