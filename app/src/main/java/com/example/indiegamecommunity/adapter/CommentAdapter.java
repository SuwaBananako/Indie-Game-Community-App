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
import com.example.indiegamecommunity.database.UserDatabaseHelper;
import com.example.indiegamecommunity.model.Comment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context context;
    private List<Comment> commentList;
    private UserDatabaseHelper userDbHelper;

    public CommentAdapter(Context context, List<Comment> commentList, UserDatabaseHelper userDbHelper) {
        this.context = context;
        this.commentList = commentList;
        this.userDbHelper = userDbHelper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.tvUsername.setText(comment.getUsername());
        holder.tvContent.setText(comment.getContent());

        // 加载用户头像
        loadAvatar(holder.ivAvatar, comment.getUsername());

        if (comment.getImagePath() != null && !comment.getImagePath().isEmpty()) {
            try {
                holder.ivCommentImage.setImageBitmap(
                        BitmapFactory.decodeFile(comment.getImagePath()));
                holder.ivCommentImage.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                holder.ivCommentImage.setVisibility(View.GONE);
            }
        } else {
            holder.ivCommentImage.setVisibility(View.GONE);
        }

        long now = System.currentTimeMillis();
        long diff = now - comment.getCreateTime();
        if (diff < 3600000) {
            long minutes = diff / 60000;
            holder.tvTime.setText(minutes <= 0 ? "刚刚" : minutes + "分钟前");
        } else if (diff < 86400000) {
            holder.tvTime.setText((diff / 3600000) + "小时前");
        } else if (diff < 172800000) {
            holder.tvTime.setText("昨天");
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
            holder.tvTime.setText(sdf.format(new Date(comment.getCreateTime())));
        }
    }

    private void loadAvatar(ImageView target, String username) {
        if (userDbHelper == null) {
            target.setImageResource(R.drawable.ic_default_avatar);
            return;
        }
        String avatarB64 = userDbHelper.getAvatar(username);
        if (avatarB64 != null && !avatarB64.isEmpty()) {
            try {
                byte[] bytes = android.util.Base64.decode(avatarB64, android.util.Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bmp != null) {
                    target.setImageBitmap(bmp);
                    return;
                }
            } catch (Exception ignored) {}
        }
        target.setImageResource(R.drawable.ic_default_avatar);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvContent, tvTime;
        ImageView ivCommentImage, ivAvatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvCommentUsername);
            tvContent = itemView.findViewById(R.id.tvCommentContent);
            tvTime = itemView.findViewById(R.id.tvCommentTime);
            ivCommentImage = itemView.findViewById(R.id.ivCommentImage);
            ivAvatar = itemView.findViewById(R.id.ivCommentAvatar);
        }
    }
}
