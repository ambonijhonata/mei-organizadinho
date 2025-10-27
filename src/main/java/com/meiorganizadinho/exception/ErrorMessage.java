package com.meiorganizadinho.exception;

import java.time.Instant;

public class ErrorMessage {
    private String error;
    private String details;
    private Instant timestamp;

    public ErrorMessage(String error, String details){
        this.error = error;
        this.details = details;
        this.timestamp = Instant.now();
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
