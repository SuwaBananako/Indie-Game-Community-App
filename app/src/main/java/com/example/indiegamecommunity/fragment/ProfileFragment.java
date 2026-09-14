package com.example.indiegamecommunity.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.indiegamecommunity.R;
import com.example.indiegamecommunity.activity.LoginActivity;
import com.example.indiegamecommunity.activity.AdminActivity;
import com.example.indiegamecommunity.activity.PostDetailActivity;
import com.example.indiegamecommunity.activity.PostEditActivity;
import com.example.indiegamecommunity.activity.SettingsActivity;
import com.example.indiegamecommunity.adapter.PostAdapter;
import com.example.indiegamecommunity.database.DatabaseHelper;
import com.example.indiegamecommunity.database.UserDatabaseHelper;
import com.example.indiegamecommunity.database.UserProfileDatabaseHelper;
import com.example.indiegamecommunity.model.Post;
import com.example.indiegamecommunity.network.*;
import com.example.indiegamecommunity.utils.SharedPreferencesHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment implements PostAdapter.OnItemClickListener {
    private static final int REQUEST_AVATAR = 100;
    private static final int REQUEST_ICON = 2003;
    private static final int REQUEST_BANNER = 2004;

    private TextView tvUsername, tvUserDesc, tvPostCount, tvCollectCount, tvGameCount;
    private ImageView ivAvatar;
    private LinearLayout layoutMyGames, layoutMyCollections, layoutMyPromos, layoutMyFunding, layoutAddGame, layoutEditProfile, layoutSettings;
    private Button btnLogout;
    private RecyclerView rvMyPosts;
    private PostAdapter postAdapter;
    private DatabaseHelper databaseHelper;
    private UserDatabaseHelper userDatabaseHelper;
    private UserProfileDatabaseHelper userProfileDatabaseHelper;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private ImageView dialogIconPreview;
    private ImageView dialogBannerPreview;
    private String pendingIconPath = "";
    private String pendingBannerPath = "";
    private String gameIconDir;
    private String username;

    private ImageView ivEditAvatar;
    private Bitmap selectedAvatarBitmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvUsername = view.findViewById(R.id.tvUsername);
        tvUserDesc = view.findViewById(R.id.tvUserDesc);
        tvPostCount = view.findViewById(R.id.tvPostCount);
        tvCollectCount = view.findViewById(R.id.tvCollectCount);
        tvGameCount = view.findViewById(R.id.tvGameCount);
        ivAvatar = view.findViewById(R.id.ivAvatar);
        layoutMyGames = view.findViewById(R.id.layoutMyGames);
        layoutMyCollections = view.findViewById(R.id.layoutMyCollections);
        layoutMyPromos = view.findViewById(R.id.layoutMyPromos);
        layoutMyFunding = view.findViewById(R.id.layoutMyFunding);
        layoutAddGame = view.findViewById(R.id.layoutAddGame);
        layoutEditProfile = view.findViewById(R.id.layoutEditProfile);
        layoutSettings = view.findViewById(R.id.layoutSettings);
        btnLogout = view.findViewById(R.id.btnLogout);
        rvMyPosts = view.findViewById(R.id.rvMyPosts);

        sharedPreferencesHelper = new SharedPreferencesHelper(getContext());
        userDatabaseHelper = new UserDatabaseHelper(getContext());
        databaseHelper = new DatabaseHelper(getContext());
        username = sharedPreferencesHelper.getUsername();

        String nickname = userDatabaseHelper.getNickname(username);
        String avatar = userDatabaseHelper.getAvatar(username);
        String desc = userDatabaseHelper.getDescription(username);

        tvUsername.setText(nickname);
        if (desc != null && !desc.isEmpty()) {
            tvUserDesc.setText(desc);
        }
        loadAvatar(avatar);

        userProfileDatabaseHelper = new UserProfileDatabaseHelper(getContext(), username);
        gameIconDir = new File(getContext().getFilesDir(), "game_icons").getAbsolutePath();
        new File(gameIconDir).mkdirs();

        loadStats();
        loadMyPosts();

        layoutMyGames.setOnClickListener(v -> showMyGames());
        layoutMyCollections.setOnClickListener(v -> showMyCollections());
        layoutMyPromos.setOnClickListener(v -> showMyPromos());
        layoutMyFunding.setOnClickListener(v -> showMyCrowdfundings());
        layoutAddGame.setOnClickListener(v -> showAddGameDialog());
        layoutEditProfile.setOnClickListener(v -> showEditProfileDialog(username));
        layoutSettings.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            sharedPreferencesHelper.clearLoginInfo();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        // Notification bell
        Button btnNotifications = view.findViewById(R.id.btnNotifications);
        int unreadCount = databaseHelper.getUnreadNotificationCount(username);
        btnNotifications.setText("通知" + (unreadCount > 0 ? " (" + unreadCount + ")" : ""));
        btnNotifications.setOnClickListener(v -> showNotifications());

        // 管理员入口
        Button btnAdminPanel = view.findViewById(R.id.btnAdminPanel);
        if (sharedPreferencesHelper.isAdmin()) {
            btnAdminPanel.setVisibility(View.VISIBLE);
            btnAdminPanel.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), AdminActivity.class);
                startActivity(intent);
            });
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStats();
        loadMyPosts();
    }

    private void loadStats() {
        String username = sharedPreferencesHelper.getUsername();
        List<Integer> collectedIds = userProfileDatabaseHelper.getCollectionPostIds();
        tvCollectCount.setText(String.valueOf(collectedIds.size()));

        // Load post count from API
        ApiHelper.execute(() -> {
            retrofit2.Response<ApiResponse<List<PostDto>>> resp = ApiClient.getApiService().getMyPosts(username).execute();
            if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200 && resp.body().getData() != null) {
                return resp.body().getData().size();
            }
            return 0;
        }, new ApiHelper.Callback<Integer>() {
            @Override
            public void onSuccess(Integer count) {
                tvPostCount.setText(String.valueOf(count));
            }
            @Override public void onError(String message) {}
        });

        // Load game count from API
        ApiHelper.execute(() -> {
            retrofit2.Response<ApiResponse<List<VoteDto>>> resp = ApiClient.getApiService().getMyVotes(username).execute();
            if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200 && resp.body().getData() != null) {
                return resp.body().getData().size();
            }
            return 0;
        }, new ApiHelper.Callback<Integer>() {
            @Override
            public void onSuccess(Integer count) {
                tvGameCount.setText(String.valueOf(count));
            }
            @Override public void onError(String message) {}
        });
    }

    private void loadAvatar(String avatarBase64) {
        if (avatarBase64 != null && !avatarBase64.isEmpty()) {
            byte[] decodedString = android.util.Base64.decode(avatarBase64, android.util.Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ivAvatar.setImageBitmap(decodedByte);
        } else {
            ivAvatar.setImageResource(android.R.drawable.ic_menu_my_calendar);
        }
    }

    private void loadMyPosts() {
        ApiHelper.execute(() -> {
            retrofit2.Response<ApiResponse<List<PostDto>>> resp = ApiClient.getApiService().getMyPosts(username).execute();
            if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200 && resp.body().getData() != null) {
                List<Post> list = new ArrayList<>();
                for (PostDto dto : resp.body().getData()) list.add(DtoConverter.toPost(dto));
                return list;
            }
            return new ArrayList<Post>();
        }, new ApiHelper.Callback<List<Post>>() {
            @Override
            public void onSuccess(List<Post> posts) {
                rvMyPosts.setLayoutManager(new LinearLayoutManager(getContext()));
                postAdapter = new PostAdapter(getContext(), posts, ProfileFragment.this);
                rvMyPosts.setAdapter(postAdapter);
            }
            @Override public void onError(String message) {}
        });
    }

    @Override
    public void onItemClick(Post post) {
        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
        intent.putExtra("post_id", post.getId());
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(Post post) {
        // Long click on my posts - could show edit/delete options
    }

    private void showMyGames() {
        android.content.Context ctx = getContext();
        ApiHelper.execute(() -> {
            retrofit2.Response<ApiResponse<List<VoteDto>>> resp = ApiClient.getApiService().getMyVotes(username).execute();
            if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200 && resp.body().getData() != null) {
                return resp.body().getData();
            }
            return new ArrayList<VoteDto>();
        }, new ApiHelper.Callback<List<VoteDto>>() {
            @Override
            public void onSuccess(List<VoteDto> myGames) {
                if (myGames.isEmpty()) {
                    Toast.makeText(ctx, "暂无游戏", Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle("我的游戏（点击查看详情，长按删除）");
                List<String> gameList = new ArrayList<>();
                for (int i = 0; i < myGames.size(); i++) {
                    VoteDto game = myGames.get(i);
                    String statusStr = "";
                    if ("pending".equals(game.getStatus())) statusStr = "[审核中] ";
                    else if ("rejected".equals(game.getStatus())) statusStr = "[已拒绝] ";
                    gameList.add((i + 1) + ". " + statusStr + game.getGameName() + " (票数: " + game.getVoteCount() + ")");
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ctx, android.R.layout.simple_list_item_1, gameList);
                ListView listView = new ListView(ctx);
                listView.setAdapter(adapter);
                listView.setOnItemLongClickListener((parent, view, position, id) -> {
                    VoteDto game = myGames.get(position);
                    new AlertDialog.Builder(ctx)
                        .setTitle("删除游戏")
                        .setMessage("确定删除《" + game.getGameName() + "》吗？")
                        .setPositiveButton("删除", (d, w) -> {
                            ApiHelper.execute(() -> {
                                ApiClient.getApiService().deleteVote(game.getId()).execute();
                                return "ok";
                            }, new ApiHelper.Callback<String>() {
                                @Override public void onSuccess(String data) {
                                    Toast.makeText(ctx, "已删除", Toast.LENGTH_SHORT).show(); showMyGames();
                                }
                                @Override public void onError(String msg) { Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show(); }
                            });
                        }).setNegativeButton("取消", null).show();
                    return true;
                });
                listView.setOnItemClickListener((parent, view, position, id) -> {
                    VoteDto game = myGames.get(position);
                    Intent intent = new Intent(getActivity(), com.example.indiegamecommunity.activity.VoteDetailActivity.class);
                    intent.putExtra("vote_id", (int) game.getId());
                    intent.putExtra("rank", position + 1);
                    startActivity(intent);
                });
                builder.setView(listView);
                builder.show();
            }
            @Override public void onError(String msg) { Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show(); }
        });
    }

    private void showMyPromos() {
        android.content.Context ctx = getContext();
        ApiHelper.execute(() -> {
            retrofit2.Response<ApiResponse<List<PromoDto>>> resp = ApiClient.getApiService().getMyPromos(username).execute();
            if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200 && resp.body().getData() != null) {
                return resp.body().getData();
            }
            return new ArrayList<PromoDto>();
        }, new ApiHelper.Callback<List<PromoDto>>() {
            @Override
            public void onSuccess(List<PromoDto> myPromos) {
                if (myPromos.isEmpty()) { Toast.makeText(ctx, "暂无宣发", Toast.LENGTH_SHORT).show(); return; }
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle("我的宣发（长按可删除）");
                List<String> items = new ArrayList<>();
                for (PromoDto p : myPromos) {
                    String statusStr = "";
                    if ("pending".equals(p.getStatus())) statusStr = "[审核中] ";
                    else if ("rejected".equals(p.getStatus())) statusStr = "[已拒绝] ";
                    items.add(statusStr + p.getName());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ctx, android.R.layout.simple_list_item_1, items);
                ListView listView = new ListView(ctx);
                listView.setAdapter(adapter);
                listView.setOnItemLongClickListener((parent, view, position, id) -> {
                    PromoDto promo = myPromos.get(position);
                    new AlertDialog.Builder(ctx)
                        .setTitle("删除宣发")
                        .setMessage("确定删除《" + promo.getName() + "》吗？")
                        .setPositiveButton("删除", (d, w) -> {
                            ApiHelper.execute(() -> {
                                ApiClient.getApiService().deletePromo(promo.getId()).execute();
                                return "ok";
                            }, new ApiHelper.Callback<String>() {
                                @Override public void onSuccess(String data) {
                                    Toast.makeText(ctx, "已删除", Toast.LENGTH_SHORT).show(); showMyPromos();
                                }
                                @Override public void onError(String msg) { Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show(); }
                            });
                        }).setNegativeButton("取消", null).show();
                    return true;
                });
                listView.setOnItemClickListener((parent, view, position, id) -> {
                    PromoDto promo = myPromos.get(position);
                    Intent intent = new Intent(getActivity(), com.example.indiegamecommunity.activity.GameDetailActivity.class);
                    intent.putExtra("game_id", (int) promo.getId());
                    startActivity(intent);
                });
                builder.setView(listView);
                builder.show();
            }
            @Override public void onError(String msg) { Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show(); }
        });
    }

    private void showMyCrowdfundings() {
        android.content.Context ctx = getContext();
        ApiHelper.execute(() -> {
            retrofit2.Response<ApiResponse<List<CFDTo>>> resp = ApiClient.getApiService().getMyCFs(username).execute();
            if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200 && resp.body().getData() != null) {
                return resp.body().getData();
            }
            return new ArrayList<CFDTo>();
        }, new ApiHelper.Callback<List<CFDTo>>() {
            @Override
            public void onSuccess(List<CFDTo> myCf) {
                if (myCf.isEmpty()) { Toast.makeText(ctx, "暂无众筹", Toast.LENGTH_SHORT).show(); return; }
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle("我的众筹（长按可删除）");
                List<String> items = new ArrayList<>();
                for (CFDTo c : myCf) {
                    String statusStr = "";
                    if ("pending".equals(c.getStatus())) statusStr = "[审核中] ";
                    else if ("rejected".equals(c.getStatus())) statusStr = "[已拒绝] ";
                    items.add(statusStr + c.getName() + " (" + c.getCurrentAmount() + "/" + c.getTargetAmount() + ")");
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ctx, android.R.layout.simple_list_item_1, items);
                ListView listView = new ListView(ctx);
                listView.setAdapter(adapter);
                listView.setOnItemLongClickListener((parent, view, position, id) -> {
                    CFDTo cf = myCf.get(position);
                    new AlertDialog.Builder(ctx)
                        .setTitle("删除众筹")
                        .setMessage("确定删除《" + cf.getName() + "》吗？")
                        .setPositiveButton("删除", (d, w) -> {
                            ApiHelper.execute(() -> {
                                ApiClient.getApiService().deleteCF(cf.getId()).execute();
                                return "ok";
                            }, new ApiHelper.Callback<String>() {
                                @Override public void onSuccess(String data) {
                                    Toast.makeText(ctx, "已删除", Toast.LENGTH_SHORT).show(); showMyCrowdfundings();
                                }
                                @Override public void onError(String msg) { Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show(); }
                            });
                        }).setNegativeButton("取消", null).show();
                    return true;
                });
                listView.setOnItemClickListener((parent, view, position, id) -> {
                    CFDTo cf = myCf.get(position);
                    Intent intent = new Intent(getActivity(), com.example.indiegamecommunity.activity.ProjectDetailActivity.class);
                    intent.putExtra("project_id", (int) cf.getId());
                    startActivity(intent);
                });
                builder.setView(listView);
                builder.show();
            }
            @Override public void onError(String msg) { Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show(); }
        });
    }

    private void showNotifications() {
        android.content.Context ctx = getContext();
        ApiHelper.execute(() -> {
            retrofit2.Response<ApiResponse<List<NotifDto>>> resp = ApiClient.getApiService().getNotifications(username).execute();
            if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200 && resp.body().getData() != null) {
                return resp.body().getData();
            }
            return new ArrayList<NotifDto>();
        }, new ApiHelper.Callback<List<NotifDto>>() {
            @Override
            public void onSuccess(List<NotifDto> notifications) {
                if (notifications.isEmpty()) { Toast.makeText(ctx, "暂无通知", Toast.LENGTH_SHORT).show(); return; }
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle("审核通知");
                List<String> items = new ArrayList<>();
                for (NotifDto n : notifications) {
                    String prefix = n.isRead() ? "" : "[未读] ";
                    String msg = n.getMessage();
                    if (msg != null && msg.length() > 40) msg = msg.substring(0, 40) + "...";
                    items.add(prefix + msg);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ctx, android.R.layout.simple_list_item_1, items);
                ListView listView = new ListView(ctx);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener((parent, view, position, id) -> {
                    NotifDto n = notifications.get(position);
                    ApiHelper.execute(() -> { ApiClient.getApiService().markRead(n.getId()).execute(); return "ok"; }, new ApiHelper.Callback<String>() {
                        @Override public void onSuccess(String data) { Toast.makeText(ctx, n.getMessage(), Toast.LENGTH_LONG).show(); showNotifications(); }
                        @Override public void onError(String msg) {}
                    });
                });
                builder.setNeutralButton("全部已读", (d, w) -> {
                    ApiHelper.execute(() -> { ApiClient.getApiService().markAllRead(username).execute(); return "ok"; }, new ApiHelper.Callback<String>() {
                        @Override public void onSuccess(String data) { Toast.makeText(ctx, "已标记全部已读", Toast.LENGTH_SHORT).show(); }
                        @Override public void onError(String msg) {}
                    });
                });
                builder.setNegativeButton("关闭", null);
                builder.setView(listView);
                builder.show();
            }
            @Override public void onError(String msg) { Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show(); }
        });
    }

    private void showAddGameDialog() {
        pendingIconPath = "";
        pendingBannerPath = "";
        android.content.Context ctx = getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("添加游戏");

        View dialogView = LayoutInflater.from(ctx).inflate(R.layout.dialog_add_game, null);
        builder.setView(dialogView);

        dialogIconPreview = dialogView.findViewById(R.id.ivGameIcon);
        dialogBannerPreview = dialogView.findViewById(R.id.ivBannerPreview);
        Button btnSelectIcon = dialogView.findViewById(R.id.btnSelectIcon);
        Button btnSelectBanner = dialogView.findViewById(R.id.btnSelectBanner);
        EditText etGameName = dialogView.findViewById(R.id.etGameName);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);

        btnSelectIcon.setOnClickListener(v -> startActivityForResult(
            new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), REQUEST_ICON));
        btnSelectBanner.setOnClickListener(v -> startActivityForResult(
            new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), REQUEST_BANNER));

        builder.setPositiveButton("添加", (dialog, which) -> {
            String gameName = etGameName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            if (gameName.isEmpty()) { Toast.makeText(ctx, "请输入游戏名称", Toast.LENGTH_SHORT).show(); return; }

            VoteDto vote = new VoteDto();
            vote.setGameName(gameName);
            vote.setDescription(description);
            vote.setIconPath(pendingIconPath);
            vote.setBannerPath(pendingBannerPath);
            vote.setAuthorUsername(username);
            vote.setStatus("pending");

            ApiHelper.execute(() -> {
                retrofit2.Response<ApiResponse<VoteDto>> resp = ApiClient.getApiService().createVote(vote).execute();
                if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200) return "ok";
                throw new Exception("添加失败");
            }, new ApiHelper.Callback<String>() {
                @Override
                public void onSuccess(String data) {
                    Toast.makeText(ctx, "添加成功！游戏将经过管理员审核后显示。", Toast.LENGTH_SHORT).show();
                    loadStats();
                }
                @Override
                public void onError(String msg) { Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show(); }
            });
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void showMyCollections() {
        android.content.Context ctx = getContext();
        List<Integer> collectedPostIds = userProfileDatabaseHelper.getCollectionPostIds();
        if (collectedPostIds.isEmpty()) {
            Toast.makeText(ctx, "暂无收藏", Toast.LENGTH_SHORT).show();
            return;
        }
        // Load post titles from API for each collected post
        ApiHelper.execute(() -> {
            List<PostDto> posts = new ArrayList<>();
            for (int pid : collectedPostIds) {
                try {
                    retrofit2.Response<ApiResponse<PostDto>> resp = ApiClient.getApiService().getPostById(pid).execute();
                    if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200 && resp.body().getData() != null) {
                        posts.add(resp.body().getData());
                    }
                } catch (Exception ignored) {}
            }
            return posts;
        }, new ApiHelper.Callback<List<PostDto>>() {
            @Override
            public void onSuccess(List<PostDto> posts) {
                if (posts.isEmpty()) { Toast.makeText(ctx, "暂无收藏", Toast.LENGTH_SHORT).show(); return; }
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle("我的收藏");
                List<String> titles = new ArrayList<>();
                for (int i = 0; i < posts.size(); i++) titles.add((i + 1) + ". " + posts.get(i).getTitle());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ctx, android.R.layout.simple_list_item_1, titles);
                ListView listView = new ListView(ctx);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener((parent, view, position, id) -> {
                    Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                    intent.putExtra("post_id", (int) posts.get(position).getId());
                    startActivity(intent);
                });
                builder.setView(listView);
                builder.show();
            }
            @Override public void onError(String msg) { Toast.makeText(ctx, "加载失败", Toast.LENGTH_SHORT).show(); }
        });
    }

    private void showEditProfileDialog(String username) {
        android.content.Context ctx = getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("编辑资料");

        View dialogView = LayoutInflater.from(ctx).inflate(R.layout.dialog_edit_profile, null);
        builder.setView(dialogView);

        ivEditAvatar = dialogView.findViewById(R.id.ivEditAvatar);
        EditText etNickname = dialogView.findViewById(R.id.etNickname);
        Button btnChangeAvatar = dialogView.findViewById(R.id.btnChangeAvatar);

        String currentNickname = userDatabaseHelper.getNickname(username);
        String currentAvatar = userDatabaseHelper.getAvatar(username);

        etNickname.setText(currentNickname);
        selectedAvatarBitmap = null;

        if (currentAvatar != null && !currentAvatar.isEmpty()) {
            byte[] decodedString = android.util.Base64.decode(currentAvatar, android.util.Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ivEditAvatar.setImageBitmap(decodedByte);
        }

        btnChangeAvatar.setOnClickListener(v -> openImagePicker());

        builder.setPositiveButton("保存", (dialog, which) -> {
            String nickname = etNickname.getText().toString().trim();
            if (nickname.isEmpty()) { Toast.makeText(ctx, "昵称不能为空", Toast.LENGTH_SHORT).show(); return; }
            String avatar = currentAvatar;
            if (selectedAvatarBitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                selectedAvatarBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                byte[] imageBytes = baos.toByteArray();
                avatar = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);
            }
            if (userDatabaseHelper.updateUserInfo(username, nickname, avatar)) {
                Toast.makeText(ctx, "修改成功", Toast.LENGTH_SHORT).show();
                tvUsername.setText(nickname);
                loadAvatar(avatar);
            } else {
                Toast.makeText(ctx, "修改失败", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "选择头像"), REQUEST_AVATAR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != getActivity().RESULT_OK || data == null) return;
        if (requestCode == REQUEST_AVATAR) {
            try {
                Uri uri = data.getData();
                if (uri == null) return;
                InputStream input = getActivity().getContentResolver().openInputStream(uri);
                selectedAvatarBitmap = BitmapFactory.decodeStream(input);
                input.close();
                if (selectedAvatarBitmap != null && ivEditAvatar != null) {
                    ivEditAvatar.setImageBitmap(selectedAvatarBitmap);
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "选择头像失败", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_ICON) {
            Uri uri = data.getData();
            if (uri != null) { pendingIconPath = saveImageToFile(uri); if (pendingIconPath != null) loadDialogPreview(dialogIconPreview, pendingIconPath); }
        } else if (requestCode == REQUEST_BANNER) {
            Uri uri = data.getData();
            if (uri != null) { pendingBannerPath = saveImageToFile(uri); if (pendingBannerPath != null) loadDialogPreview(dialogBannerPreview, pendingBannerPath); }
        }
    }

    private void loadDialogPreview(ImageView iv, String path) {
        try { iv.setImageBitmap(BitmapFactory.decodeFile(path)); } catch (Exception ignored) {}
    }

    private String saveImageToFile(Uri uri) {
        try {
            InputStream input = getActivity().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input); input.close();
            File file = new File(gameIconDir, "icon_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.flush(); out.close();
            return file.getAbsolutePath();
        } catch (Exception e) { return null; }
    }
}
