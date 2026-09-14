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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.indiegamecommunity.R;
import com.example.indiegamecommunity.activity.GameDetailActivity;
import com.example.indiegamecommunity.adapter.GamePromoAdapter;
import com.example.indiegamecommunity.model.GamePromo;
import com.example.indiegamecommunity.network.ApiClient;
import com.example.indiegamecommunity.network.ApiHelper;
import com.example.indiegamecommunity.network.DtoConverter;
import com.example.indiegamecommunity.network.PromoDto;
import com.example.indiegamecommunity.utils.SharedPreferencesHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GamePromoFragment extends Fragment {
    private static final int REQUEST_ICON = 2001;
    private static final int REQUEST_BANNER = 2002;

    private RecyclerView rvGamePromos;
    private Button btnPublish;
    private GamePromoAdapter gamePromoAdapter;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private ImageView dialogIconPreview, dialogBannerPreview;
    private String pendingIconPath = "", pendingBannerPath = "";
    private String gameIconDir;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_promo, container, false);
        rvGamePromos = view.findViewById(R.id.rvGamePromos);
        btnPublish = view.findViewById(R.id.btnPublish);

        sharedPreferencesHelper = new SharedPreferencesHelper(getContext());
        gameIconDir = new File(getContext().getFilesDir(), "game_icons").getAbsolutePath();
        new File(gameIconDir).mkdirs();

        rvGamePromos.setLayoutManager(new LinearLayoutManager(getContext()));
        loadGamePromos();

        btnPublish.setOnClickListener(v -> showPublishDialog());
        return view;
    }

    private void loadGamePromos() {
        ApiHelper.execute(() -> {
            retrofit2.Response<com.example.indiegamecommunity.network.ApiResponse<List<PromoDto>>> resp =
                ApiClient.getApiService().getApprovedPromos().execute();
            if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200 && resp.body().getData() != null) {
                List<GamePromo> list = new ArrayList<>();
                for (PromoDto dto : resp.body().getData()) list.add(DtoConverter.toGamePromo(dto));
                return list;
            }
            return new ArrayList<GamePromo>();
        }, new ApiHelper.Callback<List<GamePromo>>() {
            @Override
            public void onSuccess(List<GamePromo> list) {
                gamePromoAdapter = new GamePromoAdapter(getContext(), list, promo -> {
                    Intent intent = new Intent(getActivity(), GameDetailActivity.class);
                    intent.putExtra("promo_id", promo.getId());
                    startActivity(intent);
                });
                rvGamePromos.setAdapter(gamePromoAdapter);
            }
            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "加载失败: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPublishDialog() {
        pendingIconPath = "";
        pendingBannerPath = "";
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("发布游戏宣发");
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_publish_game, null);
        builder.setView(dialogView);

        EditText etName = dialogView.findViewById(R.id.etGameName);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        EditText etDownloadUrl = dialogView.findViewById(R.id.etDownloadUrl);
        dialogIconPreview = dialogView.findViewById(R.id.ivGameIcon);
        dialogBannerPreview = dialogView.findViewById(R.id.ivBannerPreview);
        Button btnSelectIcon = dialogView.findViewById(R.id.btnSelectIcon);
        Button btnSelectBanner = dialogView.findViewById(R.id.btnSelectBanner);

        btnSelectIcon.setOnClickListener(v -> startActivityForResult(
            new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), REQUEST_ICON));
        btnSelectBanner.setOnClickListener(v -> startActivityForResult(
            new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), REQUEST_BANNER));

        builder.setPositiveButton("发布", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            if (name.isEmpty()) { Toast.makeText(getContext(), "请输入游戏名称", Toast.LENGTH_SHORT).show(); return; }

            PromoDto dto = new PromoDto();
            dto.setName(name);
            dto.setDescription(description);
            dto.setCoverImage(pendingBannerPath);
            dto.setContentImages(pendingIconPath);
            dto.setAuthorUsername(sharedPreferencesHelper.getUsername());

            ApiHelper.execute(() -> {
                retrofit2.Response<com.example.indiegamecommunity.network.ApiResponse<PromoDto>> resp =
                    ApiClient.getApiService().createPromo(dto).execute();
                if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200) return "ok";
                throw new Exception("发布失败");
            }, new ApiHelper.Callback<String>() {
                @Override
                public void onSuccess(String data) {
                    Toast.makeText(getContext(), "发布成功！作品将经过管理员审核后显示。", Toast.LENGTH_SHORT).show();
                    loadGamePromos();
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
        Bitmap bitmap = BitmapFactory.decodeStream(input);
        input.close();
        File file = new File(gameIconDir, "icon_" + System.currentTimeMillis() + ".png");
        FileOutputStream out = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
        out.flush(); out.close();
        return file.getAbsolutePath();
    }

    @Override
    public void onResume() { super.onResume(); loadGamePromos(); }
}
