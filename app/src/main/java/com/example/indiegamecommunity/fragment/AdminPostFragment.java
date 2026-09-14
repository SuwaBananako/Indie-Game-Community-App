package com.example.indiegamecommunity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.indiegamecommunity.R;
import com.example.indiegamecommunity.database.DatabaseHelper;
import com.example.indiegamecommunity.model.Post;

import java.util.List;

public class AdminPostFragment extends Fragment {
    private RecyclerView rvPosts;
    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_posts, container, false);
        rvPosts = view.findViewById(R.id.rvPosts);
        databaseHelper = new DatabaseHelper(getContext());
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        loadPosts();
        return view;
    }

    private void loadPosts() {
        List<Post> posts = databaseHelper.getAllPosts();
        rvPosts.setAdapter(new RecyclerView.Adapter<ViewHolder>() {
            @NonNull @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_promo, parent, false);
                return new ViewHolder(v);
            }
            @Override
            public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
                Post p = posts.get(pos);
                h.tvName.setText(p.getTitle());
                h.tvDesc.setText("作者: " + p.getUsername() + "  " + p.getLikeCount() + "赞");
                h.itemView.setOnClickListener(v -> showPostMenu(p));
            }
            @Override public int getItemCount() { return posts.size(); }
        });
    }

    private void showPostMenu(Post post) {
        new AlertDialog.Builder(getContext())
                .setTitle("管理帖子")
                .setMessage("标题: " + post.getTitle() + "\n作者: " + post.getUsername())
                .setPositiveButton("删除帖子", (dialog, which) -> {
                    new AlertDialog.Builder(getContext())
                            .setTitle("确认删除")
                            .setMessage("删除帖子将同时删除所有相关评论")
                            .setPositiveButton("确认删除", (d, w) -> {
                                databaseHelper.deletePost(post.getId());
                                loadPosts();
                            })
                            .setNegativeButton("取消", null)
                            .show();
                })
                .setNegativeButton("关闭", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPosts();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc;
        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvGameName);
            tvDesc = v.findViewById(R.id.tvGameDescription);
        }
    }
}
