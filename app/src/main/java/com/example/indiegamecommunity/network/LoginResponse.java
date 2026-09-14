package com.example.indiegamecommunity.network;

public class LoginResponse {
    public String token = "";
    public String username = "";
    public String nickname = "";
    public String avatar = "";
    public boolean isAdmin = false;

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public String getNickname() { return nickname; }
    public String getAvatar() { return avatar; }
    public boolean getIsAdmin() { return isAdmin; }
}
