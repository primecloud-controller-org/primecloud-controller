package jp.primecloud.auto.api.response.component;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.api.response.AbstractResponse;


public class ListComponentResponse extends AbstractResponse {

    /**
     * コンポーネント情報のリスト
     */
    @JsonProperty("Components")
    private List<ComponentResponse> components;

    public ListComponentResponse() {}

   /**
    *
    * componentsを取得します。
    *
    * @return components
    */
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