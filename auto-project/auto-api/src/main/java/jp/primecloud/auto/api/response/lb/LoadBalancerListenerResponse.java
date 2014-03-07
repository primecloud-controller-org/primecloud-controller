package jp.primecloud.auto.api.response.lb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import jp.primecloud.auto.entity.crud.LoadBalancerListener;

import org.apache.commons.lang.BooleanUtils;



@XmlRootElement(name="LoadBalancerListenerResponse")
@XmlType(propOrder = { "loadBalancerNo", "loadBalancerPort", "servicePort", "protocol", "enabled", "status" })
public class LoadBalancerListenerResponse {

    /**
     * ロードバランサ番号
     */
    private Long loadBalancerNo;

    /**
     * ロードバランサポート
     */
    private Integer loadBalancerPort;

    /**
     * サービスポート
     */
    private Integer servicePort;

    /**
     * プロトコル
     */
    private String protocol;

    /**
     * 有効/無効
     */
    private Boolean enabled;

    /**
     * ステータス
     */
    private String status;

    public LoadBalancerListenerResponse() {}

    public LoadBalancerListenerResponse(LoadBalancerListener listener) {
        this.loadBalancerNo = listener.getLoadBalancerNo();
        this.loadBalancerPort = listener.getLoadBalancerPort();
        this.servicePort = listener.getServicePort();
        this.protocol = listener.getProtocol();
        this.enabled = BooleanUtils.isTrue(listener.getEnabled());
        this.status = listener.getStatus();
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
    * loadBalancerPortを取得します。
    *
    * @return loadBalancerPort
    */
    @XmlElement(name="LoadBalancerPort")
    public Integer getLoadBalancerPort() {
        return loadBalancerPort;
    }

   /**
    *
    * loadBalancerPortを設定します。
    *
    * @param loadBalancerPort
    */
    public void setLoadBalancerPort(Integer loadBalancerPort) {
        this.loadBalancerPort = loadBalancerPort;
    }

   /**
    *
    * servicePortを取得します。
    *
    * @return servicePort
    */
    @XmlElement(name="ServicePort")
    public Integer getServicePort() {
        return servicePort;
    }

   /**
    *
    * servicePortを設定します。
    *
    * @param servicePort
    */
    public void setServicePort(Integer servicePort) {
        this.servicePort = servicePort;
    }

   /**
    *
    * protocolを取得します。
    *
    * @return protocol
    */
    @XmlElement(name="Protocol")
    public String getProtocol() {
        return protocol;
    }

   /**
    *
    * protocolを設定します。
    *
    * @param protocol
    */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

   /**
    *
    * enabledを取得します。
    *
    * @return enabled
    */
    @XmlElement(name="Enabled")
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