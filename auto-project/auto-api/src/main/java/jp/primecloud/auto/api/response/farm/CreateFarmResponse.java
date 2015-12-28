package jp.primecloud.auto.api.response.farm;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.api.response.AbstractResponse;


public class CreateFarmResponse extends AbstractResponse {

    /**
     * ファーム番号
     */
    @JsonProperty("FarmNo")
    private Long farmNo;

    public CreateFarmResponse() {}

   /**
    *
    * farmNoを取得します。
    *
    * @return farmNo
    */
    public Long getFarmNo() {
        return farmNo;
    }

   /**
    *
    * farmNoを設定します。
    *
    * @param farmNo
    */
    public void setFarmNo(Long farmNo) {
        this.farmNo = farmNo;
    }
}