package jp.primecloud.auto.api.response;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class ListEventLogResponse extends AbstractResponse {

    @JsonProperty("EventLogs")
    private List<EventLogResponse> eventLogs = new ArrayList<EventLogResponse>();

    public ListEventLogResponse() {
    }

    public List<EventLogResponse> getEventLogs() {
        return eventLogs;
    }

    public void setEventLogs(List<EventLogResponse> eventLogs) {
        this.eventLogs = eventLogs;
    }

}
