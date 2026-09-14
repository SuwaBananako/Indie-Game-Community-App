package com.example.indiegamecommunity.model;

public class GamePromo {
    private int id;
    private String name;
    private String description;
    private String screenshot;
    private String contentImages;
    private String iconPath;
    private String bannerPath;
    private String authorUsername;
    private String status;
    private String downloadUrl;
    private long createTime;

    public GamePromo() {}

    public GamePromo(String name, String description, String screenshot, String downloadUrl) {
        this.name = name;
        this.description = description;
        this.screenshot = screenshot;
        this.contentImages = "";
        this.iconPath = "";
        this.bannerPath = "";
        this.authorUsername = "";
        this.status = "";
        this.downloadUrl = downloadUrl;
        this.createTime = System.currentTimeMillis();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getScreenshot() { return screenshot; }
    public void setScreenshot(String screenshot) { this.screenshot = screenshot; }
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
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
}
