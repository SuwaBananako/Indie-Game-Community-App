package com.example.indiegamecommunity;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;

public class IndieGameCommunityApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        applyGlobalFontScale();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        applyGlobalFontScale();
    }

    private void applyGlobalFontScale() {
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        float scale = prefs.getFloat("font_scale", 1.0f);
        Configuration config = getResources().getConfiguration();
        if (config.fontScale != scale) {
            config.fontScale = scale;
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        }
    }
}
