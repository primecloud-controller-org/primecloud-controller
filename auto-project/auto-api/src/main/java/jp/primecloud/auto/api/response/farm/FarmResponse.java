package jp.primecloud.auto.api.response.farm;

import jp.primecloud.auto.entity.crud.Farm;

import org.codehaus.jackson.annotate.JsonProperty;

public class FarmResponse {

    @JsonProperty("FarmNo")
    private Long farmNo;

    @JsonProperty("FarmName")
    private String farmName;

    @JsonProperty("DomainName")
    private String domainName;

    @JsonProperty("Comment")
    private String comment;

    public FarmResponse() {
    }

    public FarmResponse(Farm farm) {
        this.farmNo = farm.getFarmNo();
        this.farmName = farm.getFarmName();
        this.domainName = farm.getDomainName();
        this.comment = farm.getComment();
    }

    public Long getFarmNo() {
        return farmNo;
    }

    public void setFarmNo(Long farmNo) {
        this.farmNo = farmNo;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
