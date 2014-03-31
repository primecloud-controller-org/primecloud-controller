package jp.primecloud.auto.api.response;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name="ListEventLogResponse")
@XmlType(propOrder = { "success", "message", "eventLogs" })
public class ListEventLogResponse {

    /**
     * 処理の成否 true:成功、false：エラー
     */
    private boolean success;

    /**
     * メッセージ
     */
    private String message;

    /**
     * EventLog情報の一覧
     */
    private List<EventLogResponse> eventLogs;

    public ListEventLogResponse() {}

   /**
    *
    * successを取得します。
    *
    * @return success
    */
    @XmlElement(name="SUCCESS")
    public boolean isSuccess() {
        return success;
    }

   /**
    *
    * successを設定します。
    *
    * @param success
    */
    public void setSuccess(boolean success) {
        this.success = success;
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

   /**
    *
    * eventLogsを取得します。
    *
    * @return eventLogs
    */
    @XmlElementWrapper(name="EventLogs")
    @XmlElement(name="EventLog")
    public List<EventLogResponse> getEventLogs() {
        return eventLogs;
    }

   /**
    *
    * eventLogsを設定します。
    *
    * @param eventLogs
    */
    public void setEventLogs(List<EventLogResponse> eventLogs) {
        this.eventLogs = eventLogs;
    }

   /**
    *
    * eventLogを追加します。
    *
    * @param eventLog
    */
    public void addEventLogs(EventLogResponse eventLog) {
        if (eventLogs == null) {
            eventLogs = new ArrayList<EventLogResponse>();
        }
        eventLogs.add(eventLog);
    }
}