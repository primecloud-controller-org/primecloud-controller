package jp.primecloud.auto.api.response.instance;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name="CreateInstanceResponse")
@XmlType(propOrder = {"success", "message", "instanceNo"})
public class CreateInstanceResponse {

    /**
     * 処理の成否 true:正常終了、false:エラー
     */
    private boolean success;

    /**
     * メッセージ 正常終了の場合：Null、エラーの場合：エラーメッセージ
     */
    private String message;

    /**
     * インスタンス番号
     */
    private Long instanceNo;

    public CreateInstanceResponse() {}

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
    * @return message
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
     * instanceNoを取得します。
     *
     * @return instanceNo
     */
    @XmlElement(name="InstanceNo")
    public Long getInstanceNo() {
        return instanceNo;
    }

    /**
     *
     * instanceNoを設定します。
     *
     * @param instanceNo
     */
    public void setInstanceNo(Long instanceNo) {
        this.instanceNo = instanceNo;
    }
}