package jp.primecloud.auto.api.response.component;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name = "CreateComponentResponse")
@XmlType(propOrder = {"success", "message", "componentNo"})
public class CreateComponentResponse {

    /**
     * 処理の成否 true:正常終了、false:エラー
     */
    private boolean success;

    /**
     * メッセージ 正常終了の場合：Null、エラーの場合：エラーメッセージ
     */
    private String message;

    /**
     * コンポーネント番号
     */
    private Long componentNo;

    public CreateComponentResponse() {}

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
}