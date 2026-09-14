package com.example.indiegamecommunity.network;

public class CommentDto {
    public long id = 0;
    public long postId = 0;
    public String username = "";
    public String content = "";
    public String imagePath = "";
    public long createTime = 0;

    public long getId() { return id; }
    public long getPostId() { return postId; }
    public String getUsername() { return username; }
    public String getContent() { return content; }
    public String getImagePath() { return imagePath; }
    public long getCreateTime() { return createTime; }
}
