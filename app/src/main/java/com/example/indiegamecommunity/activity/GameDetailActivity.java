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
import com.example.indiegamecommunity.model.Comment;
import com.example.indiegamecommunity.network.*;
import com.example.indiegamecommunity.utils.SharedPreferencesHelper;

import java.util.List;

public class GameDetailActivity extends AppCompatActivity {
    private TextView tvGameName, tvGameDesc, tvDownloadUrl, tvCommentHeader, tvNoComments;
    private LinearLayout layoutGameImages;
    private RecyclerView rvComments;
    private EditText etComment;
    private Button btnSend;
    private ImageView ivCommentAvatar;
    private DatabaseHelper databaseHelper;
    private UserDatabaseHelper userDbHelper;
    private CommentAdapter commentAdapter;
    private int promoId;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);

        tvGameName = findViewById(R.id.tvGameName);
        tvGameDesc = findViewById(R.id.tvGameDesc);
        tvDownloadUrl = findViewById(R.id.tvDownloadUrl);
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
        userDbHelper = new UserDatabaseHelper(this);
        loadAvatar(ivCommentAvatar, username);

        promoId = getIntent().getIntExtra("promo_id", -1);

        if (promoId != -1) {
            loadGameData();
        }

        rvComments.setLayoutManager(new LinearLayoutManager(this));
        loadComments();

        btnSend.setOnClickListener(v -> postComment());
        etComment.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEND) {
                postComment(); return true;
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
            shareIntent.putExtra(Intent.EXTRA_TEXT, "分享一款独立游戏：" + tvGameName.getText());
            startActivity(Intent.createChooser(shareIntent, "分享"));
        });
        findViewById(R.id.layoutVisit).setOnClickListener(v -> {
            String url = tvDownloadUrl.getText().toString();
            if (url.isEmpty()) { Toast.makeText(this, "暂无链接", Toast.LENGTH_SHORT).show(); return; }
            if (!url.startsWith("http://") && !url.startsWith("https://")) url = "https://" + url;
            try { startActivity(new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))); }
            catch (Exception e) { Toast.makeText(this, "无法打开链接", Toast.LENGTH_SHORT).show(); }
        });
    }

    private void loadAvatar(ImageView target, String user) {
        String avatarB64 = userDbHelper.getAvatar(user);
        if (avatarB64 != null && !avatarB64.isEmpty()) {
            try {
                byte[] bytes = android.util.Base64.decode(avatarB64, android.util.Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bmp != null) { target.setImageBitmap(bmp); return; }
            } catch (Exception ignored) {}
        }
        target.setImageResource(R.drawable.ic_default_avatar);
    }

    private void loadGameData() {
        ApiHelper.execute(() -> {
            retrofit2.Response<ApiResponse<PromoDto>> resp = ApiClient.getApiService().getPromoById(promoId).execute();
            if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200) return resp.body().getData();
            return null;
        }, new ApiHelper.Callback<PromoDto>() {
            @Override
            public void onSuccess(PromoDto promo) {
                if (promo == null) return;
                tvGameName.setText(promo.getName());
                tvGameDesc.setText(promo.getDescription());
                tvDownloadUrl.setText(promo.getCoverImage() != null ? promo.getCoverImage() : "");

                ImageView ivGameIcon = findViewById(R.id.ivGameIcon);
                String iconPath = promo.getCoverImage();
                if (iconPath != null && !iconPath.isEmpty()) {
                    try {
                        java.io.File f = new java.io.File(iconPath);
                        if (f.exists()) ivGameIcon.setImageBitmap(BitmapFactory.decodeFile(iconPath));
                    } catch (Exception ignored) {}
                }
                String contentImgs = promo.getContentImages();
                if (contentImgs != null && !contentImgs.isEmpty()) {
                    layoutGameImages.removeAllViews();
                    layoutGameImages.setVisibility(View.VISIBLE);
                    for (String path : contentImgs.split(",")) {
                        if (path.isEmpty()) continue;
                        try {
                            Bitmap bmp = BitmapFactory.decodeFile(path);
                            if (bmp != null) {
                                ImageView iv = new ImageView(GameDetailActivity.this);
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
            @Override public void onError(String msg) { Toast.makeText(GameDetailActivity.this, "加载失败", Toast.LENGTH_SHORT).show(); }
        });
    }

    private void loadComments() {
        List<Comment> comments = databaseHelper.getCommentsByTarget("promo", promoId);
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
        if (TextUtils.isEmpty(content)) { Toast.makeText(this, "请输入评论内容", Toast.LENGTH_SHORT).show(); return; }
        Comment comment = new Comment(promoId, username, content);
        comment.setTargetType("promo");
        databaseHelper.insertComment(comment);
        etComment.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etComment.getWindowToken(), 0);
        loadComments();
        Toast.makeText(this, "评论成功", Toast.LENGTH_SHORT).show();
    }

    private int dpToPx(int dp) { return (int) (dp * getResources().getDisplayMetrics().density + 0.5f); }
}
