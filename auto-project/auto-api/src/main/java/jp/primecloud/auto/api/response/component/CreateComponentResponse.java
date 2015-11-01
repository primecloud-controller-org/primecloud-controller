package jp.primecloud.auto.api.response.component;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.api.response.AbstractResponse;


public class CreateComponentResponse extends AbstractResponse {

    /**
     * コンポーネント番号
     */
    @JsonProperty("ComponentNo")
    private Long componentNo;

    public CreateComponentResponse() {}

    /**
     *
     * componentNoを取得します。
     *
     * @return componentNo
     */
     public Long getComponentNo() {
         return componentNo;
     }

    /**
     *
     * componentNoを設定します。
     *
     * @param componentNo
     */
     public void setComponentNo(Long componentNo) {
         this.componentNo = componentNo;
     }
}