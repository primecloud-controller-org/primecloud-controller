package jp.primecloud.auto.service.dto;

import java.io.Serializable;

public class SslKeyDto   implements Serializable {

    /** TODO: フィールドコメントを記述 */
    private static final long serialVersionUID = -2684249648440988501L;

    private String keyName;

    private Long keyNo;

    private String keyId;

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public Long getKeyNo() {
        return keyNo;
    }

    public void setKeyNo(Long keyNo) {
        this.keyNo = keyNo;
    }

}
