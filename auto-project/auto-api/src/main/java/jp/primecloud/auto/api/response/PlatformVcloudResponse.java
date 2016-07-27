package jp.primecloud.auto.api.response;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.entity.crud.PlatformVcloud;

public class PlatformVcloudResponse {

    @JsonProperty("Url")
    private String url;

    @JsonProperty("Username")
    private String username;

    @JsonProperty("Password")
    private String password;

    @JsonProperty("OrgName")
    private String orgName;

    @JsonProperty("VdcName")
    private String vdcName;

    public PlatformVcloudResponse() {
    }

    public PlatformVcloudResponse(PlatformVcloud vcloud) {
        this.url = vcloud.getUrl();
        this.orgName = vcloud.getOrgName();
        this.vdcName = vcloud.getVdcName();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getVdcName() {
        return vdcName;
    }

    public void setVdcName(String vdcName) {
        this.vdcName = vdcName;
    }

}
