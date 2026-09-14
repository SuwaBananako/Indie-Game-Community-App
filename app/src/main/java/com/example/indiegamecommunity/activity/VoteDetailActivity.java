package com.example.indiegamecommunity.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.indiegamecommunity.R;
import com.example.indiegamecommunity.adapter.CommentAdapter;
import com.example.indiegamecommunity.database.DatabaseHelper;
import com.example.indiegamecommunity.database.UserDatabaseHelper;
import com.example.indiegamecommunity.database.UserProfileDatabaseHelper;
import com.example.indiegamecommunity.model.Comment;
import com.example.indiegamecommunity.model.Vote;
import com.example.indiegamecommunity.network.*;
import com.example.indiegamecommunity.utils.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

public class VoteDetailActivity extends AppCompatActivity {
    private TextView tvGameName, tvGameDesc, tvVoteCount, tvRank, tvCommentHeader, tvNoComments;
    private LinearLayout layoutGameImages;
    private RecyclerView rvComments;
    private EditText etComment;
    private Button btnSend;
    private ImageView ivCommentAvatar;
    private DatabaseHelper databaseHelper;
    private UserDatabaseHelper userDbHelper;
    private UserProfileDatabaseHelper userProfileDatabaseHelper;
    private CommentAdapter commentAdapter;
    private int voteId;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_detail);

        tvRank = findViewById(R.id.tvRank);
        tvGameName = findViewById(R.id.tvGameName);
        tvGameDesc = findViewById(R.id.tvGameDesc);
        tvVoteCount = findViewById(R.id.tvVoteCount);
        tvCommentHeader = findViewById(R.id.tvCommentHeader);
        tvNoComments = findViewById(R.id.tvNoComments);
        layoutGameImages = findViewById(R.id.layoutGameImages);
        rvComments = findViewById(R.id.rvComments);
        etComment = findViewById(R.id.etComment);
        btnSend = findViewById(R.id.btnSend);
        ivCommentAvatar = findViewById(R.id.ivCommentAvatar);
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        databaseHelper = new DatabaseHelper(this);
        SharedPreferencesHelper spHelper = new SharedPreferencesHelper(this);
        username = spHelper.getUsername();
        userProfileDatabaseHelper = new UserProfileDatabaseHelper(this, username);
        userDbHelper = new UserDatabaseHelper(this);
        loadAvatar(ivCommentAvatar, username);

        voteId = getIntent().getIntExtra("vote_id", -1);
        int rank = getIntent().getIntExtra("rank", 0);

        if (voteId != -1) {
            loadVoteData(rank);
        }

        rvComments.setLayoutManager(new LinearLayoutManager(this));
        loadComments();

        btnSend.setOnClickListener(v -> postComment());
        etComment.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEND) {
                postComment();
                return true;
            }
            return false;
        });

        findViewById(R.id.layoutLike).setOnClickListener(v ->
                Toast.makeText(this, "已点赞", Toast.LENGTH_SHORT).show());
        findViewById(R.id.layoutCollect).setOnClickListener(v ->
                Toast.makeText(this, "已收藏", Toast.LENGTH_SHORT).show());
        findViewById(R.id.layoutShare).setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "为独立游戏投票：" + tvGameName.getText());
            startActivity(Intent.createChooser(shareIntent, "分享"));
        });
        findViewById(R.id.layoutVote).setOnClickListener(v -> {
            if (voteId == -1) return;
            if (userProfileDatabaseHelper.isVoted(voteId)) {
                Toast.makeText(this, "您已经投票过了", Toast.LENGTH_SHORT).show();
                return;
            }
            ApiHelper.execute(() -> {
                ApiClient.getApiService().doVote(voteId).execute();
                return "ok";
            }, new ApiHelper.Callback<String>() {
                @Override
                public void onSuccess(String data) {
                    userProfileDatabaseHelper.addVote(voteId);
                    Toast.makeText(VoteDetailActivity.this, "投票成功！", Toast.LENGTH_SHORT).show();
                    loadVoteData(0);
                }
                @Override
                public void onError(String message) {
                    Toast.makeText(VoteDetailActivity.this, "投票失败", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadAvatar(ImageView target, String user) {
        String avatarB64 = userDbHelper.getAvatar(user);
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

    private void loadVoteData(int rank) {
        ApiHelper.execute(() -> {
            retrofit2.Response<ApiResponse<VoteDto>> resp = ApiClient.getApiService().getVoteById(voteId).execute();
            if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200) return resp.body().getData();
            return null;
        }, new ApiHelper.Callback<VoteDto>() {
            @Override
            public void onSuccess(VoteDto vote) {
                if (vote == null) return;
                if (rank > 0) {
                    tvRank.setVisibility(View.VISIBLE);
                    tvRank.setText(String.valueOf(rank));
                } else {
                    tvRank.setVisibility(View.GONE);
                }
                tvGameName.setText(vote.getGameName());
                tvGameDesc.setText(vote.getDescription() != null ? vote.getDescription() : "暂无简介");
                tvVoteCount.setText("已获得 " + vote.getVoteCount() + " 票");

                ImageView ivGameIcon = findViewById(R.id.ivGameIcon);
                String iconPath = vote.getIconPath();
                if (iconPath != null && !iconPath.isEmpty()) {
                    try {
                        java.io.File f = new java.io.File(iconPath);
                        if (f.exists()) {
                            ivGameIcon.setImageBitmap(BitmapFactory.decodeFile(iconPath));
                        }
                    } catch (Exception ignored) {}
                }
                String contentImgs = vote.getContentImages();
                if (contentImgs != null && !contentImgs.isEmpty()) {
                    layoutGameImages.removeAllViews();
                    layoutGameImages.setVisibility(View.VISIBLE);
                    String[] paths = contentImgs.split(",");
                    for (String path : paths) {
                        if (path.isEmpty()) continue;
                        try {
                            Bitmap bmp = BitmapFactory.decodeFile(path);
                            if (bmp != null) {
                                ImageView iv = new ImageView(VoteDetailActivity.this);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(220));
                                params.setMargins(0, 0, 0, dpToPx(8));
                                iv.setLayoutParams(params);
                                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                iv.setImageBitmap(bmp);
                                layoutGameImages.addView(iv);
                            }
                        } catch (Exception ignored) {}
                    }
                }
            }
            @Override
            public void onError(String message) {
                Toast.makeText(VoteDetailActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadComments() {
        List<Comment> comments = databaseHelper.getCommentsByTarget("vote", voteId);
        int count = comments.size();
        if (count > 0) {
            tvCommentHeader.setText("全部评论 (" + count + ")");
            tvNoComments.setVisibility(View.GONE);
            rvComments.setVisibility(View.VISIBLE);
            commentAdapter = new CommentAdapter(this, comments, userDbHelper);
            rvComments.setAdapter(commentAdapter);
        } else {
            tvCommentHeader.setText("评论");
            tvNoComments.setVisibility(View.VISIBLE);
            rvComments.setVisibility(View.GONE);
        }
    }

    private void postComment() {
        String content = etComment.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入评论内容", Toast.LENGTH_SHORT).show();
            return;
        }
        Comment comment = new Comment(voteId, username, content);
        comment.setTargetType("vote");
        databaseHelper.insertComment(comment);
        etComment.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etComment.getWindowToken(), 0);
        loadComments();
        Toast.makeText(this, "评论成功", Toast.LENGTH_SHORT).show();
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }
}
