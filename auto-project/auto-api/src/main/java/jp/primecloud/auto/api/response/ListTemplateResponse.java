package jp.primecloud.auto.api.response;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class ListTemplateResponse extends AbstractResponse {

    @JsonProperty("Templates")
    private List<TemplateResponse> templates = new ArrayList<TemplateResponse>();

    public ListTemplateResponse() {
    }

    public List<TemplateResponse> getTemplates() {
        return templates;
    }

    public void setTemplates(List<TemplateResponse> templates) {
        this.templates = templates;
    }

}
