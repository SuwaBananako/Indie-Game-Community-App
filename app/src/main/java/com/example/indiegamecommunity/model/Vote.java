package com.example.indiegamecommunity.model;

public class Vote {
    private int id;
    private String gameName;
    private String description;
    private String contentImages;
    private String iconPath;
    private String bannerPath;
    private String authorUsername;
    private String status;
    private int voteCount;
    private long createTime;

    public Vote() {}

    public Vote(String gameName) {
        this.gameName = gameName;
        this.description = "";
        this.contentImages = "";
        this.iconPath = "";
        this.bannerPath = "";
        this.authorUsername = "";
        this.status = "";
        this.voteCount = 0;
        this.createTime = System.currentTimeMillis();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getGameName() { return gameName; }
    public void setGameName(String gameName) { this.gameName = gameName; }
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
    public int getVoteCount() { return voteCount; }
    public void setVoteCount(int voteCount) { this.voteCount = voteCount; }
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
}
