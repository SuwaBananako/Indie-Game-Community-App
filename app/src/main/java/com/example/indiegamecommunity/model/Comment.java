package com.example.indiegamecommunity.model;

public class Comment {
    private int id;
    private int postId;
    private String username;
    private String content;
    private String imagePath;
    private String targetType;
    private long createTime;

    public Comment() {}

    public Comment(int postId, String username, String content) {
        this.postId = postId;
        this.username = username;
        this.content = content;
        this.imagePath = "";
        this.targetType = "";
        this.createTime = System.currentTimeMillis();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
}
