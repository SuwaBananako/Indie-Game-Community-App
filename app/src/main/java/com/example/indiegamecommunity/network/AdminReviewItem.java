package com.example.indiegamecommunity.network;

public class AdminReviewItem {
    public long id = 0;
    public String title = null;
    public String name = null;
    public String gameName = null;
    public String content = null;
    public String description = null;
    public String username = null;
    public String authorUsername = null;
    public String status = null;
    public double targetAmount = 0;
    public long createTime = 0;

    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getName() { return name; }
    public String getGameName() { return gameName; }
    public String getContent() { return content; }
    public String getDescription() { return description; }
    public String getUsername() { return username; }
    public String getAuthorUsername() { return authorUsername; }
    public String getStatus() { return status; }
    public double getTargetAmount() { return targetAmount; }
    public long getCreateTime() { return createTime; }
}
