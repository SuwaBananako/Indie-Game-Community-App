package com.example.indiegamecommunity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.indiegamecommunity.R;
import com.example.indiegamecommunity.activity.VoteDetailActivity;
import com.example.indiegamecommunity.adapter.VoteAdapter;
import com.example.indiegamecommunity.database.UserProfileDatabaseHelper;
import com.example.indiegamecommunity.network.ApiClient;
import com.example.indiegamecommunity.network.ApiHelper;
import com.example.indiegamecommunity.network.DtoConverter;
import com.example.indiegamecommunity.model.Vote;
import com.example.indiegamecommunity.utils.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

public class VoteFragment extends Fragment {
    private RecyclerView rvVotes;
    private VoteAdapter voteAdapter;
    private UserProfileDatabaseHelper userProfileDatabaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vote, container, false);
        rvVotes = view.findViewById(R.id.rvVotes);

        SharedPreferencesHelper sp = new SharedPreferencesHelper(getContext());
        String username = sp.getUsername();
        userProfileDatabaseHelper = new UserProfileDatabaseHelper(getContext(), username);

        rvVotes.setLayoutManager(new LinearLayoutManager(getContext()));
        loadVotes();
        return view;
    }

    private void loadVotes() {
        ApiHelper.execute(() -> {
            retrofit2.Response<com.example.indiegamecommunity.network.ApiResponse<List<com.example.indiegamecommunity.network.VoteDto>>> resp =
                ApiClient.getApiService().getApprovedVotes().execute();
            if (resp.isSuccessful() && resp.body() != null && resp.body().getCode() == 200
                && resp.body().getData() != null) {
                List<Vote> list = new ArrayList<>();
                for (com.example.indiegamecommunity.network.VoteDto dto : resp.body().getData()) {
                    list.add(DtoConverter.toVote(dto));
                }
                return list;
            }
            return new ArrayList<Vote>();
        }, new ApiHelper.Callback<List<Vote>>() {
            @Override
            public void onSuccess(List<Vote> list) {
                voteAdapter = new VoteAdapter(getContext(), list,
                    vote -> {
                        if (userProfileDatabaseHelper.isVoted(vote.getId())) {
                            Toast.makeText(getContext(), "您已经投票过了", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ApiHelper.execute(() -> {
                            retrofit2.Response<com.example.indiegamecommunity.network.ApiResponse<String>> vr =
                                ApiClient.getApiService().doVote(vote.getId()).execute();
                            if (vr.isSuccessful() && vr.body() != null && vr.body().getCode() == 200) {
                                return "ok";
                            }
                            throw new Exception("投票失败");
                        }, new ApiHelper.Callback<String>() {
                            @Override
                            public void onSuccess(String data) {
                                userProfileDatabaseHelper.addVote(vote.getId());
                                Toast.makeText(getContext(), R.string.vote_success, Toast.LENGTH_SHORT).show();
                                loadVotes();
                            }
                            @Override
                            public void onError(String message) {
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    },
                    vote -> {
                        Intent intent = new Intent(getActivity(), VoteDetailActivity.class);
                        intent.putExtra("vote_id", vote.getId());
                        int rank = list.indexOf(vote) + 1;
                        intent.putExtra("rank", rank);
                        startActivity(intent);
                    }
                );
                rvVotes.setAdapter(voteAdapter);
            }
            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), "加载失败: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadVotes();
    }
}
