package jp.primecloud.auto.service.dto;

import java.io.Serializable;

public class SecurityGroupDto implements Serializable {

    /** TODO: フィールドコメントを記述 */
    private static final long serialVersionUID = -1543932263746238390L;

    private String id;

    private String groupName;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public SecurityGroupDto withGroupName(String groupName){
        this.groupName = groupName;
        return this;
    }

}
