package com.example.indiegamecommunity.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.indiegamecommunity.R;
import com.example.indiegamecommunity.adapter.CommentAdapter;
import com.example.indiegamecommunity.database.DatabaseHelper;
import com.example.indiegamecommunity.database.UserDatabaseHelper;
import com.example.indiegamecommunity.database.UserProfileDatabaseHelper;
import com.example.indiegamecommunity.model.Comment;
import com.example.indiegamecommunity.network.*;
import com.example.indiegamecommunity.utils.SharedPreferencesHelper;

import java.util.List;

public class ProjectDetailActivity extends AppCompatActivity {
    private static final int REQUEST_PAYMENT = 2004;
    private TextView tvProjectName, tvProjectDesc, tvCurrentAmount, tvTargetAmount, tvProgress, tvSupporters;
    private TextView tvCommentHeader, tvNoComments;
    private View viewProgressBar;
    private LinearLayout layoutProjectImages;
    private RecyclerView rvComments;
    private EditText etComment;
    private Button btnSend;
    private ImageView ivCommentAvatar;
    private DatabaseHelper databaseHelper;
    private UserDatabaseHelper userDbHelper;
    private UserProfileDatabaseHelper userProfileDatabaseHelper;
    private CommentAdapter commentAdapter;
    private int projectId;
    private String username;
    private CFDTo currentProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);

        tvProjectName = findViewById(R.id.tvProjectName);
        tvProjectDesc = findViewById(R.id.tvProjectDesc);
        tvCurrentAmount = findViewById(R.id.tvCurrentAmount);
        tvTargetAmount = findViewById(R.id.tvTargetAmount);
        tvProgress = findViewById(R.id.tvProgress);
        tvSupporters = findViewById(R.id.tvSupporters);
        tvCommentHeader = findViewById(R.id.tvCommentHeader);
        tvNoComments = findViewById(R.id.tvNoComments);
        viewProgressBar = findViewById(R.id.viewProgressBar);
        layoutProjectImages = findViewById(R.id.layoutProjectImages);
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

        projectId = getIntent().getIntExtra("project_id", -1);

        if (projectId != -1) {
            loadProjectData();
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
            shareIntent.putExtra(Intent.EXTRA_TEXT, "支持一个独立游戏项目：" + tvProjectName.getText());
            startActivity(Intent.createChooser(shareIntent, "分享"));
        });
        findViewById(R.id.layoutSupport).setOnClickListener(v -> {
            if (projectId == -1 || currentProject == null) return;
            if (userProfileDatabaseHelper.isSupported(projectId)) {
                Toast.makeText(this, "您已经支持过了", Toast.LENGTH_SHORT).show();
                return;
            }
            showSupportDialog(currentProject);
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

    private void loadProjectData() {
        ApiHelper.execute(() -> {
            retrofit2.Response<ApiResponse<CFDTo>> resp = ApiClient.getApiService().getCFById(projectId).execute();
            if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200) return resp.body().getData();
            return null;
        }, new ApiHelper.Callback<CFDTo>() {
            @Override
            public void onSuccess(CFDTo cf) {
                if (cf == null) return;
                currentProject = cf;
                tvProjectName.setText(cf.getName());
                tvProjectDesc.setText(cf.getDescription());
                tvCurrentAmount.setText("¥" + String.format("%.0f", cf.getCurrentAmount()));
                tvTargetAmount.setText("目标 ¥" + String.format("%.0f", cf.getTargetAmount()));

                ImageView ivGameIcon = findViewById(R.id.ivGameIcon);
                String iconPath = cf.getCoverImage();
                if (iconPath != null && !iconPath.isEmpty()) {
                    try {
                        java.io.File f = new java.io.File(iconPath);
                        if (f.exists()) ivGameIcon.setImageBitmap(BitmapFactory.decodeFile(iconPath));
                    } catch (Exception ignored) {}
                }
                double progress = cf.getTargetAmount() > 0 ? (cf.getCurrentAmount() / cf.getTargetAmount() * 100) : 0;
                tvProgress.setText(String.format("%.0f%%", progress));
                tvSupporters.setText(cf.getSupporterCount() + " 人支持");

                int width = (int) (getResources().getDisplayMetrics().widthPixels * progress / 100);
                viewProgressBar.getLayoutParams().width = Math.max(width, 4);
                viewProgressBar.requestLayout();

                String contentImgs = cf.getContentImages();
                if (contentImgs != null && !contentImgs.isEmpty()) {
                    layoutProjectImages.removeAllViews();
                    layoutProjectImages.setVisibility(View.VISIBLE);
                    for (String path : contentImgs.split(",")) {
                        if (path.isEmpty()) continue;
                        try {
                            Bitmap bmp = BitmapFactory.decodeFile(path);
                            if (bmp != null) {
                                ImageView iv = new ImageView(ProjectDetailActivity.this);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(220));
                                params.setMargins(0, 0, 0, dpToPx(8));
                                iv.setLayoutParams(params);
                                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                iv.setImageBitmap(bmp);
                                layoutProjectImages.addView(iv);
                            }
                        } catch (Exception ignored) {}
                    }
                }
            }
            @Override public void onError(String msg) { Toast.makeText(ProjectDetailActivity.this, "加载失败", Toast.LENGTH_SHORT).show(); }
        });
    }

    private void loadComments() {
        List<Comment> comments = databaseHelper.getCommentsByTarget("crowdfunding", projectId);
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
        Comment comment = new Comment(projectId, username, content);
        comment.setTargetType("crowdfunding");
        databaseHelper.insertComment(comment);
        etComment.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etComment.getWindowToken(), 0);
        loadComments();
        Toast.makeText(this, "评论成功", Toast.LENGTH_SHORT).show();
    }

    private void showSupportDialog(CFDTo cf) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("支持 " + cf.getName());
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_support, null);
        builder.setView(dialogView);
        EditText etAmount = dialogView.findViewById(R.id.etSupportAmount);
        builder.setPositiveButton("下一步", (dialog, which) -> {
            String amountStr = etAmount.getText().toString().trim();
            if (amountStr.isEmpty()) { Toast.makeText(this, "请输入支持金额", Toast.LENGTH_SHORT).show(); return; }
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) { Toast.makeText(this, "金额必须大于0", Toast.LENGTH_SHORT).show(); return; }
            showPaymentSelectDialog(cf, amount);
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void showPaymentSelectDialog(CFDTo cf, double amount) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认支持 ¥" + formatAmount(amount));
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_payment_select, null);
        builder.setView(dialogView);
        RadioButton rbWechat = dialogView.findViewById(R.id.rbWechat);
        RadioButton rbAlipay = dialogView.findViewById(R.id.rbAlipay);
        LinearLayout layoutWechat = dialogView.findViewById(R.id.layoutWechat);
        LinearLayout layoutAlipay = dialogView.findViewById(R.id.layoutAlipay);
        layoutWechat.setOnClickListener(v -> { rbWechat.setChecked(true); rbAlipay.setChecked(false); });
        layoutAlipay.setOnClickListener(v -> { rbAlipay.setChecked(true); rbWechat.setChecked(false); });
        builder.setPositiveButton("前往支付", (dialog, which) -> {
            pendingCF = cf; pendingAmount = amount;
            String method = rbAlipay.isChecked() ? "alipay" : "wechat";
            Intent intent = new Intent(this, PaymentActivity.class);
            intent.putExtra(PaymentActivity.EXTRA_PROJECT_NAME, cf.getName());
            intent.putExtra(PaymentActivity.EXTRA_AMOUNT, amount);
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT_METHOD, method);
            startActivityForResult(intent, REQUEST_PAYMENT);
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PAYMENT && resultCode == RESULT_OK && pendingCF != null) {
            ApiHelper.execute(() -> {
                retrofit2.Response<ApiResponse<CFDTo>> resp = ApiClient.getApiService().supportCF(pendingCF.getId(), new SupportBody(pendingAmount)).execute();
                if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200) return resp.body().getData();
                throw new Exception("支持失败");
            }, new ApiHelper.Callback<CFDTo>() {
                @Override
                public void onSuccess(CFDTo data) {
                    userProfileDatabaseHelper.addSupport((int) pendingCF.getId());
                    currentProject = data;
                    pendingCF = null;
                    updateUIFromProject(data);
                }
                @Override public void onError(String msg) { Toast.makeText(ProjectDetailActivity.this, msg, Toast.LENGTH_SHORT).show(); }
            });
        }
    }

    private void updateUIFromProject(CFDTo cf) {
        tvCurrentAmount.setText("¥" + String.format("%.0f", cf.getCurrentAmount()));
        double progress = cf.getTargetAmount() > 0 ? (cf.getCurrentAmount() / cf.getTargetAmount() * 100) : 0;
        tvProgress.setText(String.format("%.0f%%", progress));
        tvSupporters.setText(cf.getSupporterCount() + " 人支持");
        int width = (int) (getResources().getDisplayMetrics().widthPixels * progress / 100);
        viewProgressBar.getLayoutParams().width = Math.max(width, 4);
        viewProgressBar.requestLayout();
    }

    private CFDTo pendingCF;
    private double pendingAmount;

    private String formatAmount(double amount) {
        if (amount >= 10000) return String.format("%.1f万", amount / 10000);
        else if (amount == (long) amount) return String.format("%d", (long) amount);
        else return String.format("%.2f", amount);
    }

    private int dpToPx(int dp) { return (int) (dp * getResources().getDisplayMetrics().density + 0.5f); }
}
