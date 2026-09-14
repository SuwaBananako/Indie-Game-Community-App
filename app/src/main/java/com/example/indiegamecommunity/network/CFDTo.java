package com.example.indiegamecommunity.network;

public class CFDTo {
    public long id = 0;
    public String name = "";
    public String description = "";
    public String coverImage = "";
    public String contentImages = "";
    public String authorUsername = "";
    public double targetAmount = 0.0;
    public double currentAmount = 0.0;
    public int supporterCount = 0;
    public String status = "";
    public long createTime = 0;

    public long getId() { return id; } public void setId(long v) { this.id = v; }
    public String getName() { return name; } public void setName(String v) { this.name = v; }
    public String getDescription() { return description; } public void setDescription(String v) { this.description = v; }
    public String getCoverImage() { return coverImage; } public void setCoverImage(String v) { this.coverImage = v; }
    public String getContentImages() { return contentImages; } public void setContentImages(String v) { this.contentImages = v; }
    public String getAuthorUsername() { return authorUsername; } public void setAuthorUsername(String v) { this.authorUsername = v; }
    public double getTargetAmount() { return targetAmount; } public void setTargetAmount(double v) { this.targetAmount = v; }
    public double getCurrentAmount() { return currentAmount; } public void setCurrentAmount(double v) { this.currentAmount = v; }
    public int getSupporterCount() { return supporterCount; } public void setSupporterCount(int v) { this.supporterCount = v; }
    public String getStatus() { return status; } public void setStatus(String v) { this.status = v; }
    public long getCreateTime() { return createTime; } public void setCreateTime(long v) { this.createTime = v; }
}
