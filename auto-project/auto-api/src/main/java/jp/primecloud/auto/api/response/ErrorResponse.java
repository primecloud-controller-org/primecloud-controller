package jp.primecloud.auto.api.response;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name="ErrorResponse")
@XmlType(propOrder = {"success", "message"})
public class ErrorResponse {

    /**
     * 処理の成否 true:成功、false：エラー
     */
    private boolean success;

    /**
     * メッセージ
     */
    private String message;

    public ErrorResponse() {}

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
    * messageを設定
    *
    * @param message
    */
    public void setMessage(String message) {
        this.message = message;
    }
}