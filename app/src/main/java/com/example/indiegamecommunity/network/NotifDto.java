package com.example.indiegamecommunity.network;

public class NotifDto {
    public long id = 0;
    public String username = "";
    public String message = "";
    public boolean isRead = false;
    public long createTime = 0;

    public long getId() { return id; }
    public String getUsername() { return username; }
    public String getMessage() { return message; }
    public boolean isRead() { return isRead; }
    public long getCreateTime() { return createTime; }
}
