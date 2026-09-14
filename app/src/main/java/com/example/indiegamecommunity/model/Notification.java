package com.example.indiegamecommunity.model;

public class Notification {
    private int id;
    private String username;
    private String message;
    private boolean read;
    private long createTime;

    public Notification() {}

    public Notification(String username, String message) {
        this.username = username;
        this.message = message;
        this.read = false;
        this.createTime = System.currentTimeMillis();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
}
