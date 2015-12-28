package jp.primecloud.auto.api.response.instance;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.api.response.AbstractResponse;


public class ListInstanceResponse extends AbstractResponse {

    /**
     * インスタンス情報のリスト
     */
    @JsonProperty("Instances")
    private List<InstanceResponse> instances = new ArrayList<InstanceResponse>();

    public ListInstanceResponse() {}

   /**
    *
    * instancesを取得します。
    *
    * @return instances
    */
    public List<InstanceResponse> getInstances() {
        return instances;
    }

   /**
    *
    * instancesを設定します。
    *
    * @param instances
    */
    public void setInstances(List<InstanceResponse> instances) {
        this.instances = instances;
    }

}