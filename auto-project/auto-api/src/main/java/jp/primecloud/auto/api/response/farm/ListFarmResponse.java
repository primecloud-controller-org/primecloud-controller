package jp.primecloud.auto.api.response.farm;

import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.api.response.AbstractResponse;

import org.codehaus.jackson.annotate.JsonProperty;

public class ListFarmResponse extends AbstractResponse {

    @JsonProperty("Farms")
    private List<FarmResponse> farms = new ArrayList<FarmResponse>();

    public List<FarmResponse> getFarms() {
        return farms;
    }

    public void setFarms(List<FarmResponse> farms) {
        this.farms = farms;
    }

}
