package jp.primecloud.auto.api.response;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;


public class ListTemplateResponse extends AbstractResponse {

    /**
     * テンプレート情報の一覧
     */
    @JsonProperty("Templates")
    private List<TemplateResponse> templates = new ArrayList<TemplateResponse>();

    public ListTemplateResponse() {}

   /**
    *
    * templatesを取得します。
    *
    * @return templates
    */
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

}