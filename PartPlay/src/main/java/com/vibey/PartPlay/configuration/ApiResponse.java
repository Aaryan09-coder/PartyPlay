package com.vibey.PartPlay.configuration;

import java.util.List;

public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private List<String> errors;

    // Constructor for success response with data
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Constructor for error response
    public ApiResponse(boolean success, String message, List<String> errors){
        this.success = success;
        this.message = message;
        this.errors = errors;
    }

    // Simple success response
    public ApiResponse(boolean success, String message){
        this.success = success;
        this.message = message;
    }

    // Getters

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public List<String> getErrors() {
        return errors;
    }


    // Setters

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
