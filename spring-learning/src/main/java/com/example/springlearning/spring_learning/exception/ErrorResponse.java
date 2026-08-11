package com.example.springlearning.spring_learning.exception;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private List<FieldErrorResponse> errors;

    public ErrorResponse(int status, String message, LocalDateTime timestamp, List<FieldErrorResponse> errors) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.errors = errors;
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<FieldErrorResponse> getErrors() {
        return errors;
    }

    public void setErrors(List<FieldErrorResponse> errors) {
        this.errors = errors;
    }

}
