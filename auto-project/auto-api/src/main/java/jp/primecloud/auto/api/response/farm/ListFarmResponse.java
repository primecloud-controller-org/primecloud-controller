package jp.primecloud.auto.api.response.farm;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.api.response.AbstractResponse;


public class ListFarmResponse extends AbstractResponse {

    /**
     * ファームの一覧
     */
    @JsonProperty("Farms")
    private List<FarmResponse> farms;

    public ListFarmResponse() {}

   /**
    *
    * farmsを取得します。
    *
    * @return farms
    */
    public List<FarmResponse> getFarms() {
        return farms;
    }

   /**
    *
    * farmsを設定します。
    *
    * @param farms
    */
    public void setFarms(List<FarmResponse> farms) {
        this.farms = farms;
    }

    /**
     *
     * farmをfarmsに追加します。
     *
     * @param farm
     */
    public void addFarm(FarmResponse farm) {
        if (farms == null) {
            farms = new ArrayList<FarmResponse>();
        }
        farms.add(farm);
    }
}