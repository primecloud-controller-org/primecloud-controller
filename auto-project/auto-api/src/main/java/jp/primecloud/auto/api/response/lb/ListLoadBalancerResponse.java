package jp.primecloud.auto.api.response.lb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name="ListLoadBalancerResponse")
@XmlType(propOrder = {"success", "message", "loadBalancers"})
public class ListLoadBalancerResponse {

    /**
     * 処理の成否 true:正常終了、false:エラー
     */
    private boolean success;

    /**
     * メッセージ 正常終了の場合：Null、エラーの場合：エラーメッセージ
     */
    private String message;

    /**
     * インスタンス情報のリスト
     */
    private List<LoadBalancerResponse> loadBalancers;

    public ListLoadBalancerResponse() {}

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
    * loadBalancersを取得します。
    *
    * @return loadBalancers
    */
    @XmlElementWrapper(name="LoadBalancers")
    @XmlElement(name="LoadBalancer")
    public List<LoadBalancerResponse> getLoadBalancers() {
        return loadBalancers;
    }

   /**
    *
    * loadBalancersを設定します。
    *
    * @param loadBalancers
    */
    public void setLoadBalancers(List<LoadBalancerResponse> loadBalancers) {
        this.loadBalancers = loadBalancers;
    }

   /**
    *
    * loadBalancerを追加します。
    *
    * @param loadBalancer
    */
    public void addLoadBalancer(LoadBalancerResponse loadBalancer) {
        if (loadBalancers == null) {
            loadBalancers = new ArrayList<LoadBalancerResponse>();
        }
        loadBalancers.add(loadBalancer);
    }
}