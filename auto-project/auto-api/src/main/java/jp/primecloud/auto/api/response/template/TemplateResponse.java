package jp.primecloud.auto.api.response.template;

import jp.primecloud.auto.entity.crud.Template;

import org.codehaus.jackson.annotate.JsonProperty;

public class TemplateResponse {

    @JsonProperty("TemplateNo")
    private Long templateNo;

    @JsonProperty("TemplateName")
    private String templateName;

    @JsonProperty("Description")
    private String description;

    public TemplateResponse(Template template) {
        this.templateNo = template.getTemplateNo();
        this.templateName = template.getTemplateNameDisp();
        this.description = template.getTemplateDescriptionDisp();
    }

    public Long getTemplateNo() {
        return templateNo;
    }

    public void setTemplateNo(Long templateNo) {
        this.templateNo = templateNo;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
