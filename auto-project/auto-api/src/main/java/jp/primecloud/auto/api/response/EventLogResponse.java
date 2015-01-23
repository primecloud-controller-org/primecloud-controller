package jp.primecloud.auto.api.response;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import jp.primecloud.auto.log.entity.crud.EventLog;




@XmlRootElement(name="EventLogResponse")
@XmlType(propOrder = { "date", "logLevel", "farmName", "componentName", "instanceName", "message" })
public class EventLogResponse {

    /**
     * 日時
     */
    private String date;

    /**
     * ログレベル
     */
    private String logLevel;

    /**
     * myCloud名
     */
    private String farmName;

    /**
     * サービス名
     */
    private String componentName;

    /**
     * サーバ名
     */
    private String instanceName;

    /**
     * メッセージ
     */
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
    @XmlElement(name="Date")
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
    @XmlElement(name="LogLevel")
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
    @XmlElement(name="FarmName")
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
    @XmlElement(name="ComponentName")
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
    @XmlElement(name="InstanceName")
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
    @XmlElement(name="Message")
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