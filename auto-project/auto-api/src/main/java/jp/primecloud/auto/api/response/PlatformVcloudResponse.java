package jp.primecloud.auto.api.response;

import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.entity.crud.PlatformVcloud;


public class PlatformVcloudResponse {

    /** URL */
    @JsonProperty("Url")
    private String url;

    /** ユーザ名 */
    @JsonProperty("Username")
    private String username;

    /** パスワード */
    @JsonProperty("Password")
    private String password;

    /** 組織名 */
    @JsonProperty("OrgName")
    private String orgName;

    /** vDC名 */
    @JsonProperty("VdcName")
    private String vdcName;

	public PlatformVcloudResponse() {}

    public PlatformVcloudResponse(PlatformVcloud vcloud) {
        this.url = vcloud.getUrl();
        this.orgName = vcloud.getOrgName();
        this.vdcName = vcloud.getVdcName();
    }

   /**
    *
    * urlを取得します。
    *
    * @return url
    */
    public String getUrl() {
	    return url;
	}

   /**
    *
    * urlを設定
    *
    * @param url
    */
	public void setUrl(String url) {
		this.url = url;
	}

   /**
    *
    * usernameを取得します。
    *
    * @return username
    */
	public String getUsername() {
		return username;
	}

   /**
    *
    * usernameを設定
    *
    * @param username
    */
	public void setUsername(String username) {
		this.username = username;
	}

   /**
    *
    * passwordを取得します。
    *
    * @return password
    */
	public String getPassword() {
		return password;
	}

   /**
    *
    * passwordを設定
    *
    * @param password
    */
	public void setPassword(String password) {
		this.password = password;
	}

   /**
    *
    * orgNameを取得します。
    *
    * @return orgName
    */
	public String getOrgName() {
		return orgName;
	}

   /**
    *
    * orgNameを設定
    *
    * @param orgName
    */
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

   /**
    *
    * vdcNameを取得します。
    *
    * @return vdcName
    */
	public String getVdcName() {
		return vdcName;
	}

   /**
    *
    * vdcNameを設定
    *
    * @param vdcName
    */
	public void setVdcName(String vdcName) {
		this.vdcName = vdcName;
	}
}