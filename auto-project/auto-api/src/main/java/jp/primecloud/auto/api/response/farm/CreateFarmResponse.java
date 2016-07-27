package jp.primecloud.auto.api.response.farm;

import jp.primecloud.auto.api.response.AbstractResponse;

import org.codehaus.jackson.annotate.JsonProperty;

public class CreateFarmResponse extends AbstractResponse {

    @JsonProperty("FarmNo")
    private Long farmNo;

    public CreateFarmResponse() {
    }

    public Long getFarmNo() {
        return farmNo;
    }

    public void setFarmNo(Long farmNo) {
        this.farmNo = farmNo;
    }

}
