package com.example.indiegamecommunity.network;

public class PostUpdateBody {
    public String title;
    public String content;
    public String category;
    public PostUpdateBody(String title, String content, String category) {
        this.title = title; this.content = content; this.category = category;
    }
}
