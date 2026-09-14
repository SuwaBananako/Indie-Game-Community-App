package com.example.indiegamecommunity.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.indiegamecommunity.R;
import com.example.indiegamecommunity.adapter.CommentAdapter;
import com.example.indiegamecommunity.database.UserDatabaseHelper;
import com.example.indiegamecommunity.database.UserProfileDatabaseHelper;
import com.example.indiegamecommunity.model.Comment;
import com.example.indiegamecommunity.network.*;
import com.example.indiegamecommunity.utils.SharedPreferencesHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {
    private TextView tvTitle, tvUsername, tvContent, tvLikeCount, tvCollectCount, tvTime, tvCategoryTag;
    private TextView tvLikeLabel, tvCollectLabel, tvCommentCount, tvCommentLabel, tvNoComments, tvCommentHeader;
    private ImageView ivLike, ivCollect, ivComment, ivCoverImage, ivPostAvatar, ivCommentAvatar;
    private LinearLayout layoutLike, layoutCollect, layoutComment, layoutShare, layoutPostImages;
    private Button btnBack, btnSend, btnAddImage;
    private EditText etComment;
    private ScrollView scrollView;
    private RecyclerView rvComments;
    private CommentAdapter commentAdapter;
    private UserProfileDatabaseHelper userProfileDatabaseHelper;
    private UserDatabaseHelper userDbHelper;
    private int postId;
    private String username;
    private String commentImagePath = "";

    private static final int PICK_COMMENT_IMAGE = 2001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        tvTitle = findViewById(R.id.tvTitle);
        tvUsername = findViewById(R.id.tvUsername);
        tvContent = findViewById(R.id.tvContent);
        tvLikeCount = findViewById(R.id.tvLikeCount);
        tvCollectCount = findViewById(R.id.tvCollectCount);
        tvTime = findViewById(R.id.tvTime);
        tvCategoryTag = findViewById(R.id.tvCategoryTag);
        tvLikeLabel = findViewById(R.id.tvLikeLabel);
        tvCollectLabel = findViewById(R.id.tvCollectLabel);
        tvCommentCount = findViewById(R.id.tvCommentCount);
        tvCommentLabel = findViewById(R.id.tvCommentLabel);
        tvNoComments = findViewById(R.id.tvNoComments);
        tvCommentHeader = findViewById(R.id.tvCommentHeader);
        ivLike = findViewById(R.id.ivLike);
        ivCollect = findViewById(R.id.ivCollect);
        ivComment = findViewById(R.id.ivComment);
        ivCoverImage = findViewById(R.id.ivCoverImage);
        ivPostAvatar = findViewById(R.id.ivPostAvatar);
        ivCommentAvatar = findViewById(R.id.ivCommentAvatar);
        layoutLike = findViewById(R.id.layoutLike);
        layoutCollect = findViewById(R.id.layoutCollect);
        layoutComment = findViewById(R.id.layoutComment);
        layoutShare = findViewById(R.id.layoutShare);
        layoutPostImages = findViewById(R.id.layoutPostImages);
        btnBack = findViewById(R.id.btnBack);
        btnSend = findViewById(R.id.btnSend);
        btnAddImage = findViewById(R.id.btnAddImage);
        etComment = findViewById(R.id.etComment);
        scrollView = findViewById(R.id.scrollView);
        rvComments = findViewById(R.id.rvComments);

        SharedPreferencesHelper sp = new SharedPreferencesHelper(this);
        username = sp.getUsername();
        userProfileDatabaseHelper = new UserProfileDatabaseHelper(this, username);
        userDbHelper = new UserDatabaseHelper(this);

        loadAvatar(ivCommentAvatar, username);

        postId = getIntent().getIntExtra("post_id", -1);
        if (postId != -1) {
            loadPost();
        }
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        loadComments();

        layoutLike.setOnClickListener(v -> handleLike());
        layoutCollect.setOnClickListener(v -> handleCollect());
        btnBack.setOnClickListener(v -> finish());
        layoutComment.setOnClickListener(v -> {
            etComment.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.showSoftInput(etComment, InputMethodManager.SHOW_IMPLICIT);
        });
        layoutShare.setOnClickListener(v -> sharePost());
        btnSend.setOnClickListener(v -> postComment());
        btnAddImage.setOnClickListener(v -> startActivityForResult(
            new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), PICK_COMMENT_IMAGE));

        etComment.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEND) {
                postComment(); return true;
            }
            return false;
        });
    }

    private void loadPost() {
        ApiHelper.execute(() -> {
            retrofit2.Response<ApiResponse<PostDto>> resp = ApiClient.getApiService().getPostById(postId).execute();
            if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200) {
                return resp.body().getData();
            }
            return null;
        }, new ApiHelper.Callback<PostDto>() {
            @Override
            public void onSuccess(PostDto post) {
                if (post == null) { Toast.makeText(PostDetailActivity.this, "帖子不存在", Toast.LENGTH_SHORT).show(); return; }
                tvTitle.setText(post.getTitle());
                tvUsername.setText(post.getUsername());
                tvContent.setText(post.getContent());
                tvLikeCount.setText(String.valueOf(post.getLikeCount()));
                tvCollectCount.setText(String.valueOf(post.getCollectCount()));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                tvTime.setText(sdf.format(new Date(post.getCreateTime())));
                if (post.getCategory() != null && !post.getCategory().isEmpty()) {
                    tvCategoryTag.setText(post.getCategory());
                    tvCategoryTag.setVisibility(View.VISIBLE);
                }
                loadAvatar(ivPostAvatar, post.getUsername());
                if (post.getCoverImage() != null && !post.getCoverImage().isEmpty()) {
                    try {
                        Bitmap bmp = BitmapFactory.decodeFile(post.getCoverImage());
                        if (bmp != null) { ivCoverImage.setImageBitmap(bmp); ivCoverImage.setVisibility(View.VISIBLE); }
                    } catch (Exception ignored) {}
                }
                String contentImgs = post.getContentImages();
                if (contentImgs != null && !contentImgs.isEmpty()) {
                    layoutPostImages.removeAllViews();
                    layoutPostImages.setVisibility(View.VISIBLE);
                    for (String path : contentImgs.split(",")) {
                        if (!path.isEmpty()) {
                            try {
                                Bitmap bmp = BitmapFactory.decodeFile(path);
                                if (bmp != null) {
                                    ImageView iv = new ImageView(PostDetailActivity.this);
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(220));
                                    params.setMargins(0, 0, 0, dpToPx(8));
                                    iv.setLayoutParams(params);
                                    iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    iv.setImageBitmap(bmp);
                                    iv.setClickable(true); iv.setFocusable(true);
                                    iv.setOnClickListener(v -> showImageFullScreen(path));
                                    layoutPostImages.addView(iv);
                                }
                            } catch (Exception ignored) {}
                        }
                    }
                }
                updateButtonStates();
            }
            @Override
            public void onError(String message) { Toast.makeText(PostDetailActivity.this, "加载失败", Toast.LENGTH_SHORT).show(); }
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

    private void showImageFullScreen(String imagePath) {
        try {
            Bitmap bmp = BitmapFactory.decodeFile(imagePath);
            if (bmp == null) { Toast.makeText(this, "图片加载失败", Toast.LENGTH_SHORT).show(); return; }
            ImageView fullImage = new ImageView(this);
            fullImage.setImageBitmap(bmp);
            fullImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            fullImage.setBackgroundColor(0xFF000000);
            new AlertDialog.Builder(this).setView(fullImage)
                .setPositiveButton("保存到相册", (dialog, which) -> saveImageToGallery(imagePath))
                .setNegativeButton("关闭", null).show();
        } catch (Exception e) { Toast.makeText(this, "图片加载失败", Toast.LENGTH_SHORT).show(); }
    }

    private void saveImageToGallery(String imagePath) {
        try {
            Bitmap bmp = BitmapFactory.decodeFile(imagePath);
            if (bmp == null) { Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show(); return; }
            String filename = "IMG_" + System.currentTimeMillis() + ".jpg";
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "IndieGameCommunity");
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, filename);
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 95, out);
            out.flush(); out.close();
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(file));
            sendBroadcast(mediaScanIntent);
            Toast.makeText(this, "已保存到 " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) { Toast.makeText(this, "保存失败: " + e.getMessage(), Toast.LENGTH_SHORT).show(); }
    }

    private void loadComments() {
        ApiHelper.execute(() -> {
            retrofit2.Response<ApiResponse<List<CommentDto>>> resp = ApiClient.getApiService().getComments(postId).execute();
            if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200 && resp.body().getData() != null) {
                List<Comment> list = new ArrayList<>();
                for (CommentDto dto : resp.body().getData()) list.add(DtoConverter.toComment(dto));
                return list;
            }
            return new ArrayList<Comment>();
        }, new ApiHelper.Callback<List<Comment>>() {
            @Override
            public void onSuccess(List<Comment> comments) {
                int count = comments.size();
                tvCommentCount.setText(String.valueOf(count));
                if (count > 0) {
                    tvCommentHeader.setText("全部评论 (" + count + ")");
                    tvNoComments.setVisibility(View.GONE);
                    rvComments.setVisibility(View.VISIBLE);
                    commentAdapter = new CommentAdapter(PostDetailActivity.this, comments, userDbHelper);
                    rvComments.setAdapter(commentAdapter);
                } else {
                    tvCommentHeader.setText("全部评论");
                    tvNoComments.setVisibility(View.VISIBLE);
                    rvComments.setVisibility(View.GONE);
                }
            }
            @Override
            public void onError(String message) {}
        });
    }

    private void postComment() {
        String content = etComment.getText().toString().trim();
        if (TextUtils.isEmpty(content) && commentImagePath.isEmpty()) {
            Toast.makeText(this, "请输入评论内容或添加图片", Toast.LENGTH_SHORT).show();
            return;
        }
        ApiHelper.execute(() -> {
            retrofit2.Response<ApiResponse<CommentDto>> resp = ApiClient.getApiService()
                .addComment(postId, new CommentBody(username, content, commentImagePath)).execute();
            if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200) return "ok";
            throw new Exception("评论失败");
        }, new ApiHelper.Callback<String>() {
            @Override
            public void onSuccess(String data) {
                etComment.setText("");
                commentImagePath = "";
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etComment.getWindowToken(), 0);
                loadComments();
                Toast.makeText(PostDetailActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(String message) {
                Toast.makeText(PostDetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sharePost() {
        ApiHelper.execute(() -> {
            retrofit2.Response<ApiResponse<PostDto>> resp = ApiClient.getApiService().getPostById(postId).execute();
            if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200) return resp.body().getData();
            return null;
        }, new ApiHelper.Callback<PostDto>() {
            @Override
            public void onSuccess(PostDto post) {
                if (post == null) return;
                String text = "【" + post.getTitle() + "】\n" + post.getContent() + "\n\n—— 来自独立游戏社区 by " + post.getUsername();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, post.getTitle());
                shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(Intent.createChooser(shareIntent, "分享到"));
            }
            @Override
            public void onError(String message) {}
        });
    }

    private void updateButtonStates() {
        int selectedColor = ContextCompat.getColor(this, R.color.acfun_red);
        if (userProfileDatabaseHelper.isLiked(postId)) {
            ivLike.setImageTintList(ColorStateList.valueOf(selectedColor));
            tvLikeLabel.setTextColor(selectedColor); tvLikeCount.setTextColor(selectedColor);
            layoutLike.setEnabled(false);
        }
        if (userProfileDatabaseHelper.isCollected(postId)) {
            ivCollect.setImageTintList(ColorStateList.valueOf(selectedColor));
            tvCollectLabel.setTextColor(selectedColor); tvCollectCount.setTextColor(selectedColor);
            layoutCollect.setEnabled(false);
        }
    }

    private void handleLike() {
        if (userProfileDatabaseHelper.isLiked(postId)) {
            Toast.makeText(this, "您已经点赞过了", Toast.LENGTH_SHORT).show(); return;
        }
        ApiHelper.execute(() -> {
            ApiClient.getApiService().likePost(postId).execute();
            return "ok";
        }, new ApiHelper.Callback<String>() {
            @Override
            public void onSuccess(String data) {
                userProfileDatabaseHelper.addLike(postId);
                ApiHelper.execute(() -> {
                    retrofit2.Response<ApiResponse<PostDto>> resp = ApiClient.getApiService().getPostById(postId).execute();
                    if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200) return resp.body().getData();
                    return null;
                }, new ApiHelper.Callback<PostDto>() {
                    @Override
                    public void onSuccess(PostDto post) {
                        if (post != null) tvLikeCount.setText(String.valueOf(post.getLikeCount()));
                    }
                    @Override
                    public void onError(String message) {}
                });
                int selectedColor = ContextCompat.getColor(PostDetailActivity.this, R.color.acfun_red);
                ivLike.setImageTintList(ColorStateList.valueOf(selectedColor));
                tvLikeLabel.setTextColor(selectedColor); tvLikeCount.setTextColor(selectedColor);
                layoutLike.setEnabled(false);
                Toast.makeText(PostDetailActivity.this, R.string.like_success, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(String message) { Toast.makeText(PostDetailActivity.this, "点赞失败", Toast.LENGTH_SHORT).show(); }
        });
    }

    private void handleCollect() {
        if (userProfileDatabaseHelper.isCollected(postId)) {
            Toast.makeText(this, "您已经收藏过了", Toast.LENGTH_SHORT).show(); return;
        }
        // Collect is still local for simplicity
        userProfileDatabaseHelper.addCollection(postId, tvTitle.getText().toString());
        Toast.makeText(this, R.string.collect_success, Toast.LENGTH_SHORT).show();
        int selectedColor = ContextCompat.getColor(this, R.color.acfun_red);
        ivCollect.setImageTintList(ColorStateList.valueOf(selectedColor));
        tvCollectLabel.setTextColor(selectedColor); tvCollectCount.setTextColor(selectedColor);
        layoutCollect.setEnabled(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_COMMENT_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                try {
                    String path = saveCommentImage(uri);
                    if (path != null) { commentImagePath = path; Toast.makeText(this, "图片已添加，点击发送即可", Toast.LENGTH_SHORT).show(); }
                } catch (Exception e) { Toast.makeText(this, "图片选取失败", Toast.LENGTH_SHORT).show(); }
            }
        }
    }

    private String saveCommentImage(Uri uri) throws Exception {
        InputStream input = getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input); input.close();
        File dir = new File(getFilesDir(), "comments_img");
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, "comment_" + System.currentTimeMillis() + ".jpg");
        FileOutputStream out = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, out);
        out.flush(); out.close();
        return file.getAbsolutePath();
    }

    private int dpToPx(int dp) { return (int) (dp * getResources().getDisplayMetrics().density + 0.5f); }
}
