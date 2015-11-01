package jp.primecloud.auto.api.response;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;


public class ListPlatformResponse extends AbstractResponse {

    /**
     * プラットフォーム情報の一覧
     */
    @JsonProperty("Platforms")
    private List<PlatformResponse> platforms;

    public ListPlatformResponse() {}

   /**
    *
    * platformsを取得します。
    *
    * @return platforms
    */
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