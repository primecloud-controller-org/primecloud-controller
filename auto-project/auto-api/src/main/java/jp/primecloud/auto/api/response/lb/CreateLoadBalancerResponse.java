package jp.primecloud.auto.api.response.lb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name="CreateLoadBalancerResponse")
@XmlType(propOrder = {"success", "message", "loadBalancerNo"})
public class CreateLoadBalancerResponse {

    /**
     * 処理の成否 true:正常終了、false:エラー
     */
    private boolean success;

    /**
     * メッセージ 正常終了の場合：Null、エラーの場合：エラーメッセージ
     */
    private String message;

    /**
     * ロードバランサ番号
     */
    private Long loadBalancerNo;

    public CreateLoadBalancerResponse() {}

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
}