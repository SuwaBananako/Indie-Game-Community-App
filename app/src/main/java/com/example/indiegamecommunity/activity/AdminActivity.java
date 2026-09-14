package com.example.indiegamecommunity.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.indiegamecommunity.R;
import com.example.indiegamecommunity.fragment.AdminCommentFragment;
import com.example.indiegamecommunity.fragment.AdminGameFragment;
import com.example.indiegamecommunity.fragment.AdminPostFragment;
import com.example.indiegamecommunity.fragment.AdminReviewFragment;
import com.example.indiegamecommunity.fragment.AdminUserFragment;
import com.example.indiegamecommunity.utils.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    private SharedPreferencesHelper sharedPreferencesHelper;
    private List<TabItem> tabs = new ArrayList<>();

    private static class TabItem {
        LinearLayout layout;
        TextView label;
        Fragment fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        sharedPreferencesHelper = new SharedPreferencesHelper(this);

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            sharedPreferencesHelper.clearLoginInfo();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        TextView tvAdminUser = findViewById(R.id.tvAdminUser);
        tvAdminUser.setText(sharedPreferencesHelper.getUsername());

        initTabs();

        if (savedInstanceState == null) {
            selectTab(0);
        }
    }

    private void initTabs() {
        addTab(R.id.tab_review, R.id.tv_review, new AdminReviewFragment());
        addTab(R.id.tab_users, R.id.tv_users, new AdminUserFragment());
        addTab(R.id.tab_posts, R.id.tv_posts, new AdminPostFragment());
        addTab(R.id.tab_comments, R.id.tv_comments, new AdminCommentFragment());
        addTab(R.id.tab_games, R.id.tv_games, new AdminGameFragment());
    }

    private void addTab(int layoutId, int labelId, Fragment fragment) {
        TabItem item = new TabItem();
        item.layout = findViewById(layoutId);
        item.label = findViewById(labelId);
        item.fragment = fragment;

        int index = tabs.size();
        item.layout.setOnClickListener(v -> selectTab(index));
        tabs.add(item);
    }

    private void selectTab(int index) {
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

    @Override
    public void onBackPressed() {
        // 管理员退出时回到登录页
        sharedPreferencesHelper.clearLoginInfo();
        super.onBackPressed();
    }
}
