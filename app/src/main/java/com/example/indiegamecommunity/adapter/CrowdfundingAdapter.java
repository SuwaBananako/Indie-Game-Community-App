package com.example.indiegamecommunity.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.indiegamecommunity.R;
import com.example.indiegamecommunity.model.Crowdfunding;

import java.util.List;

public class CrowdfundingAdapter extends RecyclerView.Adapter<CrowdfundingAdapter.ViewHolder> {
    private Context context;
    private List<Crowdfunding> crowdfundingList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Crowdfunding cf);
        void onSupportClick(Crowdfunding cf);
    }

    public CrowdfundingAdapter(Context context, List<Crowdfunding> crowdfundingList, OnItemClickListener listener) {
        this.context = context;
        this.crowdfundingList = crowdfundingList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_crowdfunding, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Crowdfunding cf = crowdfundingList.get(position);
        holder.tvName.setText(cf.getName());
        holder.tvDesc.setText(cf.getDescription() != null ? cf.getDescription() : "");
        holder.tvTargetAmount.setText("¥" + formatAmount(cf.getTargetAmount()));
        holder.tvCurrentAmount.setText("¥" + formatAmount(cf.getCurrentAmount()));
        holder.tvProgress.setText(String.format("%.0f%%", cf.getProgress()));
        holder.tvSupporters.setText(cf.getSupporterCount() + " 位支持者");

        // 加载自定义横幅图或使用默认背景
        String bannerPath = cf.getBannerPath();
        if (bannerPath != null && !bannerPath.isEmpty()) {
            try {
                holder.ivBanner.setImageBitmap(BitmapFactory.decodeFile(bannerPath));
            } catch (Exception e) {
                holder.ivBanner.setImageBitmap(null);
            }
        } else {
            holder.ivBanner.setImageBitmap(null);
        }

        int totalWidth = context.getResources().getDisplayMetrics().widthPixels - 48;
        double progress = cf.getProgress();
        int barWidth = (int) (totalWidth * progress / 100.0);
        if (barWidth < 4) barWidth = 4;
        if (barWidth > totalWidth) barWidth = totalWidth;
        holder.viewProgressBar.getLayoutParams().width = barWidth;
        holder.viewProgressBar.requestLayout();

        holder.itemView.setOnClickListener(v -> {
            if (listener != null && position >= 0 && position < crowdfundingList.size()) {
                listener.onItemClick(crowdfundingList.get(position));
            }
        });

        holder.btnSupport.setOnClickListener(v -> {
            if (listener != null && position >= 0 && position < crowdfundingList.size()) {
                listener.onSupportClick(crowdfundingList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return crowdfundingList.size();
    }

    private String formatAmount(double amount) {
        if (amount >= 10000) {
            return String.format("%.1f万", amount / 10000);
        } else if (amount == (long) amount) {
            return String.format("%d", (long) amount);
        } else {
            return String.format("%.2f", amount);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc, tvTargetAmount, tvCurrentAmount, tvSupporters, tvProgress;
        View viewProgressBar, btnSupport;
        ImageView ivBanner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvProjectName);
            tvDesc = itemView.findViewById(R.id.tvProjectDesc);
            tvTargetAmount = itemView.findViewById(R.id.tvTargetAmount);
            tvCurrentAmount = itemView.findViewById(R.id.tvCurrentAmount);
            tvSupporters = itemView.findViewById(R.id.tvSupporters);
            tvProgress = itemView.findViewById(R.id.tvProgress);
            viewProgressBar = itemView.findViewById(R.id.viewProgressBar);
            btnSupport = itemView.findViewById(R.id.btnSupport);
            ivBanner = itemView.findViewById(R.id.ivBanner);
        }
    }
}
