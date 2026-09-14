package com.example.indiegamecommunity.network;

public class VoteDto {
    public long id = 0;
    public String gameName = "";
    public String description = "";
    public String contentImages = "";
    public String iconPath = "";
    public String bannerPath = "";
    public String authorUsername = "";
    public String status = "";
    public int voteCount = 0;
    public long createTime = 0;

    public long getId() { return id; } public void setId(long v) { this.id = v; }
    public String getGameName() { return gameName; } public void setGameName(String v) { this.gameName = v; }
    public String getDescription() { return description; } public void setDescription(String v) { this.description = v; }
    public String getContentImages() { return contentImages; } public void setContentImages(String v) { this.contentImages = v; }
    public String getIconPath() { return iconPath; } public void setIconPath(String v) { this.iconPath = v; }
    public String getBannerPath() { return bannerPath; } public void setBannerPath(String v) { this.bannerPath = v; }
    public String getAuthorUsername() { return authorUsername; } public void setAuthorUsername(String v) { this.authorUsername = v; }
    public String getStatus() { return status; } public void setStatus(String v) { this.status = v; }
    public int getVoteCount() { return voteCount; } public void setVoteCount(int v) { this.voteCount = v; }
    public long getCreateTime() { return createTime; } public void setCreateTime(long v) { this.createTime = v; }
}
