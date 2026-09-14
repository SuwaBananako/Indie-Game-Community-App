package com.example.indiegamecommunity.network;

public class PostDto {
    public long id = 0;
    public String title = "";
    public String content = "";
    public String category = "";
    public String coverImage = "";
    public String contentImages = "";
    public String username = "";
    public int likeCount = 0;
    public int commentCount = 0;
    public int collectCount = 0;
    public String status = "";
    public long createTime = 0;
    public long updateTime = 0;

    public long getId() { return id; } public  void setId(long id) { this.id = id; }
    public String getTitle() { return title; } public void setTitle(String v) { this.title = v; }
    public String getContent() { return content; } public void setContent(String v) { this.content = v; }
    public String getCategory() { return category; } public void setCategory(String v) { this.category = v; }
    public String getCoverImage() { return coverImage; } public void setCoverImage(String v) { this.coverImage = v; }
    public String getContentImages() { return contentImages; } public void setContentImages(String v) { this.contentImages = v; }
    public String getUsername() { return username; } public void setUsername(String v) { this.username = v; }
    public int getLikeCount() { return likeCount; } public void setLikeCount(int v) { this.likeCount = v; }
    public int getCommentCount() { return commentCount; } public void setCommentCount(int v) { this.commentCount = v; }
    public int getCollectCount() { return collectCount; } public void setCollectCount(int v) { this.collectCount = v; }
    public String getStatus() { return status; } public void setStatus(String v) { this.status = v; }
    public long getCreateTime() { return createTime; } public void setCreateTime(long v) { this.createTime = v; }
    public long getUpdateTime() { return updateTime; } public void setUpdateTime(long v) { this.updateTime = v; }
}
