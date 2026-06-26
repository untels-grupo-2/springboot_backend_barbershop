package com.diamondbarbershop.apibarbershop.shared.infrastructure.rest;

public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;

    public ApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> succes(String message, T data){
        return new ApiResponse<>(200,message,data);
    }

    public static <T> ApiResponse<T> error(String message, T data){
        return new ApiResponse<>(400, message, data);
    }

    // Getters y Setters
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}