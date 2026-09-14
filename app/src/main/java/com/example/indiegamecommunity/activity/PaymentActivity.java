package com.example.indiegamecommunity.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.indiegamecommunity.R;

public class PaymentActivity extends AppCompatActivity {
    public static final String EXTRA_PROJECT_NAME = "project_name";
    public static final String EXTRA_AMOUNT = "amount";
    public static final String EXTRA_PAYMENT_METHOD = "payment_method";

    private TextView tvPayTitle, tvPayAmount, tvProjectName, tvAmount, tvBigAmount;
    private Button btnBack, btnConfirmPay, btnCancelPay, btnSwitchWechat, btnSwitchAlipay;

    private String projectName;
    private double amount;
    private String paymentMethod;
    private String amountStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        tvPayTitle = findViewById(R.id.tvPayTitle);
        tvPayAmount = findViewById(R.id.tvPayAmount);
        tvProjectName = findViewById(R.id.tvProjectName);
        tvAmount = findViewById(R.id.tvAmount);
        tvBigAmount = findViewById(R.id.tvBigAmount);
        btnBack = findViewById(R.id.btnBack);
        btnConfirmPay = findViewById(R.id.btnConfirmPay);
        btnCancelPay = findViewById(R.id.btnCancelPay);
        btnSwitchWechat = findViewById(R.id.btnSwitchWechat);
        btnSwitchAlipay = findViewById(R.id.btnSwitchAlipay);

        projectName = getIntent().getStringExtra(EXTRA_PROJECT_NAME);
        if (projectName == null) projectName = "未知名项目";
        amount = getIntent().getDoubleExtra(EXTRA_AMOUNT, 0);
        paymentMethod = getIntent().getStringExtra(EXTRA_PAYMENT_METHOD);
        if (paymentMethod == null) paymentMethod = "wechat";

        amountStr = "¥" + String.format("%.2f", amount);
        updateUI();

        btnBack.setOnClickListener(v -> onBackPressed());
        btnCancelPay.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        btnSwitchWechat.setOnClickListener(v -> switchToWechat());
        btnSwitchAlipay.setOnClickListener(v -> switchToAlipay());

        btnConfirmPay.setOnClickListener(v -> launchPayment());
    }

    private void updateUI() {
        tvPayAmount.setText(amountStr);
        tvAmount.setText(amountStr);
        tvBigAmount.setText(amountStr);
        tvProjectName.setText(projectName);
        btnConfirmPay.setText("立即支付 " + amountStr);

        if ("wechat".equals(paymentMethod)) {
            tvPayTitle.setText("微信支付");
            btnSwitchAlipay.setVisibility(View.VISIBLE);
            btnSwitchWechat.setVisibility(View.GONE);
            btnConfirmPay.setText("立即支付 " + amountStr);
        } else {
            tvPayTitle.setText("支付宝");
            btnSwitchWechat.setVisibility(View.VISIBLE);
            btnSwitchAlipay.setVisibility(View.GONE);
            btnConfirmPay.setText("立即支付 " + amountStr);
        }
    }

    private void switchToWechat() {
        paymentMethod = "wechat";
        updateUI();
        Toast.makeText(this, "已切换到微信支付", Toast.LENGTH_SHORT).show();
    }

    private void switchToAlipay() {
        paymentMethod = "alipay";
        updateUI();
        Toast.makeText(this, "已切换到支付宝", Toast.LENGTH_SHORT).show();
    }

    private void launchPayment() {
        btnConfirmPay.setEnabled(false);
        btnConfirmPay.setText("正在跳转...");

        boolean launched;
        if ("wechat".equals(paymentMethod)) {
            launched = jumpToWechatPay();
        } else {
            launched = jumpToAlipay();
        }

        if (launched) {
            // 成功跳转到支付App，视为支付成功
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("支付已发起")
                    .setMessage("项目：" + projectName
                            + "\n金额：" + amountStr
                            + "\n支付方式：" + ("wechat".equals(paymentMethod) ? "微信支付" : "支付宝")
                            + "\n\n已跳转至" + ("wechat".equals(paymentMethod) ? "微信" : "支付宝") + "，请在App内完成支付。")
                    .setPositiveButton("已完成支付", (dialog, which) -> {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("payment_method", paymentMethod);
                        setResult(RESULT_OK, resultIntent);
                        Toast.makeText(this, "支付成功！感谢您的支持", Toast.LENGTH_LONG).show();
                        finish();
                    })
                    .setNegativeButton("支付遇到问题", (dialog, which) -> {
                        btnConfirmPay.setEnabled(true);
                        btnConfirmPay.setText("立即支付 " + amountStr);
                        Toast.makeText(this, "支付失败，请重试", Toast.LENGTH_LONG).show();
                    })
                    .setCancelable(false)
                    .show();
        } else {
            // 跳转失败，提示用户
            btnConfirmPay.setEnabled(true);
            btnConfirmPay.setText("立即支付 " + amountStr);
            String appName = "wechat".equals(paymentMethod) ? "微信" : "支付宝";
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("支付失败")
                    .setMessage("未检测到" + appName + "客户端，请安装" + appName + "后重试。")
                    .setPositiveButton("确定", null)
                    .show();
        }
    }

    private boolean jumpToWechatPay() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("weixin://"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean jumpToAlipay() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("alipays://platformapi/startapp"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        } catch (Exception e) {
            try {
                // 尝试支付宝网页版
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.alipay.com/"));
                startActivity(intent);
                return true;
            } catch (Exception e2) {
                return false;
            }
        }
    }

    @Override
    public void onBackPressed() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("退出支付")
                .setMessage("确定要退出支付吗？")
                .setPositiveButton("确定退出", (dialog, which) -> {
                    setResult(RESULT_CANCELED);
                    finish();
                })
                .setNegativeButton("继续支付", null)
                .show();
    }
}
