package jp.primecloud.auto.api.response;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.entity.crud.Template;




public class TemplateResponse {

    /**
     * テンプレート番号
     */
    @JsonProperty("TemplateNo")
    private Long templateNo;

    /**
     * テンプレート名
     */
    @JsonProperty("TemplateName")
    private String templateName;

    /**
     * 説明文(テンプレートの説明)
     */
    @JsonProperty("Description")
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