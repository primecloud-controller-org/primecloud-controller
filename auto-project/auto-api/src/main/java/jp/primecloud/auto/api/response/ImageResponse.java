package jp.primecloud.auto.api.response;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.Platform;




public class ImageResponse {

    /**
     * イメージ番号
     */
    @JsonProperty("ImageNo")
    private Long imageNo;

    /**
     * イメージ名
     */
    @JsonProperty("ImageName")
    private String imageName;

    /**
     * プラットフォーム名
     */
    @JsonProperty("PlatformName")
    private String platformName;

    /**
     * OS(名称)
     */
    @JsonProperty("OS")
    private String os;

    public ImageResponse() {}

    public ImageResponse(Platform platform, Image image) {
        this.imageNo = image.getImageNo();
        this.imageName = image.getImageNameDisp();
        this.platformName = platform.getPlatformNameDisp();
        this.os = image.getOsDisp();
    }

   /**
    *
    * imageNoを取得します。
    *
    * @return imageNo
    */
    public Long getImageNo() {
        return imageNo;
    }

   /**
    *
    * imageNoを設定します。
    *
    * @param imageNo
    */
    public void setImageNo(Long imageNo) {
        this.imageNo = imageNo;
    }

   /**
    *
    * imageNameを取得します。
    *
    * @return imageName
    */
    public String getImageName() {
        return imageName;
    }

   /**
    *
    * imageNameを設定します。
    *
    * @param imageName
    */
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

   /**
    *
    * platformNameを取得します。
    *
    * @return platformName
    */
    public String getPlatformName() {
        return platformName;
    }

   /**
    *
    * platformNameを設定します。
    *
    * @param platformName
    */
    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

   /**
    *
    * osを取得します。
    *
    * @return os
    */
    public String getOs() {
        return os;
    }

   /**
    *
    * osを設定します。
    *
    * @param os
    */
    public void setOs(String os) {
        this.os = os;
    }
}