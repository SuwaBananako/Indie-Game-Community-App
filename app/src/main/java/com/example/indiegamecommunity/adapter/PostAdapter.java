package com.example.indiegamecommunity.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.indiegamecommunity.R;
import com.example.indiegamecommunity.model.Post;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private Context context;
    private List<Post> postList;
    private OnItemClickListener listener;

    private static final int[] FALLBACK_DRAWABLES = {
            R.drawable.banner1, R.drawable.banner2,
            R.drawable.banner3, R.drawable.banner4
    };

    public interface OnItemClickListener {
        void onItemClick(Post post);
        void onItemLongClick(Post post);
    }

    public PostAdapter(Context context, List<Post> postList, OnItemClickListener listener) {
        this.context = context;
        this.postList = postList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.tvTitle.setText(post.getTitle());
        holder.tvContent.setText(post.getContent());
        holder.tvUsername.setText(post.getUsername());
        holder.tvLikeCount.setText(formatCount(post.getLikeCount()) + "赞");

        if (post.getCategory() != null && !post.getCategory().isEmpty()) {
            holder.tvCategoryTag.setText(post.getCategory());
            holder.tvCategoryTag.setVisibility(View.VISIBLE);
        } else {
            holder.tvCategoryTag.setVisibility(View.GONE);
        }

        SimpleDateFormat sdf;
        long now = System.currentTimeMillis();
        long diff = now - post.getCreateTime();

        if (diff < 3600000) {
            long minutes = diff / 60000;
            holder.tvTime.setText(minutes <= 0 ? "刚刚" : minutes + "分钟前");
        } else if (diff < 86400000) {
            holder.tvTime.setText((diff / 3600000) + "小时前");
        } else if (diff < 172800000) {
            holder.tvTime.setText("昨天");
        } else {
            sdf = new SimpleDateFormat("MM-dd", Locale.getDefault());
            holder.tvTime.setText(sdf.format(new Date(post.getCreateTime())));
        }

        int drawableId = FALLBACK_DRAWABLES[position % FALLBACK_DRAWABLES.length];
        String coverPath = post.getCoverImage();
        if (coverPath != null && !coverPath.isEmpty()) {
            try {
                java.io.File imgFile = new java.io.File(coverPath);
                if (imgFile.exists()) {
                    Bitmap bmp = BitmapFactory.decodeFile(coverPath);
                    if (bmp != null) {
                        holder.ivThumbnail.setImageBitmap(bmp);
                    } else {
                        holder.ivThumbnail.setImageResource(drawableId);
                    }
                } else {
                    holder.ivThumbnail.setImageResource(drawableId);
                }
            } catch (Exception e) {
                holder.ivThumbnail.setImageResource(drawableId);
            }
        } else {
            holder.ivThumbnail.setImageResource(drawableId);
        }
    }

    private String formatCount(int count) {
        if (count >= 10000) {
            return String.format("%.1f万", count / 10000.0);
        }
        return String.valueOf(count);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvUsername, tvLikeCount, tvTime, tvCategoryTag;
        ImageView ivThumbnail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvCategoryTag = itemView.findViewById(R.id.tvCategoryTag);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onItemClick(postList.get(pos));
                }
            });
            itemView.setOnLongClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onItemLongClick(postList.get(pos));
                }
                return true;
            });
        }
    }
}
