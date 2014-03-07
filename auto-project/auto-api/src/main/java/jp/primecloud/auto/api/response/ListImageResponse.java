package jp.primecloud.auto.api.response;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name="ListImageResponse")
@XmlType(propOrder = {"success", "message", "images"})
public class ListImageResponse {

    /**
     * 処理の成否 true:成功、false：エラー
     */
    private boolean success;

    /**
     * メッセージ
     */
    private String message;

    /**
     * イメージ情報の一覧
     */
    private List<ImageResponse> images;

    public ListImageResponse() {}

   /**
    *
    * successを取得します。
    *
    * @return success
    */
    @XmlElement(name="SUCCESS")
    public boolean isSuccess() {
        return success;
    }

   /**
    *
    * successを設定します。
    *
    * @param success
    */
    public void setSuccess(boolean success) {
        this.success = success;
    }

   /**
    *
    * messageを取得します。
    *
    * @return message
    */
    @XmlElement(name="Message")
    public String getMessage() {
        return message;
    }

   /**
    *
    * messageを設定します。
    *
    * @param message
    */
    public void setMessage(String message) {
        this.message = message;
    }

   /**
    *
    * imagesを取得します。
    *
    * @return images
    */
    @XmlElementWrapper(name="Images")
    @XmlElement(name="Image")
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