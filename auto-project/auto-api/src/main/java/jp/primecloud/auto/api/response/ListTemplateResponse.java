package jp.primecloud.auto.api.response;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name="ListTemplateResponse")
@XmlType(propOrder = {"success", "message", "templates"})
public class ListTemplateResponse {

    /**
     * 処理の成否 true:成功、false：エラー
     */
    private boolean success;

    /**
     * メッセージ
     */
    private String message;

    /**
     * テンプレート情報の一覧
     */
    private List<TemplateResponse> templates;

    public ListTemplateResponse() {}

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
    * templatesを取得します。
    *
    * @return templates
    */
    @XmlElementWrapper(name="Templates")
    @XmlElement(name="Template")
    public List<TemplateResponse> getTemplates() {
        return templates;
    }

   /**
    *
    * templatesを設定します。
    *
    * @param templates
    */
    public void setTemplates(List<TemplateResponse> templates) {
        this.templates = templates;
    }

    /**
     *
     * template を templatesに追加します。
     *
     * @param template
     */
    public void addTemplate(TemplateResponse template) {
        if (templates == null) {
            templates = new ArrayList<TemplateResponse>();
        }
        templates.add(template);
    }
}