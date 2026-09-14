package com.example.indiegamecommunity.network;

public class PromoDto {
    public long id = 0;
    public String name = "";
    public String description = "";
    public String coverImage = "";
    public String contentImages = "";
    public String authorUsername = "";
    public int dislikeCount = 0;
    public String platform = "";
    public String status = "";
    public long createTime = 0;

    public long getId() { return id; } public void setId(long v) { this.id = v; }
    public String getName() { return name; } public void setName(String v) { this.name = v; }
    public String getDescription() { return description; } public void setDescription(String v) { this.description = v; }
    public String getCoverImage() { return coverImage; } public void setCoverImage(String v) { this.coverImage = v; }
    public String getContentImages() { return contentImages; } public void setContentImages(String v) { this.contentImages = v; }
    public String getAuthorUsername() { return authorUsername; } public void setAuthorUsername(String v) { this.authorUsername = v; }
    public int getDislikeCount() { return dislikeCount; } public void setDislikeCount(int v) { this.dislikeCount = v; }
    public String getPlatform() { return platform; } public void setPlatform(String v) { this.platform = v; }
    public String getStatus() { return status; } public void setStatus(String v) { this.status = v; }
    public long getCreateTime() { return createTime; } public void setCreateTime(long v) { this.createTime = v; }
}
