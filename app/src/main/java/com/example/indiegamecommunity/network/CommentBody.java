package com.example.indiegamecommunity.network;

public class CommentBody {
    public String username = "";
    public String content = "";
    public String imagePath = "";
    public CommentBody() {}
    public CommentBody(String username, String content, String imagePath) {
        this.username = username; this.content = content; this.imagePath = imagePath;
    }
}
