package jp.primecloud.auto.api.response;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import jp.primecloud.auto.entity.crud.PlatformVcloud;


@XmlRootElement(name="PlatformVcloudResponse")
@XmlType(propOrder = { "url","username","password","orgName","vdcName" })
public class PlatformVcloudResponse {

    /** URL */
    private String url;

    /** ユーザ名 */
    private String username;

    /** パスワード */
    private String password;

    /** 組織名 */
    private String orgName;

    /** vDC名 */
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
    @XmlElement(name="Url")
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
    @XmlElement(name="Username")
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
    @XmlElement(name="Password")
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
    @XmlElement(name="OrgName")
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
    @XmlElement(name="VdcName")
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