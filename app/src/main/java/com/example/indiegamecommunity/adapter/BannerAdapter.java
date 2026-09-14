package com.example.indiegamecommunity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.indiegamecommunity.R;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.ViewHolder> {
    private Context context;
    private List<BannerItem> bannerList;
    private OnBannerClickListener listener;

    public interface OnBannerClickListener {
        void onBannerClick(BannerItem item);
    }

    public static class BannerItem {
        private int imageRes;
        private String title;
        private String subtitle;
        private String linkUrl;
        private int bgColor;

        public BannerItem(int imageRes, String title, String subtitle, int bgColor, String linkUrl) {
            this.imageRes = imageRes;
            this.title = title;
            this.subtitle = subtitle;
            this.bgColor = bgColor;
            this.linkUrl = linkUrl;
        }

        public int getImageRes() { return imageRes; }
        public String getTitle() { return title; }
        public String getSubtitle() { return subtitle; }
        public int getBgColor() { return bgColor; }
        public String getLinkUrl() { return linkUrl; }
    }

    public BannerAdapter(Context context, List<BannerItem> bannerList, OnBannerClickListener listener) {
        this.context = context;
        this.bannerList = bannerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_banner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BannerItem item = bannerList.get(position);
        holder.ivBanner.setImageResource(item.getImageRes());
        holder.ivBanner.setBackgroundColor(item.getBgColor());
        holder.tvTitle.setText(item.getTitle());
        holder.tvSubtitle.setText(item.getSubtitle());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBannerClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bannerList != null ? bannerList.size() : 0;
    }

    public List<BannerItem> getBannerList() {
        return bannerList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBanner;
        TextView tvTitle, tvSubtitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBanner = itemView.findViewById(R.id.ivBanner);
            tvTitle = itemView.findViewById(R.id.tvBannerTitle);
            tvSubtitle = itemView.findViewById(R.id.tvBannerSubtitle);
        }
    }
}
