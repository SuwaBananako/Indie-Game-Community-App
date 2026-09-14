package com.example.indiegamecommunity.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.indiegamecommunity.R;
import com.example.indiegamecommunity.activity.PaymentActivity;
import com.example.indiegamecommunity.activity.ProjectDetailActivity;
import com.example.indiegamecommunity.adapter.CrowdfundingAdapter;
import com.example.indiegamecommunity.model.Crowdfunding;
import com.example.indiegamecommunity.network.ApiClient;
import com.example.indiegamecommunity.network.ApiHelper;
import com.example.indiegamecommunity.network.CFDTo;
import com.example.indiegamecommunity.network.DtoConverter;
import com.example.indiegamecommunity.network.SupportBody;
import com.example.indiegamecommunity.utils.SharedPreferencesHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CrowdfundingFragment extends Fragment {
    private static final int REQUEST_ICON = 2002;
    private static final int REQUEST_BANNER = 2004;

    private RecyclerView rvCrowdfundings;
    private Button btnLaunch;
    private CrowdfundingAdapter crowdfundingAdapter;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private ImageView dialogIconPreview, dialogBannerPreview;
    private String pendingIconPath = "", pendingBannerPath = "";
    private String gameIconDir;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crowdfunding, container, false);
        rvCrowdfundings = view.findViewById(R.id.rvCrowdfundings);
        btnLaunch = view.findViewById(R.id.btnLaunch);

        sharedPreferencesHelper = new SharedPreferencesHelper(getContext());
        gameIconDir = new File(getContext().getFilesDir(), "game_icons").getAbsolutePath();
        new File(gameIconDir).mkdirs();

        rvCrowdfundings.setLayoutManager(new LinearLayoutManager(getContext()));
        loadCrowdfundings();
        btnLaunch.setOnClickListener(v -> showLaunchDialog());
        return view;
    }

    private void loadCrowdfundings() {
        ApiHelper.execute(() -> {
            retrofit2.Response<com.example.indiegamecommunity.network.ApiResponse<List<CFDTo>>> resp =
                ApiClient.getApiService().getApprovedCFs().execute();
            if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200 && resp.body().getData() != null) {
                List<Crowdfunding> list = new ArrayList<>();
                for (CFDTo dto : resp.body().getData()) list.add(DtoConverter.toCrowdfunding(dto));
                return list;
            }
            return new ArrayList<Crowdfunding>();
        }, new ApiHelper.Callback<List<Crowdfunding>>() {
            @Override
            public void onSuccess(List<Crowdfunding> list) {
                crowdfundingAdapter = new CrowdfundingAdapter(getContext(), list, new CrowdfundingAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Crowdfunding cf) {
                        Intent intent = new Intent(getActivity(), ProjectDetailActivity.class);
                        intent.putExtra("project_id", cf.getId());
                        startActivity(intent);
                    }
                    @Override
                    public void onSupportClick(Crowdfunding cf) { showSupportDialog(cf); }
                });
                rvCrowdfundings.setAdapter(crowdfundingAdapter);
            }
            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "加载失败: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLaunchDialog() {
        pendingIconPath = ""; pendingBannerPath = "";
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("发起众筹");
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_launch_project, null);
        builder.setView(dialogView);

        dialogIconPreview = dialogView.findViewById(R.id.ivGameIcon);
        dialogBannerPreview = dialogView.findViewById(R.id.ivBannerPreview);
        Button btnSelectIcon = dialogView.findViewById(R.id.btnSelectIcon);
        Button btnSelectBanner = dialogView.findViewById(R.id.btnSelectBanner);
        EditText etName = dialogView.findViewById(R.id.etProjectName);
        EditText etDesc = dialogView.findViewById(R.id.etProjectDesc);
        EditText etAmount = dialogView.findViewById(R.id.etTargetAmount);

        btnSelectIcon.setOnClickListener(v -> startActivityForResult(
            new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), REQUEST_ICON));
        btnSelectBanner.setOnClickListener(v -> startActivityForResult(
            new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), REQUEST_BANNER));

        builder.setPositiveButton("发起", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();
            if (name.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(getContext(), "请填写项目名称和目标金额", Toast.LENGTH_SHORT).show(); return;
            }
            double amount = Double.parseDouble(amountStr);

            CFDTo dto = new CFDTo();
            dto.setName(name);
            dto.setDescription(desc);
            dto.setCoverImage(pendingBannerPath);
            dto.setContentImages(pendingIconPath);
            dto.setAuthorUsername(sharedPreferencesHelper.getUsername());
            dto.setTargetAmount(amount);

            ApiHelper.execute(() -> {
                retrofit2.Response<com.example.indiegamecommunity.network.ApiResponse<CFDTo>> resp =
                    ApiClient.getApiService().createCF(dto).execute();
                if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200) return "ok";
                throw new Exception("发起失败");
            }, new ApiHelper.Callback<String>() {
                @Override
                public void onSuccess(String data) {
                    Toast.makeText(getContext(), "发起成功！项目将经过管理员审核后显示。", Toast.LENGTH_SHORT).show();
                    loadCrowdfundings();
                }
                @Override
                public void onError(String message) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void showSupportDialog(Crowdfunding cf) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("支持 " + cf.getName());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_support, null);
        builder.setView(dialogView);
        EditText etAmount = dialogView.findViewById(R.id.etSupportAmount);

        builder.setPositiveButton("下一步", (dialog, which) -> {
            String amountStr = etAmount.getText().toString().trim();
            if (amountStr.isEmpty()) { Toast.makeText(getContext(), "请输入支持金额", Toast.LENGTH_SHORT).show(); return; }
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) { Toast.makeText(getContext(), "金额必须大于0", Toast.LENGTH_SHORT).show(); return; }
            showPaymentSelectDialog(cf, amount);
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void showPaymentSelectDialog(Crowdfunding cf, double amount) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("选择支付方式");
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_payment_select, null);
        builder.setView(dialogView);
        RadioButton rbWechat = dialogView.findViewById(R.id.rbWechat);
        RadioButton rbAlipay = dialogView.findViewById(R.id.rbAlipay);
        LinearLayout layoutWechat = dialogView.findViewById(R.id.layoutWechat);
        LinearLayout layoutAlipay = dialogView.findViewById(R.id.layoutAlipay);
        layoutWechat.setOnClickListener(v -> { rbWechat.setChecked(true); rbAlipay.setChecked(false); });
        layoutAlipay.setOnClickListener(v -> { rbAlipay.setChecked(true); rbWechat.setChecked(false); });

        builder.setPositiveButton("前往支付", (dialog, which) -> {
            Intent intent = new Intent(getActivity(), PaymentActivity.class);
            intent.putExtra("amount", amount);
            intent.putExtra("payment_method", rbWechat.isChecked() ? "微信支付" : "支付宝");
            intent.putExtra("project_name", cf.getName());
            startActivity(intent);

            // Call API after payment (simulated)
            ApiHelper.execute(() -> {
                ApiClient.getApiService().supportCF(cf.getId(), new SupportBody(amount)).execute();
                return "ok";
            }, new ApiHelper.Callback<String>() {
                @Override
                public void onSuccess(String data) { loadCrowdfundings(); }
                @Override
                public void onError(String message) {}
            });
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null || resultCode != getActivity().RESULT_OK) return;
        Uri uri = data.getData();
        if (uri == null) return;
        try {
            String path = saveIconToFile(uri);
            if (path == null) return;
            if (requestCode == REQUEST_ICON) {
                pendingIconPath = path;
                if (dialogIconPreview != null) dialogIconPreview.setImageBitmap(BitmapFactory.decodeFile(path));
                Toast.makeText(getContext(), "图标已选择", Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_BANNER) {
                pendingBannerPath = path;
                if (dialogBannerPreview != null) dialogBannerPreview.setImageBitmap(BitmapFactory.decodeFile(path));
                Toast.makeText(getContext(), "横幅背景已选择", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "图片选取失败", Toast.LENGTH_SHORT).show();
        }
    }

    private String saveIconToFile(Uri uri) throws Exception {
        InputStream input = getContext().getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input); input.close();
        File file = new File(gameIconDir, "icon_" + System.currentTimeMillis() + ".png");
        FileOutputStream out = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, out); out.flush(); out.close();
        return file.getAbsolutePath();
    }

    @Override
    public void onResume() { super.onResume(); loadCrowdfundings(); }
}
