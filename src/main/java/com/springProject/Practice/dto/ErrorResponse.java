package com.springProject.Practice.dto;


import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {

    private LocalDateTime timeStamp;
    private int status;
    private String message;
    private Map<String,String> error;


    public ErrorResponse(LocalDateTime timeStamp, int status, String message, Map<String, String> error) {
        this.timeStamp = timeStamp;
        this.status = status;
        this.message = message;
        this.error = error;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getError() {
        return error;
    }

    public void setError(Map<String, String> error) {
        this.error = error;
    }

}
