package jp.primecloud.auto.api.response.image;

import java.util.ArrayList;
import java.util.List;

import jp.primecloud.auto.api.response.AbstractResponse;

import org.codehaus.jackson.annotate.JsonProperty;

public class ListImageResponse extends AbstractResponse {

    @JsonProperty("Images")
    private List<ImageResponse> images = new ArrayList<ImageResponse>();

    public ListImageResponse() {
    }

    public List<ImageResponse> getImages() {
        return images;
    }

    public void setImages(List<ImageResponse> images) {
        this.images = images;
    }

}
