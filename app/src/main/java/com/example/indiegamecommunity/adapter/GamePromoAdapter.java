package com.example.indiegamecommunity.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.indiegamecommunity.R;
import com.example.indiegamecommunity.model.GamePromo;

import java.util.List;

public class GamePromoAdapter extends RecyclerView.Adapter<GamePromoAdapter.ViewHolder> {
    private Context context;
    private List<GamePromo> gamePromoList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(GamePromo promo);
    }

    public GamePromoAdapter(Context context, List<GamePromo> gamePromoList, OnItemClickListener listener) {
        this.context = context;
        this.gamePromoList = gamePromoList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_game_promo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GamePromo promo = gamePromoList.get(position);
        holder.tvName.setText(promo.getName());
        holder.tvDescription.setText(promo.getDescription());
        holder.tvDownloadUrl.setText(promo.getDownloadUrl() != null ? promo.getDownloadUrl() : "");

        // 加载自定义横幅图或使用默认背景
        String bannerPath = promo.getBannerPath();
        if (bannerPath != null && !bannerPath.isEmpty()) {
            try {
                holder.ivBanner.setImageBitmap(BitmapFactory.decodeFile(bannerPath));
            } catch (Exception e) {
                holder.ivBanner.setImageBitmap(null);
            }
        } else {
            holder.ivBanner.setImageBitmap(null);
        }

        holder.btnVisit.setOnClickListener(v -> {
            String url = promo.getDownloadUrl();
            if (url == null || url.trim().isEmpty()) {
                Toast.makeText(context, "暂无下载链接", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "无法打开链接", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null && position >= 0 && position < gamePromoList.size()) {
                listener.onItemClick(gamePromoList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return gamePromoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription, tvDownloadUrl;
        Button btnVisit;
        ImageView ivBanner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvGameName);
            tvDescription = itemView.findViewById(R.id.tvGameDescription);
            tvDownloadUrl = itemView.findViewById(R.id.tvDownloadUrl);
            btnVisit = itemView.findViewById(R.id.btnVisit);
            ivBanner = itemView.findViewById(R.id.ivBanner);
        }
    }
}
