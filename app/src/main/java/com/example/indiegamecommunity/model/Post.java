package com.example.indiegamecommunity.model;

public class Post {
    private int id;
    private String username;
    private String title;
    private String content;
    private String coverImage;
    private String contentImages;
    private int likeCount;
    private int collectCount;
    private String category;
    private String status;
    private long createTime;

    public Post() {}

    public Post(String username, String title, String content, String category) {
        this.username = username;
        this.title = title;
        this.content = content;
        this.category = category;
        this.coverImage = "";
        this.contentImages = "";
        this.likeCount = 0;
        this.collectCount = 0;
        this.status = "";
        this.createTime = System.currentTimeMillis();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public String getContentImages() { return contentImages; }
    public void setContentImages(String contentImages) { this.contentImages = contentImages; }
    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
    public int getCollectCount() { return collectCount; }
    public void setCollectCount(int collectCount) { this.collectCount = collectCount; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
}
