package com.example.indiegamecommunity.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.indiegamecommunity.R;
import com.example.indiegamecommunity.network.ApiClient;
import com.example.indiegamecommunity.network.ApiHelper;
import com.example.indiegamecommunity.network.PostDto;
import com.example.indiegamecommunity.network.PostUpdateBody;
import com.example.indiegamecommunity.utils.SharedPreferencesHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostEditActivity extends AppCompatActivity {
    private EditText etTitle, etContent;
    private Spinner spCategory;
    private Button btnSubmit, btnCancel, btnBack, btnSelectCover, btnAddContentImage;
    private ImageView ivCoverPreview;
    private TextView tvCoverHint;
    private LinearLayout layoutImagePreviews;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private int postId = -1;
    private String coverImagePath = "";
    private String pendingCoverPath = "";
    private List<String> contentImagePaths = new ArrayList<>();

    private static final int PICK_COVER = 1001;
    private static final int PICK_CONTENT = 1002;

    private static final List<String> CATEGORIES = Arrays.asList(
            "游戏推荐", "开发日志", "游戏美术", "游戏音乐",
            "行业资讯", "活动赛事", "问答求助", "闲聊灌水"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_edit);

        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        spCategory = findViewById(R.id.spCategory);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnCancel = findViewById(R.id.btnCancel);
        btnBack = findViewById(R.id.btnBack);
        btnSelectCover = findViewById(R.id.btnSelectCover);
        btnAddContentImage = findViewById(R.id.btnAddContentImage);
        ivCoverPreview = findViewById(R.id.ivCoverPreview);
        tvCoverHint = findViewById(R.id.tvCoverHint);
        layoutImagePreviews = findViewById(R.id.layoutImagePreviews);

        sharedPreferencesHelper = new SharedPreferencesHelper(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, CATEGORIES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);

        String defaultCategory = getIntent().getStringExtra("category");
        if (defaultCategory != null) {
            int idx = CATEGORIES.indexOf(defaultCategory);
            if (idx >= 0) spCategory.setSelection(idx);
        }

        postId = getIntent().getIntExtra("post_id", -1);
        if (postId != -1) {
            // Load existing post from API
            ApiHelper.execute(() -> {
                retrofit2.Response<com.example.indiegamecommunity.network.ApiResponse<PostDto>> resp =
                    ApiClient.getApiService().getPostById(postId).execute();
                if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200) {
                    return resp.body().getData();
                }
                return null;
            }, new ApiHelper.Callback<PostDto>() {
                @Override
                public void onSuccess(PostDto post) {
                    if (post != null) {
                        etTitle.setText(post.getTitle());
                        etContent.setText(post.getContent());
                        int idx = CATEGORIES.indexOf(post.getCategory());
                        if (idx >= 0) spCategory.setSelection(idx);
                        if (post.getCoverImage() != null && !post.getCoverImage().isEmpty()) {
                            coverImagePath = post.getCoverImage();
                            loadCoverPreview();
                        }
                        String contentImgs = post.getContentImages();
                        if (contentImgs != null && !contentImgs.isEmpty()) {
                            for (String p : contentImgs.split(",")) {
                                if (!p.isEmpty()) contentImagePaths.add(p);
                            }
                            refreshContentImagePreviews();
                        }
                    }
                }
                @Override
                public void onError(String message) {
                    Toast.makeText(PostEditActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                }
            });
        }

        btnSelectCover.setOnClickListener(v -> startActivityForResult(
            new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), PICK_COVER));

        btnAddContentImage.setOnClickListener(v -> startActivityForResult(
            new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), PICK_CONTENT));

        btnSubmit.setOnClickListener(v -> submitPost());
        btnCancel.setOnClickListener(v -> finish());
        btnBack.setOnClickListener(v -> finish());
    }

    private void submitPost() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();
        String category = (String) spCategory.getSelectedItem();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "请填写标题和内容", Toast.LENGTH_SHORT).show();
            return;
        }

        String username = sharedPreferencesHelper.getUsername();

        if (postId != -1) {
            // Update existing post
            ApiHelper.execute(() -> {
                retrofit2.Response<com.example.indiegamecommunity.network.ApiResponse<PostDto>> resp =
                    ApiClient.getApiService().updatePost(postId,
                        new PostUpdateBody(title, content, category)).execute();
                if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200) return "ok";
                throw new Exception("编辑失败");
            }, new ApiHelper.Callback<String>() {
                @Override
                public void onSuccess(String data) {
                    Toast.makeText(PostEditActivity.this, "修改已提交，等待管理员审核后生效", Toast.LENGTH_SHORT).show();
                    finish();
                }
                @Override
                public void onError(String message) {
                    Toast.makeText(PostEditActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Create new post
            PostDto dto = new PostDto();
            dto.setTitle(title);
            dto.setContent(content);
            dto.setCategory(category);
            dto.setUsername(username);
            dto.setCoverImage(coverImagePath);
            dto.setContentImages(TextUtils.join(",", contentImagePaths));
            dto.setStatus("pending");

            ApiHelper.execute(() -> {
                retrofit2.Response<com.example.indiegamecommunity.network.ApiResponse<PostDto>> resp =
                    ApiClient.getApiService().createPost(dto).execute();
                if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200) return "ok";
                throw new Exception("发布失败");
            }, new ApiHelper.Callback<String>() {
                @Override
                public void onSuccess(String data) {
                    Toast.makeText(PostEditActivity.this, "发布成功！帖子将经过管理员审核后显示。", Toast.LENGTH_SHORT).show();
                    finish();
                }
                @Override
                public void onError(String message) {
                    Toast.makeText(PostEditActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadCoverPreview() {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(coverImagePath);
            if (bitmap != null) {
                ivCoverPreview.setImageBitmap(bitmap);
                ivCoverPreview.setVisibility(View.VISIBLE);
                tvCoverHint.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            ivCoverPreview.setVisibility(View.GONE);
            tvCoverHint.setVisibility(View.VISIBLE);
        }
    }

    private void refreshContentImagePreviews() {
        layoutImagePreviews.removeAllViews();
        for (String path : contentImagePaths) {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                if (bitmap != null) {
                    ImageView iv = new ImageView(this);
                    int size = dpToPx(90);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
                    params.setMargins(0, 0, dpToPx(8), 0);
                    iv.setLayoutParams(params);
                    iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    iv.setImageBitmap(bitmap);
                    layoutImagePreviews.addView(iv);
                }
            } catch (Exception ignored) {}
        }
    }

    private int dpToPx(int dp) { return (int) (dp * getResources().getDisplayMetrics().density + 0.5f); }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) return;
        Uri uri = data.getData();
        if (uri == null) return;
        try {
            String path = saveImageToFile(uri);
            if (path == null) return;
            if (requestCode == PICK_COVER) {
                coverImagePath = path;
                loadCoverPreview();
                Toast.makeText(this, "封面已选择", Toast.LENGTH_SHORT).show();
            } else if (requestCode == PICK_CONTENT) {
                contentImagePaths.add(path);
                refreshContentImagePreviews();
                Toast.makeText(this, "图片已添加", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "图片选择失败", Toast.LENGTH_SHORT).show();
        }
    }

    private String saveImageToFile(Uri uri) throws Exception {
        InputStream input = getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input);
        input.close();
        File dir = new File(getFilesDir(), "post_images");
        dir.mkdirs();
        File file = new File(dir, "img_" + System.currentTimeMillis() + ".jpg");
        FileOutputStream out = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
        out.flush(); out.close();
        return file.getAbsolutePath();
    }
}
