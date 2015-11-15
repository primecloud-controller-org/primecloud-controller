package jp.primecloud.auto.api.response.address;

import jp.primecloud.auto.api.response.AbstractResponse;

import org.codehaus.jackson.annotate.JsonProperty;

public class AddAwsAddressResponse extends AbstractResponse {

    @JsonProperty("AwsAddress")
    private AwsAddressResponse awsAddress;

    public AddAwsAddressResponse(AwsAddressResponse awsAddress) {
        this.awsAddress = awsAddress;
    }

    public AwsAddressResponse getAwsAddress() {
        return awsAddress;
    }

    public void setAwsAddress(AwsAddressResponse awsAddress) {
        this.awsAddress = awsAddress;
    }

}
