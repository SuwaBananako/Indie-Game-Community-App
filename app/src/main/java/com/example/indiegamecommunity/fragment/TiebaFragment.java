package com.example.indiegamecommunity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.indiegamecommunity.R;
import com.example.indiegamecommunity.activity.PostDetailActivity;
import com.example.indiegamecommunity.activity.PostEditActivity;
import com.example.indiegamecommunity.adapter.PostAdapter;
import com.example.indiegamecommunity.network.ApiClient;
import com.example.indiegamecommunity.network.ApiHelper;
import com.example.indiegamecommunity.network.DtoConverter;
import com.example.indiegamecommunity.model.Post;

import java.util.ArrayList;
import java.util.List;

public class TiebaFragment extends Fragment {
    private RecyclerView rvPosts;
    private EditText etSearch;
    private Button btnSearch, btnPost;
    private PostAdapter postAdapter;
    private List<Post> postList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tieba, container, false);

        rvPosts = view.findViewById(R.id.rvPosts);
        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnPost = view.findViewById(R.id.btnPost);

        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        loadPosts();

        btnSearch.setOnClickListener(v -> {
            String keyword = etSearch.getText().toString().trim();
            if (keyword.isEmpty()) {
                loadPosts();
            } else {
                searchPosts(keyword);
            }
        });

        btnPost.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), PostEditActivity.class));
        });

        return view;
    }

    private void loadPosts() {
        ApiHelper.execute(() -> {
            retrofit2.Response<com.example.indiegamecommunity.network.ApiResponse<com.example.indiegamecommunity.network.PageResponse<com.example.indiegamecommunity.network.PostDto>>> resp =
                ApiClient.getApiService().getApprovedPosts(0, 50).execute();
            if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200
                && resp.body().getData() != null && resp.body().getData().getContent() != null) {
                List<Post> list = new ArrayList<>();
                for (com.example.indiegamecommunity.network.PostDto dto : resp.body().getData().getContent()) {
                    list.add(DtoConverter.toPost(dto));
                }
                return list;
            }
            return new ArrayList<Post>();
        }, new ApiHelper.Callback<List<Post>>() {
            @Override
            public void onSuccess(List<Post> data) {
                postList = data;
                postAdapter = new PostAdapter(getContext(), postList, new PostAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Post post) {
                        Intent intent = new Intent(getContext(), PostDetailActivity.class);
                        intent.putExtra("post_id", post.getId());
                        startActivity(intent);
                    }
                    @Override
                    public void onItemLongClick(Post post) {
                        showPostOptionsDialog(post);
                    }
                });
                rvPosts.setAdapter(postAdapter);
            }
            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "加载失败: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchPosts(String keyword) {
        ApiHelper.execute(() -> {
            retrofit2.Response<com.example.indiegamecommunity.network.ApiResponse<com.example.indiegamecommunity.network.PageResponse<com.example.indiegamecommunity.network.PostDto>>> resp =
                ApiClient.getApiService().searchPosts(keyword, 0, 50).execute();
            if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200
                && resp.body().getData() != null && resp.body().getData().getContent() != null) {
                List<Post> list = new ArrayList<>();
                for (com.example.indiegamecommunity.network.PostDto dto : resp.body().getData().getContent()) {
                    list.add(DtoConverter.toPost(dto));
                }
                return list;
            }
            return new ArrayList<Post>();
        }, new ApiHelper.Callback<List<Post>>() {
            @Override
            public void onSuccess(List<Post> data) {
                postList = data;
                postAdapter = new PostAdapter(getContext(), postList, new PostAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Post post) {
                        Intent intent = new Intent(getContext(), PostDetailActivity.class);
                        intent.putExtra("post_id", post.getId());
                        startActivity(intent);
                    }
                    @Override
                    public void onItemLongClick(Post post) {
                        showPostOptionsDialog(post);
                    }
                });
                rvPosts.setAdapter(postAdapter);
            }
            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "搜索失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPostOptionsDialog(Post post) {
        new AlertDialog.Builder(getContext())
            .setTitle(post.getTitle())
            .setItems(new String[]{"查看详情", "分享"}, (dialog, which) -> {
                if (which == 0) {
                    Intent intent = new Intent(getContext(), PostDetailActivity.class);
                    intent.putExtra("post_id", post.getId());
                    startActivity(intent);
                }
            })
            .show();
    }
}
