package jp.primecloud.auto.api.response;

import jp.primecloud.auto.log.entity.crud.EventLog;

import org.codehaus.jackson.annotate.JsonProperty;

public class EventLogResponse {

    @JsonProperty("Date")
    private String date;

    @JsonProperty("LogLevel")
    private String logLevel;

    @JsonProperty("FarmName")
    private String farmName;

    @JsonProperty("ComponentName")
    private String componentName;

    @JsonProperty("InstanceName")
    private String instanceName;

    @JsonProperty("Message")
    private String message;

    public EventLogResponse() {
    }

    public EventLogResponse(EventLog eventLog) {
        farmName = eventLog.getFarmName();
        componentName = eventLog.getComponentName();
        instanceName = eventLog.getInstanceName();
        message = eventLog.getMessage();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
