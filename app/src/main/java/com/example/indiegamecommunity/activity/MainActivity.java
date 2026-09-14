package com.example.indiegamecommunity.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.indiegamecommunity.R;
import com.example.indiegamecommunity.fragment.CrowdfundingFragment;
import com.example.indiegamecommunity.fragment.ForumFragment;
import com.example.indiegamecommunity.fragment.GamePromoFragment;
import com.example.indiegamecommunity.fragment.ProfileFragment;
import com.example.indiegamecommunity.fragment.TiebaFragment;
import com.example.indiegamecommunity.fragment.VoteFragment;
import com.example.indiegamecommunity.network.ApiClient;
import com.example.indiegamecommunity.network.ApiHelper;
import com.example.indiegamecommunity.network.ApiResponse;
import com.example.indiegamecommunity.network.NotifDto;
import com.example.indiegamecommunity.utils.SharedPreferencesHelper;
import com.example.indiegamecommunity.view.SakuraView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_SETTINGS = "app_settings";

    private SharedPreferencesHelper sharedPreferencesHelper;
    private SakuraView sakuraView;
    private List<TabItem> tabs = new ArrayList<>();
    private int currentTabIndex = 0;
    private boolean notificationChecked = false;

    private static class TabItem {
        LinearLayout layout;
        ImageView icon;
        TextView label;
        Fragment fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyNightMode();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferencesHelper = new SharedPreferencesHelper(this);
        ApiClient.init(() -> sharedPreferencesHelper.getToken());

        sakuraView = findViewById(R.id.sakuraView);
        applySakuraEffect();
        initTabs();

        if (savedInstanceState == null) {
            selectTab(0);
        }
    }

    private void applyNightMode() {
        SharedPreferences prefs = getSharedPreferences(PREFS_SETTINGS, MODE_PRIVATE);
        boolean nightMode = prefs.getBoolean("night_mode", false);
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void applySakuraEffect() {
        SharedPreferences prefs = getSharedPreferences(PREFS_SETTINGS, MODE_PRIVATE);
        boolean sakuraOn = prefs.getBoolean("sakura_effect", true);
        if (sakuraView != null) {
            if (sakuraOn) {
                sakuraView.setVisibility(View.VISIBLE);
                sakuraView.start();
            } else {
                sakuraView.stop();
                sakuraView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        applySakuraEffect();

        if (!notificationChecked) {
            notificationChecked = true;
            checkReviewNotifications();
        }
    }

    private void checkReviewNotifications() {
        String username = sharedPreferencesHelper.getUsername();
        if (username.isEmpty()) return;

        ApiHelper.execute(() -> {
            retrofit2.Response<ApiResponse<List<NotifDto>>> response =
                ApiClient.getApiService().getNotifications(username).execute();
            if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                return response.body().getData();
            }
            return new ArrayList<NotifDto>();
        }, new ApiHelper.Callback<List<NotifDto>>() {
            @Override
            public void onSuccess(List<NotifDto> notifications) {
                List<NotifDto> unreadApproved = new ArrayList<>();
                for (NotifDto n : notifications) {
                    if (!n.isRead() && n.getMessage() != null && n.getMessage().contains("通过")) {
                        unreadApproved.add(n);
                    }
                }
                if (!unreadApproved.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < Math.min(unreadApproved.size(), 5); i++) {
                        if (i > 0) sb.append("\n\n");
                        sb.append("• ").append(unreadApproved.get(i).getMessage());
                    }
                    if (unreadApproved.size() > 5) {
                        sb.append("\n\n...等 ").append(unreadApproved.size()).append(" 条通知");
                    }
                    new AlertDialog.Builder(MainActivity.this)
                        .setTitle("审核通知")
                        .setMessage(sb.toString())
                        .setPositiveButton("知道了", (d, w) -> {
                            ApiHelper.execute(() -> {
                                ApiClient.getApiService().markAllRead(username).execute();
                                return "ok";
                            }, null);
                        })
                        .setCancelable(false)
                        .show();
                }
            }
            @Override
            public void onError(String message) { /* 静默处理 */ }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sakuraView != null) sakuraView.stop();
    }

    private void initTabs() {
        addTab(R.id.tab_forum, R.id.iv_forum, R.id.tv_forum, new ForumFragment());
        addTab(R.id.tab_tieba, R.id.iv_tieba, R.id.tv_tieba, new TiebaFragment());
        addTab(R.id.tab_promo, R.id.iv_promo, R.id.tv_promo, new GamePromoFragment());
        addTab(R.id.tab_crowdfunding, R.id.iv_crowdfunding, R.id.tv_crowdfunding, new CrowdfundingFragment());
        addTab(R.id.tab_vote, R.id.iv_vote, R.id.tv_vote, new VoteFragment());
        addTab(R.id.tab_profile, R.id.iv_profile, R.id.tv_profile, new ProfileFragment());
    }

    private void addTab(int layoutId, int iconId, int labelId, Fragment fragment) {
        TabItem item = new TabItem();
        item.layout = findViewById(layoutId);
        item.icon = findViewById(iconId);
        item.label = findViewById(labelId);
        item.fragment = fragment;

        int index = tabs.size();
        item.layout.setOnClickListener(v -> selectTab(index));
        tabs.add(item);
    }

    private void selectTab(int index) {
        currentTabIndex = index;
        int selectedColor = ContextCompat.getColor(this, R.color.acfun_red);
        int unselectedColor = ContextCompat.getColor(this, R.color.acfun_text_light);

        for (int i = 0; i < tabs.size(); i++) {
            TabItem tab = tabs.get(i);
            boolean selected = (i == index);
            tab.layout.setSelected(selected);
            tab.label.setTextColor(selected ? selectedColor : unselectedColor);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, tabs.get(index).fragment)
                .commit();
    }

    public String getCurrentUsername() {
        return sharedPreferencesHelper.getUsername();
    }
}
