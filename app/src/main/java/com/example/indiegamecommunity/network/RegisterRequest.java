package com.example.indiegamecommunity.network;

public class RegisterRequest {
    public String username = "";
    public String password = "";
    public String nickname = null;
    public boolean isAdmin = false;
    public RegisterRequest() {}
    public RegisterRequest(String username, String password, String nickname, boolean isAdmin) {
        this.username = username; this.password = password; this.nickname = nickname; this.isAdmin = isAdmin;
    }
}
