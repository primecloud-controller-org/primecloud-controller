package jp.primecloud.auto.api.response;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;


public class ListEventLogResponse extends AbstractResponse {

    /**
     * EventLog情報の一覧
     */
    @JsonProperty("EventLogs")
    private List<EventLogResponse> eventLogs = new ArrayList<EventLogResponse>();

    public ListEventLogResponse() {}

   /**
    *
    * eventLogsを取得します。
    *
    * @return eventLogs
    */
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

}