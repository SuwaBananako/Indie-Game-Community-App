package com.example.indiegamecommunity.network;

public class ApiResponse<T> {
    public int code = 200;
    public String message = "success";
    public T data = null;

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
