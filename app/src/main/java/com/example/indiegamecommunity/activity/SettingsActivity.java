package com.example.indiegamecommunity.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.example.indiegamecommunity.R;

public class SettingsActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "app_settings";
    private static final String KEY_FONT_SCALE = "font_scale";
    private static final String KEY_SAKURA = "sakura_effect";
    private static final String KEY_NIGHT_MODE = "night_mode";

    private SeekBar sbFontSize, sbBrightness;
    private TextView tvFontPreview;
    private SwitchCompat swSakura, swNightMode;
    private Button btnResetDefault;
    private float baseDensity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        baseDensity = getResources().getDisplayMetrics().density;

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        float savedScale = prefs.getFloat(KEY_FONT_SCALE, 1.0f);
        applyFontScale(savedScale);

        setContentView(R.layout.activity_settings);

        tvFontPreview = findViewById(R.id.tvFontPreview);
        sbFontSize = findViewById(R.id.sbFontSize);
        sbBrightness = findViewById(R.id.sbBrightness);
        swSakura = findViewById(R.id.swSakura);
        swNightMode = findViewById(R.id.swNightMode);
        btnResetDefault = findViewById(R.id.btnResetDefault);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // 樱花开关
        swSakura.setChecked(prefs.getBoolean(KEY_SAKURA, true));
        swSakura.setOnCheckedChangeListener((button, checked) -> {
            prefs.edit().putBoolean(KEY_SAKURA, checked).apply();
            Toast.makeText(this, checked ? "樱花特效已开启" : "樱花特效已关闭", Toast.LENGTH_SHORT).show();
        });

        // 夜间模式开关
        swNightMode.setChecked(prefs.getBoolean(KEY_NIGHT_MODE, false));
        swNightMode.setOnCheckedChangeListener((button, checked) -> {
            prefs.edit().putBoolean(KEY_NIGHT_MODE, checked).apply();
            AppCompatDelegate.setDefaultNightMode(
                    checked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            recreate();
        });

        // 字体 SeekBar
        int fontProgress = Math.round((savedScale - 0.8f) / 0.1f);
        sbFontSize.setProgress(Math.max(0, Math.min(10, fontProgress)));
        updateFontPreview(sbFontSize.getProgress());

        sbFontSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateFontPreview(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {
                float scale = 0.8f + seekBar.getProgress() * 0.1f;
                saveFontScale(scale);
                recreate();
            }
        });

        // 亮度
        int currentBrightness = getScreenBrightness();
        sbBrightness.setProgress(currentBrightness);
        sbBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {
                setScreenBrightness(seekBar.getProgress());
            }
        });

        // 恢复默认
        btnResetDefault.setOnClickListener(v -> {
            SharedPreferences p = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            float defaultScale = 1.0f;
            saveFontScale(defaultScale);
            applyFontScale(defaultScale);
            sbFontSize.setProgress(2);
            updateFontPreview(2);

            int defaultBrightness = 128;
            sbBrightness.setProgress(defaultBrightness);
            setScreenBrightness(defaultBrightness);

            p.edit().putBoolean(KEY_SAKURA, true).apply();
            swSakura.setChecked(true);

            p.edit().putBoolean(KEY_NIGHT_MODE, false).apply();
            swNightMode.setChecked(false);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

            Toast.makeText(this, "已恢复默认设置", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateFontPreview(int progress) {
        float scale = 0.8f + progress * 0.1f;
        float px = 16 * scale * baseDensity;
        tvFontPreview.setTextSize(TypedValue.COMPLEX_UNIT_PX, px);
        tvFontPreview.setText("预览 ABC 123 | 缩放 " + String.format("%.1f", scale) + "x");
    }

    private void saveFontScale(float scale) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
                .putFloat(KEY_FONT_SCALE, scale).apply();
    }

    private void applyFontScale(float scale) {
        android.content.res.Configuration config = getResources().getConfiguration();
        config.fontScale = scale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private int getScreenBrightness() {
        try {
            return Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            return 128;
        }
    }

    private void setScreenBrightness(int brightness) {
        if (Settings.System.canWrite(this)) {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.screenBrightness = brightness / 255f;
            getWindow().setAttributes(lp);
        } else {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(android.net.Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            Toast.makeText(this, "请授权修改系统设置后重新调整亮度", Toast.LENGTH_LONG).show();
        }
    }

    public static float getFontScale(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getFloat(KEY_FONT_SCALE, 1.0f);
    }

    public static boolean isNightMode(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_NIGHT_MODE, false);
    }

    public static boolean isSakuraOn(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_SAKURA, true);
    }
}
