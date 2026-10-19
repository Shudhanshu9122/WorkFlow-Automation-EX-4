package edu.srmist.bookissue.dto;

import java.time.Instant;
import java.util.Map;

public class ProcessResponse {

    private Long processInstanceKey;
    private String bpmnProcessId;
    private String status;
    private String currentActivity;
    private Map<String, Object> variables;
    private Instant timestamp;

    public ProcessResponse() {
        this.timestamp = Instant.now();
    }

    public ProcessResponse(Long processInstanceKey, String bpmnProcessId, String status,
                           String currentActivity, Map<String, Object> variables) {
        this.processInstanceKey = processInstanceKey;
        this.bpmnProcessId = bpmnProcessId;
        this.status = status;
        this.currentActivity = currentActivity;
        this.variables = variables;
        this.timestamp = Instant.now();
    }

    public Long getProcessInstanceKey() {
        return processInstanceKey;
    }

    public void setProcessInstanceKey(Long processInstanceKey) {
        this.processInstanceKey = processInstanceKey;
    }

    public String getBpmnProcessId() {
        return bpmnProcessId;
    }

    public void setBpmnProcessId(String bpmnProcessId) {
        this.bpmnProcessId = bpmnProcessId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(String currentActivity) {
        this.currentActivity = currentActivity;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
