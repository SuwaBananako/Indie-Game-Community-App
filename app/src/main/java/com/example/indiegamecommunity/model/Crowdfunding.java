package com.example.indiegamecommunity.model;

public class Crowdfunding {
    private int id;
    private String name;
    private String description;
    private String contentImages;
    private String iconPath;
    private String bannerPath;
    private String authorUsername;
    private String status;
    private double targetAmount;
    private double currentAmount;
    private int supporterCount;
    private long createTime;

    public Crowdfunding() {}

    public Crowdfunding(String name, String description, double targetAmount) {
        this.name = name;
        this.description = description;
        this.contentImages = "";
        this.iconPath = "";
        this.bannerPath = "";
        this.authorUsername = "";
        this.status = "";
        this.targetAmount = targetAmount;
        this.currentAmount = 0;
        this.supporterCount = 0;
        this.createTime = System.currentTimeMillis();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getContentImages() { return contentImages; }
    public void setContentImages(String contentImages) { this.contentImages = contentImages; }
    public String getIconPath() { return iconPath; }
    public void setIconPath(String iconPath) { this.iconPath = iconPath; }
    public String getBannerPath() { return bannerPath; }
    public void setBannerPath(String bannerPath) { this.bannerPath = bannerPath; }
    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getTargetAmount() { return targetAmount; }
    public void setTargetAmount(double targetAmount) { this.targetAmount = targetAmount; }
    public double getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(double currentAmount) { this.currentAmount = currentAmount; }
    public int getSupporterCount() { return supporterCount; }
    public void setSupporterCount(int supporterCount) { this.supporterCount = supporterCount; }
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }

    public double getProgress() {
        if (targetAmount <= 0) return 0;
        return (currentAmount / targetAmount) * 100;
    }
}
