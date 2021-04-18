package com.ihouse.backend.service;

import lombok.Data;

@Data
public class ServiceResult<T> {


    private boolean success;

    private String message;

    private T result;

    public ServiceResult(boolean success, String message, T result) {
        this.success = success;
        this.message = message;
        this.result = result;
    }

    public ServiceResult( T result) {
        this.success = true;
        this.message = "success";
        this.result = result;
    }

    public ServiceResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ServiceResult() {
        this.success = true;
        this.message = "success";
    }
}
