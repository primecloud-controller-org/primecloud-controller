package jp.primecloud.auto.api.response.farm;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import jp.primecloud.auto.entity.crud.Farm;



@XmlRootElement(name="FarmResponse")
@XmlType(propOrder = {"farmNo", "farmName", "domainName", "comment"})
public class FarmResponse {

    /**
     * ファーム番号
     */
    private Long farmNo;

    /**
     * ファーム名
     */
    private String farmName;

    /**
     * ドメイン名
     */
    private String domainName;

    /**
     * コメント
     */
    private String comment;

    public FarmResponse() {}

    public FarmResponse(Farm farm) {
        this.farmNo = farm.getFarmNo();
        this.farmName = farm.getFarmName();
        this.domainName = farm.getDomainName();
        this.comment = farm.getComment();
    }

   /**
    *
    * farmNoを取得します。
    *
    * @return farmNo
    */
    @XmlElement(name="FarmNo")
    public Long getFarmNo() {
        return farmNo;
    }

   /**
    *
    * farmNoを設定します。
    *
    * @param farmNo
    */
    public void setFarmNo(Long farmNo) {
        this.farmNo = farmNo;
    }

   /**
    *
    * farmNameを取得します。
    *
    * @return farmName
    */
    @XmlElement(name="FarmName")
    public String getFarmName() {
        return farmName;
    }

   /**
    *
    * farmNameを設定します。
    *
    * @param farmName
    */
    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

   /**
    *
    * domainNameを取得します。
    *
    * @return domainName
    */
    @XmlElement(name="DomainName")
    public String getDomainName() {
        return domainName;
    }

   /**
    *
    * domainNameを設定します。
    *
    * @param domainName
    */
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

   /**
    *
    * commentを取得します。
    *
    * @return comment
    */
    @XmlElement(name="Comment")
    public String getComment() {
        return comment;
    }

   /**
    *
    * commentを設定します。
    *
    * @param comment
    */
    public void setComment(String comment) {
        this.comment = comment;
    }
}