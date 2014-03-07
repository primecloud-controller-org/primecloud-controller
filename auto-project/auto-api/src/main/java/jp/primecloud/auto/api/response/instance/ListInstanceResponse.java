package jp.primecloud.auto.api.response.instance;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name="ListInstanceResponse")
@XmlType(propOrder = {"success", "message", "instances"})
public class ListInstanceResponse {

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
    private List<InstanceResponse> instances;

    public ListInstanceResponse() {}

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
    * instancesを取得します。
    *
    * @return instances
    */
    @XmlElementWrapper(name="Instances")
    @XmlElement(name="Instance")
    public List<InstanceResponse> getInstances() {
        return instances;
    }

   /**
    *
    * instancesを設定します。
    *
    * @param instances
    */
    public void setInstances(List<InstanceResponse> instances) {
        this.instances = instances;
    }

    /**
    *
    * instance を instances に追加します。
    *
    * @param instances
    */
    public void addInstance(InstanceResponse instance) {
        if (instances == null) {
            instances = new ArrayList<InstanceResponse>();
        }
        instances.add(instance);
    }
}