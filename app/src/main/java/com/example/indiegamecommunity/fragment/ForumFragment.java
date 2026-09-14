package com.example.indiegamecommunity.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.indiegamecommunity.R;
import com.example.indiegamecommunity.adapter.BannerAdapter;
import com.example.indiegamecommunity.adapter.CategoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class ForumFragment extends Fragment {
    private RecyclerView rvBanner;
    private GridView gvCategories;
    private EditText etSearch;
    private Button btnSearch;
    private LinearLayout layoutDots;
    private BannerAdapter bannerAdapter;
    private CategoryAdapter categoryAdapter;

    private Handler bannerHandler = new Handler();
    private int currentBannerPos = 0;
    private int totalBanners = 0;
    private boolean isBannerScrolling = false;
    private Runnable bannerRunnable;
    private LinearLayoutManager bannerLayoutManager;
    private List<View> dotViews = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum, container, false);

        rvBanner = view.findViewById(R.id.rvBanner);
        gvCategories = view.findViewById(R.id.gvCategories);
        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        layoutDots = view.findViewById(R.id.layoutDots);

        initBanner();
        initCategories();

        btnSearch.setOnClickListener(v -> {
            String keyword = etSearch.getText().toString().trim();
            if (!keyword.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.baidu.com/s?wd=" + keyword));
                startActivity(intent);
            }
        });

        gvCategories.setOnItemClickListener((parent, v, position, id) -> {
            CategoryAdapter.CategoryItem item = (CategoryAdapter.CategoryItem) parent.getItemAtPosition(position);
            Intent intent = new Intent(getActivity(), com.example.indiegamecommunity.activity.CategoryDetailActivity.class);
            intent.putExtra("category_name", item.getName());
            startActivity(intent);
        });

        return view;
    }

    private void initBanner() {
        List<BannerAdapter.BannerItem> bannerList = new ArrayList<>();
        bannerList.add(new BannerAdapter.BannerItem(
                R.drawable.ba1, "Steam 独立游戏特卖", "海量折扣，探索独立佳作",
                Color.parseColor("#FD4C5C"), "https://store.steampowered.com/"));
        bannerList.add(new BannerAdapter.BannerItem(
                R.drawable.ba2, "itch.io 热门新游", "独立开发者的创意乐园",
                Color.parseColor("#E03A4A"), "https://itch.io/"));
        bannerList.add(new BannerAdapter.BannerItem(
                R.drawable.ba3, "TapTap 游戏社区", "发现好游戏，一起聊游戏",
                Color.parseColor("#FF7B88"), "https://www.taptap.cn/"));
        bannerList.add(new BannerAdapter.BannerItem(
                R.drawable.ba4, "IndieNova 独立游戏", "中国独立游戏开发者社区",
                Color.parseColor("#FF8C42"), "https://indienova.com/"));

        totalBanners = bannerList.size();
        bannerAdapter = new BannerAdapter(getContext(), bannerList, this::onBannerClicked);

        bannerLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvBanner.setLayoutManager(bannerLayoutManager);
        rvBanner.setHasFixedSize(true);
        rvBanner.setItemViewCacheSize(totalBanners);
        rvBanner.setAdapter(bannerAdapter);

        // 使用 PagerSnapHelper 实现逐页滚动
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvBanner);

        // 监听滚动状态，滚动空闲时才开始计时
        rvBanner.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isBannerScrolling = false;
                    // 更新当前位置
                    int firstVisible = bannerLayoutManager.findFirstVisibleItemPosition();
                    if (firstVisible >= 0 && firstVisible < totalBanners) {
                        currentBannerPos = firstVisible;
                        updateDots();
                    }
                    // 滚动完成后重新开始计时
                    scheduleNextScroll();
                } else {
                    isBannerScrolling = true;
                    // 取消 pending 的滚动任务
                    bannerHandler.removeCallbacks(bannerRunnable);
                }
            }
        });

        // 创建圆点指示器
        initDots();
        updateDots();

        // 启动自动轮播
        scheduleNextScroll();
    }

    private void initDots() {
        layoutDots.removeAllViews();
        dotViews.clear();
        int dotSize = (int) (8 * getResources().getDisplayMetrics().density);
        int dotMargin = (int) (4 * getResources().getDisplayMetrics().density);

        for (int i = 0; i < totalBanners; i++) {
            View dot = new View(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dotSize, dotSize);
            params.setMargins(dotMargin, 0, dotMargin, 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(R.drawable.dot_unselected);
            layoutDots.addView(dot);
            dotViews.add(dot);
        }
    }

    private void updateDots() {
        for (int i = 0; i < dotViews.size(); i++) {
            dotViews.get(i).setBackgroundResource(
                    i == currentBannerPos ? R.drawable.dot_selected : R.drawable.dot_unselected);
        }
    }

    private void scheduleNextScroll() {
        bannerHandler.removeCallbacks(bannerRunnable);
        bannerRunnable = () -> {
            if (isBannerScrolling || totalBanners <= 1) {
                scheduleNextScroll();
                return;
            }
            currentBannerPos = (currentBannerPos + 1) % totalBanners;
            rvBanner.smoothScrollToPosition(currentBannerPos);
            updateDots();
        };
        bannerHandler.postDelayed(bannerRunnable, 4000);
    }

    private void onBannerClicked(BannerAdapter.BannerItem item) {
        String url = item.getLinkUrl();
        if (url != null && !url.isEmpty()) {
            showLinkConfirmDialog(item, url);
        }
    }

    private void showLinkConfirmDialog(BannerAdapter.BannerItem item, String url) {
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle(item.getTitle())
                .setMessage("即将跳转到外部网站：\n" + url + "\n\n是否继续？")
                .setPositiveButton("访问", (dialog, which) -> {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    } catch (Exception e) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://" + url)));
                        } catch (Exception ignored) {}
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void initCategories() {
        List<CategoryAdapter.CategoryItem> categoryList = new ArrayList<>();
        categoryList.add(new CategoryAdapter.CategoryItem(R.drawable.tu1, "游戏推荐", Color.parseColor("#FD4C5C")));
        categoryList.add(new CategoryAdapter.CategoryItem(R.drawable.tu2, "开发日志", Color.parseColor("#E03A4A")));
        categoryList.add(new CategoryAdapter.CategoryItem(R.drawable.tu3, "游戏美术", Color.parseColor("#FF7B88")));
        categoryList.add(new CategoryAdapter.CategoryItem(R.drawable.tu4, "游戏音乐", Color.parseColor("#FF8C42")));
        categoryList.add(new CategoryAdapter.CategoryItem(R.drawable.tu5, "行业资讯", Color.parseColor("#FFC107")));
        categoryList.add(new CategoryAdapter.CategoryItem(R.drawable.tu6, "活动赛事", Color.parseColor("#FD4C5C")));
        categoryList.add(new CategoryAdapter.CategoryItem(R.drawable.tu7, "问答求助", Color.parseColor("#FF7B88")));
        categoryList.add(new CategoryAdapter.CategoryItem(R.drawable.tu8, "闲聊灌水", Color.parseColor("#FF8C42")));

        categoryAdapter = new CategoryAdapter(getContext(), categoryList);
        gvCategories.setAdapter(categoryAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        bannerHandler.removeCallbacks(bannerRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        scheduleNextScroll();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bannerHandler.removeCallbacks(bannerRunnable);
    }
}
