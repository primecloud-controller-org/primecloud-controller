package jp.primecloud.auto.api.response.instance;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.api.response.AbstractResponse;


public class CreateInstanceResponse extends AbstractResponse {

    /**
     * インスタンス番号
     */
    @JsonProperty("InstanceNo")
    private Long instanceNo;

    public CreateInstanceResponse() {}


    /**
     *
     * instanceNoを取得します。
     *
     * @return instanceNo
     */
    public Long getInstanceNo() {
        return instanceNo;
    }

    /**
     *
     * instanceNoを設定します。
     *
     * @param instanceNo
     */
    public void setInstanceNo(Long instanceNo) {
        this.instanceNo = instanceNo;
    }
}