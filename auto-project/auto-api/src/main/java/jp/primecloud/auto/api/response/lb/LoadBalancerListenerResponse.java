package jp.primecloud.auto.api.response.lb;

import org.apache.commons.lang.BooleanUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.entity.crud.LoadBalancerListener;


public class LoadBalancerListenerResponse {

    /**
     * ロードバランサ番号
     */
    @JsonProperty("LoadBalancerNo")
    private Long loadBalancerNo;

    /**
     * ロードバランサポート
     */
    @JsonProperty("LoadBalancerPort")
    private Integer loadBalancerPort;

    /**
     * サービスポート
     */
    @JsonProperty("ServicePort")
    private Integer servicePort;

    /**
     * プロトコル
     */
    @JsonProperty("Protocol")
    private String protocol;

    /**
     * 有効/無効
     */
    @JsonProperty("Enabled")
    private Boolean enabled;

    /**
     * ステータス
     */
    @JsonProperty("Status")
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