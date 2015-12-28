package jp.primecloud.auto.api.response.address;

import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.api.response.AbstractResponse;

import org.codehaus.jackson.annotate.JsonProperty;

public class ListAwsAddressResponse extends AbstractResponse {

    @JsonProperty("AwsAddresses")
    private List<AwsAddressResponse> awsAddresses = new ArrayList<AwsAddressResponse>();

    public List<AwsAddressResponse> getAwsAddresses() {
        return awsAddresses;
    }

    public void setAwsAddresses(List<AwsAddressResponse> awsAddresses) {
        this.awsAddresses = awsAddresses;
    }

}
