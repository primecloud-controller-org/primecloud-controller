package jp.primecloud.auto.api.response.lb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name="ListLoadBalancerListener")
@XmlType(propOrder = {"success", "message", "loadBalancerListeners"})
public class ListLoadBalancerListenerResponse {

    /**
     * 処理の成否 true:正常終了、false:エラー
     */
    private boolean success;

    /**
     * メッセージ 正常終了の場合：Null、エラーの場合：エラーメッセージ
     */
    private String message;

    /**
     * ロードバランサ リスナー情報のリスト
     */
    private List<LoadBalancerListenerResponse> loadBalancerListeners;

    public ListLoadBalancerListenerResponse() {}

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
    * loadBalancerListenersを取得します。
    *
    * @return loadBalancerListeners
    */
    @XmlElementWrapper(name="LoadBalancerListeners")
    @XmlElement(name="LoadBalancerListener")
    public List<LoadBalancerListenerResponse> getLoadBalancerListeners() {
        return loadBalancerListeners;
    }

   /**
    *
    * loadBalancerListenersを設定します。
    *
    * @param loadBalancerListeners
    */
    public void setLoadBalancerListeners(List<LoadBalancerListenerResponse> loadBalancerListeners) {
        this.loadBalancerListeners = loadBalancerListeners;
    }

   /**
    *
    * loadBalancerListenerを追加します。
    *
    * @param loadBalancerListener
    */
    public void addLoadBalancerListener(LoadBalancerListenerResponse loadBalancerListener) {
        if (loadBalancerListeners == null) {
            loadBalancerListeners = new ArrayList<LoadBalancerListenerResponse>();
        }
        loadBalancerListeners.add(loadBalancerListener);
    }
}