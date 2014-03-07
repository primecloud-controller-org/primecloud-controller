package jp.primecloud.auto.api.response.farm;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name="CreateFarmResponse")
@XmlType(propOrder = {"success", "message", "farmNo"})
public class CreateFarmResponse {

    /**
     * 処理の成否 true:正常終了、false:エラー
     */
    private boolean success;

    /**
     * メッセージ 正常終了の場合：Null、エラーの場合：エラーメッセージ
     */
    private String message;

    /**
     * ファーム番号
     */
    private Long farmNo;

    public CreateFarmResponse() {}

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
    * farmNoを取得します。
    *
    * @return farmNo
    */
    @XmlElement(name="FarmNo")
    public Long getFarmNo() {
        return farmNo;
    }

   /**
    *
    * farmNoを設定します。
    *
    * @param farmNo
    */
    public void setFarmNo(Long farmNo) {
        this.farmNo = farmNo;
    }
}