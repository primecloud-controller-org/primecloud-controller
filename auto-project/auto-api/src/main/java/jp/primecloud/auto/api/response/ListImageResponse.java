package jp.primecloud.auto.api.response;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;


public class ListImageResponse extends AbstractResponse {

    /**
     * イメージ情報の一覧
     */
    @JsonProperty("Images")
    private List<ImageResponse> images;

    public ListImageResponse() {}


   /**
    *
    * imagesを取得します。
    *
    * @return images
    */
    public List<ImageResponse> getImages() {
        return images;
    }

   /**
    *
    * imagesを設定します。
    *
    * @param images
    */
    public void setImages(List<ImageResponse> images) {
        this.images = images;
    }

    /**
    *
    * imageを追加します。
    *
    * @param image
    */
    public void addImage(ImageResponse image) {
        if (images == null) {
            images = new ArrayList<ImageResponse>();
        }
        images.add(image);
    }
}