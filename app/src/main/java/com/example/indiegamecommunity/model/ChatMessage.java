package com.example.indiegamecommunity.model;

public class ChatMessage {
    public static final int TYPE_USER = 1;
    public static final int TYPE_AI = 2;

    private int type;
    private String message;
    private long time;

    public ChatMessage() {}

    public ChatMessage(int type, String message) {
        this.type = type;
        this.message = message;
        this.time = System.currentTimeMillis();
    }

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public long getTime() { return time; }
    public void setTime(long time) { this.time = time; }
}