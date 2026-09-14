package com.example.indiegamecommunity.network;

public class NetworkConfig {
    // 模拟器用 10.0.2.2，真机用电脑 IP
    public String BASE_URL = "http://10.0.2.2:8080/";
    private static NetworkConfig instance;
    public static NetworkConfig getInstance() {
        if (instance == null) instance = new NetworkConfig();
        return instance;
    }
    public String getBASE_URL() { return BASE_URL; }
}
