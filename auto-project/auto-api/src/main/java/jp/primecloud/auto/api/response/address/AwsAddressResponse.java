package jp.primecloud.auto.api.response.address;

import jp.primecloud.auto.entity.crud.AwsAddress;

import org.codehaus.jackson.annotate.JsonProperty;

public class AwsAddressResponse {

    @JsonProperty("AddressNo")
    private Long addressNo;

    @JsonProperty("PlatformNo")
    private Long platformNo;

    @JsonProperty("PublicIp")
    private String publicIp;

    @JsonProperty("Comment")
    private String comment;

    @JsonProperty("AssociateInstanceNo")
    private Long associateInstanceNo;

    public AwsAddressResponse(AwsAddress awsAddress) {
        this.addressNo = awsAddress.getAddressNo();
        this.platformNo = awsAddress.getPlatformNo();
        this.publicIp = awsAddress.getPublicIp();
        this.comment = awsAddress.getComment();
        this.associateInstanceNo = awsAddress.getInstanceNo();
    }

    public Long getAddressNo() {
        return addressNo;
    }

    public void setAddressNo(Long addressNo) {
        this.addressNo = addressNo;
    }

    public Long getPlatformNo() {
        return platformNo;
    }

    public void setPlatformNo(Long platformNo) {
        this.platformNo = platformNo;
    }

    public String getPublicIp() {
        return publicIp;
    }

    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getAssociateInstanceNo() {
        return associateInstanceNo;
    }

    public void setAssociateInstanceNo(Long associateInstanceNo) {
        this.associateInstanceNo = associateInstanceNo;
    }

}
