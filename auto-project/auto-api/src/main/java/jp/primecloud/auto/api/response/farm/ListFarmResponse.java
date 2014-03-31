package jp.primecloud.auto.api.response.farm;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name="ListFarmResponse")
@XmlType(propOrder = {"success", "message", "farms"})
public class ListFarmResponse {

    /**
     * 処理の成否 true:正常終了、false:エラー
     */
    private boolean success;

    /**
     * メッセージ 正常終了の場合：Null、エラーの場合：エラーメッセージ
     */
    private String message;

    /**
     * ファームの一覧
     */
    private List<FarmResponse> farms;

    public ListFarmResponse() {}

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
    * farmsを取得します。
    *
    * @return farms
    */
    @XmlElementWrapper(name="Farms")
    @XmlElement(name="Farm")
    public List<FarmResponse> getFarms() {
        return farms;
    }

   /**
    *
    * farmsを設定します。
    *
    * @param farms
    */
    public void setFarms(List<FarmResponse> farms) {
        this.farms = farms;
    }

    /**
     *
     * farmをfarmsに追加します。
     *
     * @param farm
     */
    public void addFarm(FarmResponse farm) {
        if (farms == null) {
            farms = new ArrayList<FarmResponse>();
        }
        farms.add(farm);
    }
}