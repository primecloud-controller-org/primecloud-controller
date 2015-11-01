package jp.primecloud.auto.api.response.farm;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.entity.crud.Farm;


public class FarmResponse {

    /**
     * ファーム番号
     */
    @JsonProperty("FarmNo")
    private Long farmNo;

    /**
     * ファーム名
     */
    @JsonProperty("FarmName")
    private String farmName;

    /**
     * ドメイン名
     */
    @JsonProperty("DomainName")
    private String domainName;

    /**
     * コメント
     */
    @JsonProperty("Comment")
    private String comment;

    public FarmResponse() {}

    public FarmResponse(Farm farm) {
        this.farmNo = farm.getFarmNo();
        this.farmName = farm.getFarmName();
        this.domainName = farm.getDomainName();
        this.comment = farm.getComment();
    }

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

   /**
    *
    * farmNameを取得します。
    *
    * @return farmName
    */
    public String getFarmName() {
        return farmName;
    }

   /**
    *
    * farmNameを設定します。
    *
    * @param farmName
    */
    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

   /**
    *
    * domainNameを取得します。
    *
    * @return domainName
    */
    public String getDomainName() {
        return domainName;
    }

   /**
    *
    * domainNameを設定します。
    *
    * @param domainName
    */
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

   /**
    *
    * commentを取得します。
    *
    * @return comment
    */
    public String getComment() {
        return comment;
    }

   /**
    *
    * commentを設定します。
    *
    * @param comment
    */
    public void setComment(String comment) {
        this.comment = comment;
    }
}