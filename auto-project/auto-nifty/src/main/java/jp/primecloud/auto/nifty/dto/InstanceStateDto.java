package jp.primecloud.auto.nifty.dto;

import java.io.Serializable;

import com.nifty.cloud.sdk.server.model.InstanceState;


/**
 * <p>
 * nifty sdkのInstanceStateクラスからのデータ移送用クラス
 * </p>
 *
 */
public class InstanceStateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer code;

    private String name;

    /**
     * codeを取得します。
     * @return code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * codeを設定します。
     * @param code code
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * nameを取得します。
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * nameを設定します。
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    public InstanceStateDto(InstanceState instanceState) {
        code = instanceState.getCode();
        name = instanceState.getName();
    }

}
