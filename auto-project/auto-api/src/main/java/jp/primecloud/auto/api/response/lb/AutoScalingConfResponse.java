package jp.primecloud.auto.api.response.lb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import jp.primecloud.auto.entity.crud.AutoScalingConf;

import org.apache.commons.lang.BooleanUtils;



@XmlRootElement(name="LoadBalancerAutoScalingResponse")
@XmlType(propOrder = { "enabled", "platformNo", "imageNo", "instanceType", "namingRule", "idleTimeMax", "idleTimeMin", "continueLimit", "addCount", "delCount" })
public class AutoScalingConfResponse {

    /**
     * 有効/無効 true:有効、false:無効
     */
    private Boolean enabled;

    /**
     * プラットフォーム番号
     */
    private Long platformNo;

    /**
     * イメージ番号
     */
    private Long imageNo;

    /**
     * インスタンスタイプ
     */
    private String instanceType;

    /**
     * ネーミングルール
     */
    private String namingRule;

    /**
     * 増加指標CPU使用率(%)
     */
    private Long idleTimeMax;

    /**
     * 削減指標CPU使用率(%)
     */
    private Long idleTimeMin;

    /**
     * 監視継続時間(秒)
     */
    private Long continueLimit;

    /**
     * 増加サーバー数(台)
     */
    private Long addCount;

    /**
     * 削減サーバ数(台)
     */
    private Long delCount;

    public AutoScalingConfResponse() {}

    public AutoScalingConfResponse(AutoScalingConf autoScalingConf) {
        this.enabled = BooleanUtils.isTrue(autoScalingConf.getEnabled());
        this.platformNo = autoScalingConf.getPlatformNo();
        this.imageNo = autoScalingConf.getImageNo();
        this.instanceType = autoScalingConf.getInstanceType();
        this.namingRule = autoScalingConf.getNamingRule();
        this.idleTimeMax = autoScalingConf.getIdleTimeMax();
        this.idleTimeMin = autoScalingConf.getIdleTimeMin();
        this.continueLimit = autoScalingConf.getContinueLimit();
        this.addCount = autoScalingConf.getAddCount();
        this.delCount = autoScalingConf.getDelCount();
    }

   /**
    *
    * enabledを取得します。
    *
    * @return enabled
    */
    @XmlElement(name="Enabled")
    public Boolean getEnabled() {
        return enabled;
    }

   /**
    *
    * enabledを設定します。
    *
    * @param enabled
    */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

   /**
    *
    * platformNoを取得します。
    *
    * @return platformNo
    */
    @XmlElement(name="PlatformNo")
    public Long getPlatformNo() {
        return platformNo;
    }

   /**
    *
    * platformNoを設定します。
    *
    * @param platformNo
    */
    public void setPlatformNo(Long platformNo) {
        this.platformNo = platformNo;
    }

   /**
    *
    * imageNoを取得します。
    *
    * @return imageNo
    */
    @XmlElement(name="ImageNo")
    public Long getImageNo() {
        return imageNo;
    }

   /**
    *
    * imageNoを設定します。
    *
    * @param imageNo
    */
    public void setImageNo(Long imageNo) {
        this.imageNo = imageNo;
    }

   /**
    *
    * instanceTypeを取得します。
    *
    * @return instanceType
    */
    @XmlElement(name="InstanceType")
    public String getInstanceType() {
        return instanceType;
    }

   /**
    *
    * instanceTypeを設定します。
    *
    * @param instanceType
    */
    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

   /**
    *
    * namingRuleを取得します。
    *
    * @return namingRule
    */
    @XmlElement(name="NamingRule")
    public String getNamingRule() {
        return namingRule;
    }

   /**
    *
    * namingRuleを設定します。
    *
    * @param namingRule
    */
    public void setNamingRule(String namingRule) {
        this.namingRule = namingRule;
    }

   /**
    *
    * idleTimeMaxを取得します。
    *
    * @return idleTimeMax
    */
    @XmlElement(name="IdleTimeMax")
    public Long getIdleTimeMax() {
        return idleTimeMax;
    }

   /**
    *
    * idleTimeMaxを設定します。
    *
    * @param idleTimeMax
    */
    public void setIdleTimeMax(Long idleTimeMax) {
        this.idleTimeMax = idleTimeMax;
    }

   /**
    *
    * idleTimeMinを取得します。
    *
    * @return idleTimeMin
    */
    @XmlElement(name="IdleTimeMin")
    public Long getIdleTimeMin() {
        return idleTimeMin;
    }

   /**
    *
    * idleTimeMinを設定します。
    *
    * @param idleTimeMin
    */
    public void setIdleTimeMin(Long idleTimeMin) {
        this.idleTimeMin = idleTimeMin;
    }

   /**
    *
    * continueLimitを取得します。
    *
    * @return continueLimit
    */
    @XmlElement(name="ContinueLimit")
    public Long getContinueLimit() {
        return continueLimit;
    }

   /**
    *
    * continueLimitを設定します。
    *
    * @param continueLimit
    */
    public void setContinueLimit(Long continueLimit) {
        this.continueLimit = continueLimit;
    }

   /**
    *
    * addCountを取得します。
    *
    * @return addCount
    */
    @XmlElement(name="AddCount")
    public Long getAddCount() {
        return addCount;
    }

   /**
    *
    * addCountを設定します。
    *
    * @param addCount
    */
    public void setAddCount(Long addCount) {
        this.addCount = addCount;
    }

   /**
    *
    * delCountを取得します。
    *
    * @return delCount
    */
    @XmlElement(name="DelCount")
    public Long getDelCount() {
        return delCount;
    }

   /**
    *
    * delCountを設定します。
    *
    * @param delCount
    */
    public void setDelCount(Long delCount) {
        this.delCount = delCount;
    }
}