package jp.primecloud.auto.api.response;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name="ListPlatformResponse")
@XmlType(propOrder = {"success", "message", "platforms"})
public class ListPlatformResponse {

    /**
     * 処理の成否 true:成功、false：エラー
     */
    private boolean success;

    /**
     * メッセージ
     */
    private String message;

    /**
     * プラットフォーム情報の一覧
     */
    private List<PlatformResponse> platforms;

    public ListPlatformResponse() {}

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
    * platformsを取得します。
    *
    * @return platforms
    */
    @XmlElementWrapper(name="Platforms")
    @XmlElement(name="Platform")
    public List<PlatformResponse> getPlatforms() {
        return platforms;
    }

   /**
    *
    * platformsを設定します。
    *
    * @param platforms
    */
    public void setPlatforms(List<PlatformResponse> platforms) {
        this.platforms = platforms;
    }

    /**
    *
    * platformを追加します。
    *
    * @param platform
    */
    public void addPlatform(PlatformResponse platform) {
        if (platforms == null) {
            platforms = new ArrayList<PlatformResponse>();
        }
        platforms.add(platform);
    }
}