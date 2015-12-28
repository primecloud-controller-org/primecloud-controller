package jp.primecloud.auto.api.response;

import org.codehaus.jackson.annotate.JsonProperty;

public class AbstractResponse {

    /**
     * 処理の成否 true:成功、false：エラー
     */
    @JsonProperty("SUCCESS")
    private boolean success = true;

    /**
     * メッセージ
     */
    @JsonProperty("Message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
