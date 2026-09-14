package com.example.indiegamecommunity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.indiegamecommunity.R;
import com.example.indiegamecommunity.network.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminReviewFragment extends Fragment {
    private LinearLayout layoutPendingPosts;
    private LinearLayout layoutPendingPromos;
    private LinearLayout layoutPendingCrowdfundings;
    private LinearLayout layoutPendingVotes;
    private View view;
    private SimpleDateFormat sdf;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_admin_review, container, false);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        layoutPendingPosts = view.findViewById(R.id.layoutPendingPosts);
        layoutPendingPromos = view.findViewById(R.id.layoutPendingPromos);
        layoutPendingCrowdfundings = view.findViewById(R.id.layoutPendingCrowdfundings);
        layoutPendingVotes = view.findViewById(R.id.layoutPendingVotes);

        loadPendingContent();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPendingContent();
    }

    void loadPendingContent() {
        loadPendingPosts();
        loadPendingPromos();
        loadPendingCrowdfundings();
        loadPendingVotes();
    }

    private void loadPendingPosts() {
        layoutPendingPosts.removeAllViews();
        ApiHelper.execute(() -> {
            retrofit2.Response<ApiResponse<List<AdminReviewItem>>> resp = ApiClient.getApiService().getPendingPosts().execute();
            if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200 && resp.body().getData() != null) {
                return resp.body().getData();
            }
            return new ArrayList<AdminReviewItem>();
        }, new ApiHelper.Callback<List<AdminReviewItem>>() {
            @Override
            public void onSuccess(List<AdminReviewItem> pendingPosts) {
                if (pendingPosts.isEmpty()) {
                    addEmptyText(layoutPendingPosts, "暂无需审核的帖子");
                    return;
                }
                for (AdminReviewItem post : pendingPosts) {
                    View card = LayoutInflater.from(getContext()).inflate(R.layout.item_admin_review, layoutPendingPosts, false);
                    TextView tvTitle = card.findViewById(R.id.tvTitle);
                    TextView tvInfo = card.findViewById(R.id.tvInfo);
                    TextView tvPreview = card.findViewById(R.id.tvPreview);
                    Button btnApprove = card.findViewById(R.id.btnApprove);
                    Button btnReject = card.findViewById(R.id.btnReject);

                    tvTitle.setText(post.getTitle());
                    tvInfo.setText("作者: " + post.getUsername() + " | " + sdf.format(new Date(post.getCreateTime())));
                    String preview = post.getContent();
                    if (preview != null && preview.length() > 80) preview = preview.substring(0, 80) + "...";
                    tvPreview.setText(preview != null ? preview : "");

                    btnApprove.setOnClickListener(v -> {
                        ApiHelper.execute(() -> { ApiClient.getApiService().approvePost(post.getId()).execute(); return "ok"; },
                            new ApiHelper.Callback<String>() {
                                @Override public void onSuccess(String d) { Toast.makeText(getContext(), "已通过帖子审核", Toast.LENGTH_SHORT).show(); loadPendingPosts(); }
                                @Override public void onError(String m) { Toast.makeText(getContext(), m, Toast.LENGTH_SHORT).show(); }
                            });
                    });
                    btnReject.setOnClickListener(v -> {
                        ApiHelper.execute(() -> { ApiClient.getApiService().rejectPost(post.getId()).execute(); return "ok"; },
                            new ApiHelper.Callback<String>() {
                                @Override public void onSuccess(String d) { Toast.makeText(getContext(), "已拒绝帖子", Toast.LENGTH_SHORT).show(); loadPendingPosts(); }
                                @Override public void onError(String m) { Toast.makeText(getContext(), m, Toast.LENGTH_SHORT).show(); }
                            });
                    });
                    layoutPendingPosts.addView(card);
                }
            }
            @Override public void onError(String m) {}
        });
    }

    private void loadPendingPromos() {
        layoutPendingPromos.removeAllViews();
        ApiHelper.execute(() -> {
            retrofit2.Response<ApiResponse<List<AdminReviewItem>>> resp = ApiClient.getApiService().getPendingPromos().execute();
            if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200 && resp.body().getData() != null) {
                return resp.body().getData();
            }
            return new ArrayList<AdminReviewItem>();
        }, new ApiHelper.Callback<List<AdminReviewItem>>() {
            @Override
            public void onSuccess(List<AdminReviewItem> items) {
                if (items.isEmpty()) { addEmptyText(layoutPendingPromos, "暂无需审核的宣发"); return; }
                for (AdminReviewItem p : items) {
                    View card = LayoutInflater.from(getContext()).inflate(R.layout.item_admin_review, layoutPendingPromos, false);
                    TextView tvTitle = card.findViewById(R.id.tvTitle);
                    TextView tvInfo = card.findViewById(R.id.tvInfo);
                    TextView tvPreview = card.findViewById(R.id.tvPreview);
                    Button btnApprove = card.findViewById(R.id.btnApprove);
                    Button btnReject = card.findViewById(R.id.btnReject);

                    tvTitle.setText("[宣发] " + p.getName());
                    tvInfo.setText("作者: " + p.getUsername() + " | " + sdf.format(new Date(p.getCreateTime())));
                    String preview = p.getDescription();
                    if (preview != null && preview.length() > 80) preview = preview.substring(0, 80) + "...";
                    tvPreview.setText(preview != null ? preview : "");

                    btnApprove.setOnClickListener(v -> {
                        ApiHelper.execute(() -> { ApiClient.getApiService().approvePromo(p.getId()).execute(); return "ok"; },
                            new ApiHelper.Callback<String>() {
                                @Override public void onSuccess(String d) { Toast.makeText(getContext(), "已通过宣发审核", Toast.LENGTH_SHORT).show(); loadPendingPromos(); }
                                @Override public void onError(String m) { Toast.makeText(getContext(), m, Toast.LENGTH_SHORT).show(); }
                            });
                    });
                    btnReject.setOnClickListener(v -> {
                        ApiHelper.execute(() -> { ApiClient.getApiService().rejectPromo(p.getId()).execute(); return "ok"; },
                            new ApiHelper.Callback<String>() {
                                @Override public void onSuccess(String d) { Toast.makeText(getContext(), "已拒绝宣发", Toast.LENGTH_SHORT).show(); loadPendingPromos(); }
                                @Override public void onError(String m) { Toast.makeText(getContext(), m, Toast.LENGTH_SHORT).show(); }
                            });
                    });
                    layoutPendingPromos.addView(card);
                }
            }
            @Override public void onError(String m) {}
        });
    }

    private void loadPendingCrowdfundings() {
        layoutPendingCrowdfundings.removeAllViews();
        ApiHelper.execute(() -> {
            retrofit2.Response<ApiResponse<List<AdminReviewItem>>> resp = ApiClient.getApiService().getPendingCFs().execute();
            if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200 && resp.body().getData() != null) {
                return resp.body().getData();
            }
            return new ArrayList<AdminReviewItem>();
        }, new ApiHelper.Callback<List<AdminReviewItem>>() {
            @Override
            public void onSuccess(List<AdminReviewItem> items) {
                if (items.isEmpty()) { addEmptyText(layoutPendingCrowdfundings, "暂无需审核的众筹"); return; }
                for (AdminReviewItem c : items) {
                    View card = LayoutInflater.from(getContext()).inflate(R.layout.item_admin_review, layoutPendingCrowdfundings, false);
                    TextView tvTitle = card.findViewById(R.id.tvTitle);
                    TextView tvInfo = card.findViewById(R.id.tvInfo);
                    TextView tvPreview = card.findViewById(R.id.tvPreview);
                    Button btnApprove = card.findViewById(R.id.btnApprove);
                    Button btnReject = card.findViewById(R.id.btnReject);

                    tvTitle.setText("[众筹] " + c.getName());
                    tvInfo.setText("作者: " + c.getUsername() + " | 目标: " + c.getTargetAmount() + " | " + sdf.format(new Date(c.getCreateTime())));
                    String preview = c.getDescription();
                    if (preview != null && preview.length() > 80) preview = preview.substring(0, 80) + "...";
                    tvPreview.setText(preview != null ? preview : "");

                    btnApprove.setOnClickListener(v -> {
                        ApiHelper.execute(() -> { ApiClient.getApiService().approveCF(c.getId()).execute(); return "ok"; },
                            new ApiHelper.Callback<String>() {
                                @Override public void onSuccess(String d) { Toast.makeText(getContext(), "已通过众筹审核", Toast.LENGTH_SHORT).show(); loadPendingCrowdfundings(); }
                                @Override public void onError(String m) { Toast.makeText(getContext(), m, Toast.LENGTH_SHORT).show(); }
                            });
                    });
                    btnReject.setOnClickListener(v -> {
                        ApiHelper.execute(() -> { ApiClient.getApiService().rejectCF(c.getId()).execute(); return "ok"; },
                            new ApiHelper.Callback<String>() {
                                @Override public void onSuccess(String d) { Toast.makeText(getContext(), "已拒绝众筹", Toast.LENGTH_SHORT).show(); loadPendingCrowdfundings(); }
                                @Override public void onError(String m) { Toast.makeText(getContext(), m, Toast.LENGTH_SHORT).show(); }
                            });
                    });
                    layoutPendingCrowdfundings.addView(card);
                }
            }
            @Override public void onError(String m) {}
        });
    }

    private void loadPendingVotes() {
        layoutPendingVotes.removeAllViews();
        ApiHelper.execute(() -> {
            retrofit2.Response<ApiResponse<List<AdminReviewItem>>> resp = ApiClient.getApiService().getPendingVotes().execute();
            if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200 && resp.body().getData() != null) {
                return resp.body().getData();
            }
            return new ArrayList<AdminReviewItem>();
        }, new ApiHelper.Callback<List<AdminReviewItem>>() {
            @Override
            public void onSuccess(List<AdminReviewItem> items) {
                if (items.isEmpty()) { addEmptyText(layoutPendingVotes, "暂无需审核的游戏"); return; }
                for (AdminReviewItem v : items) {
                    View card = LayoutInflater.from(getContext()).inflate(R.layout.item_admin_review, layoutPendingVotes, false);
                    TextView tvTitle = card.findViewById(R.id.tvTitle);
                    TextView tvInfo = card.findViewById(R.id.tvInfo);
                    TextView tvPreview = card.findViewById(R.id.tvPreview);
                    Button btnApprove = card.findViewById(R.id.btnApprove);
                    Button btnReject = card.findViewById(R.id.btnReject);

                    tvTitle.setText("[投票游戏] " + v.getName());
                    tvInfo.setText("作者: " + v.getUsername() + " | " + sdf.format(new Date(v.getCreateTime())));
                    String preview = v.getDescription();
                    if (preview != null && preview.length() > 80) preview = preview.substring(0, 80) + "...";
                    tvPreview.setText(preview != null ? preview : "");

                    btnApprove.setOnClickListener(v1 -> {
                        ApiHelper.execute(() -> { ApiClient.getApiService().approveVote(v.getId()).execute(); return "ok"; },
                            new ApiHelper.Callback<String>() {
                                @Override public void onSuccess(String d) { Toast.makeText(getContext(), "已通过游戏审核", Toast.LENGTH_SHORT).show(); loadPendingVotes(); }
                                @Override public void onError(String m) { Toast.makeText(getContext(), m, Toast.LENGTH_SHORT).show(); }
                            });
                    });
                    btnReject.setOnClickListener(v1 -> {
                        ApiHelper.execute(() -> { ApiClient.getApiService().rejectVote(v.getId()).execute(); return "ok"; },
                            new ApiHelper.Callback<String>() {
                                @Override public void onSuccess(String d) { Toast.makeText(getContext(), "已拒绝游戏", Toast.LENGTH_SHORT).show(); loadPendingVotes(); }
                                @Override public void onError(String m) { Toast.makeText(getContext(), m, Toast.LENGTH_SHORT).show(); }
                            });
                    });
                    layoutPendingVotes.addView(card);
                }
            }
            @Override public void onError(String m) {}
        });
    }

    private void addEmptyText(LinearLayout layout, String text) {
        TextView tv = new TextView(getContext());
        tv.setText(text);
        tv.setTextSize(13);
        tv.setPadding(16, 16, 16, 16);
        tv.setTextColor(0xFF888888);
        layout.addView(tv);
    }
}
