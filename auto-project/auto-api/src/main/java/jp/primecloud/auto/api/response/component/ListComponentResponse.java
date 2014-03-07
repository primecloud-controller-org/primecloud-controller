package jp.primecloud.auto.api.response.component;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name="ListComponentResponse")
@XmlType(propOrder = {"success", "message", "components"})
public class ListComponentResponse {

    /**
     * 処理の成否 true:正常終了、false:エラー
     */
    private boolean success;

    /**
     * メッセージ 正常終了の場合：Null、エラーの場合：エラーメッセージ
     */
    private String message;

    /**
     * コンポーネント情報のリスト
     */
    private List<ComponentResponse> components;

    public ListComponentResponse() {}

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
    * componentsを取得します。
    *
    * @return components
    */
    @XmlElementWrapper(name="Components")
    @XmlElement(name="Component")
    public List<ComponentResponse> getComponents() {
        return components;
    }

   /**
    *
    * componentsを設定します。
    *
    * @param components
    */
    public void setComponents(List<ComponentResponse> components) {
        this.components = components;
    }

   /**
    *
    * components に component を追加します。
    *
    * @param component
    */
    public void addComponents(ComponentResponse component) {
        if (components == null) {
            components = new ArrayList<ComponentResponse>();
        }
        components.add(component);
    }
}