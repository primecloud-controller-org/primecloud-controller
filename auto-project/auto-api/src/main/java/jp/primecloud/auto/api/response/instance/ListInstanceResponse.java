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
    private List<InstanceResponse> instances;

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

    /**
    *
    * instance を instances に追加します。
    *
    * @param instances
    */
    public void addInstance(InstanceResponse instance) {
        if (instances == null) {
            instances = new ArrayList<InstanceResponse>();
        }
        instances.add(instance);
    }
}