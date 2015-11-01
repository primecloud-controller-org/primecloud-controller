package jp.primecloud.auto.api.response;


import org.codehaus.jackson.annotate.JsonProperty;

import jp.primecloud.auto.log.entity.crud.EventLog;




public class EventLogResponse {

    /**
     * 日時
     */
    @JsonProperty("Date")
    private String date;

    /**
     * ログレベル
     */
    @JsonProperty("LogLevel")
    private String logLevel;

    /**
     * myCloud名
     */
    @JsonProperty("FarmName")
    private String farmName;

    /**
     * サービス名
     */
    @JsonProperty("ComponentName")
    private String componentName;

    /**
     * サーバ名
     */
    @JsonProperty("InstanceName")
    private String instanceName;

    /**
     * メッセージ
     */
    @JsonProperty("Message")
    private String message;

    public EventLogResponse() {}

    public EventLogResponse(EventLog eventLog) {
        farmName = eventLog.getFarmName();
        componentName = eventLog.getComponentName();
        instanceName = eventLog.getInstanceName();
        message = eventLog.getMessage();
    }

   /**
    *
    * dateを取得します。
    *
    * @return date
    */
    public String getDate() {
        return date;
    }

    /**
     *
     * dateを設定します。
     *
     * @param date
     */
    public void setDate(String date) {
        this.date = date;
    }

   /**
    *
    * logLevelを取得します。
    *
    * @return logLevel
    */
    public String getLogLevel() {
        return logLevel;
    }

    /**
    *
    * logLevelを設定します。
    *
    * @param logLevel
    */
    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    /**
    *
    * farmNameを取得します。
    *
    * @return farmName
    */
    public String getFarmName() {
        return farmName;
    }

    /**
    *
    * vを設定します。
    *
    * @param farmName
    */
    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    /**
    *
    * componentNameを取得します。
    *
    * @return componentName
    */
    public String getComponentName() {
        return componentName;
    }

    /**
    *
    * componentNameを設定します。
    *
    * @param componentName
    */
    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    /**
    *
    * instanceNameを取得します。
    *
    * @return instanceName
    */
    public String getInstanceName() {
        return instanceName;
    }

   /**
    *
    * instanceNameを設定します。
    *
    * @param instanceName
    */
    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

   /**
    *
    * messageを取得します。
    *
    * @return message
    */
    public String getMessage() {
        return message;
    }

   /**
    *
    * messageを設定します。
    *
    * @param message
    */
    public void setMessage(String message) {
        this.message = message;
    }
}