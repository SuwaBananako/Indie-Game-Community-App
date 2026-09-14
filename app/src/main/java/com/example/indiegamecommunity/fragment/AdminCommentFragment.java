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
import com.example.indiegamecommunity.model.Comment;

import java.util.List;

public class AdminCommentFragment extends Fragment {
    private RecyclerView rvComments;
    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_comments, container, false);
        rvComments = view.findViewById(R.id.rvComments);
        databaseHelper = new DatabaseHelper(getContext());
        rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
        loadComments();
        return view;
    }

    private void loadComments() {
        List<Comment> comments = databaseHelper.getAllComments();
        rvComments.setAdapter(new RecyclerView.Adapter<ViewHolder>() {
            @NonNull @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_promo, parent, false);
                return new ViewHolder(v);
            }
            @Override
            public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
                Comment c = comments.get(pos);
                h.tvName.setText(c.getUsername() + " 的评论");
                String preview = c.getContent().length() > 30 ? c.getContent().substring(0, 30) + "..." : c.getContent();
                h.tvDesc.setText("帖子ID: " + c.getPostId() + "  " + preview);
                h.itemView.setOnClickListener(v -> showCommentMenu(c));
            }
            @Override public int getItemCount() { return comments.size(); }
        });
    }

    private void showCommentMenu(Comment comment) {
        new AlertDialog.Builder(getContext())
                .setTitle("管理评论")
                .setMessage("用户: " + comment.getUsername() + "\n内容: " + comment.getContent())
                .setPositiveButton("删除评论", (dialog, which) -> {
                    databaseHelper.deleteComment(comment.getId());
                    loadComments();
                })
                .setNegativeButton("关闭", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadComments();
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
