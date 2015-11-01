package jp.primecloud.auto.api.response.lb;

import org.apache.commons.lang.BooleanUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.entity.crud.AutoScalingConf;


public class AutoScalingConfResponse {

    /**
     * 有効/無効 true:有効、false:無効
     */
    @JsonProperty("Enabled")
    private Boolean enabled;

    /**
     * プラットフォーム番号
     */
    @JsonProperty("PlatformNo")
    private Long platformNo;

    /**
     * イメージ番号
     */
    @JsonProperty("ImageNo")
    private Long imageNo;

    /**
     * インスタンスタイプ
     */
    @JsonProperty("InstanceType")
    private String instanceType;

    /**
     * ネーミングルール
     */
    @JsonProperty("NamingRule")
    private String namingRule;

    /**
     * 増加指標CPU使用率(%)
     */
    @JsonProperty("IdleTimeMax")
    private Long idleTimeMax;

    /**
     * 削減指標CPU使用率(%)
     */
    @JsonProperty("IdleTimeMin")
    private Long idleTimeMin;

    /**
     * 監視継続時間(秒)
     */
    @JsonProperty("ContinueLimit")
    private Long continueLimit;

    /**
     * 増加サーバー数(台)
     */
    @JsonProperty("AddCount")
    private Long addCount;

    /**
     * 削減サーバ数(台)
     */
    @JsonProperty("DelCount")
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