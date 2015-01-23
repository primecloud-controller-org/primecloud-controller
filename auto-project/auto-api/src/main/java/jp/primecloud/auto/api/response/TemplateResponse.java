package jp.primecloud.auto.api.response;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import jp.primecloud.auto.entity.crud.Template;




@XmlRootElement(name="TemplateResponse")
@XmlType(propOrder = { "templateNo", "templateName", "description" })
public class TemplateResponse {

    /**
     * テンプレート番号
     */
    private Long templateNo;

    /**
     * テンプレート名
     */
    private String templateName;

    /**
     * 説明文(テンプレートの説明)
     */
    private String description;

    public TemplateResponse() {}

    public TemplateResponse(Template template) {
        this.templateNo = template.getTemplateNo();
        this.templateName = template.getTemplateNameDisp();
        this.description = template.getTemplateDescriptionDisp();
    }

   /**
    *
    * templateNoを取得します。
    *
    * @return templateNo
    */
    @XmlElement(name="TemplateNo")
    public Long getTemplateNo() {
        return templateNo;
    }

   /**
    *
    * templateNoを設定します。
    *
    * @param templateNo
    */
    public void setTemplateNo(Long templateNo) {
        this.templateNo = templateNo;
    }

   /**
    *
    * templateNameを取得します。
    *
    * @return templateName
    */
    @XmlElement(name="TemplateName")
    public String getTemplateName() {
        return templateName;
    }

   /**
    *
    * templateNameを設定します。
    *
    * @param templateName
    */
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

   /**
    *
    * descriptionを取得します。
    *
    * @return description
    */
    @XmlElement(name="Description")
    public String getDescription() {
        return description;
    }

   /**
    *
    * descriptionを設定します。
    *
    * @param description
    */
    public void setDescription(String description) {
        this.description = description;
    }
}