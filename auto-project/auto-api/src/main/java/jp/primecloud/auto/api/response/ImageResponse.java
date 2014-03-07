package jp.primecloud.auto.api.response;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import jp.primecloud.auto.entity.crud.Image;
import jp.primecloud.auto.entity.crud.Platform;





@XmlRootElement(name="ImageResponse")
@XmlType(propOrder = {"imageNo", "imageName", "platformName", "os"})
public class ImageResponse {

    /**
     * イメージ番号
     */
    private Long imageNo;

    /**
     * イメージ名
     */
    private String imageName;

    /**
     * プラットフォーム名
     */
    private String platformName;

    /**
     * OS(名称)
     */
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
    @XmlElement(name="ImageNo")
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
    @XmlElement(name="ImageName")
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
    @XmlElement(name="PlatformName")
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
    @XmlElement(name="OS")
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