package com.example.indiegamecommunity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.indiegamecommunity.R;
import com.example.indiegamecommunity.database.UserDatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminUserFragment extends Fragment {
    private RecyclerView rvUsers;
    private UserDatabaseHelper userDatabaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_users, container, false);
        rvUsers = view.findViewById(R.id.rvUsers);
        userDatabaseHelper = new UserDatabaseHelper(getContext());
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        loadUsers();
        return view;
    }

    private void loadUsers() {
        List<String[]> users = userDatabaseHelper.getAllUsers();
        rvUsers.setAdapter(new RecyclerView.Adapter<ViewHolder>() {
            @NonNull @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_promo, parent, false);
                return new ViewHolder(v);
            }
            @Override
            public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
                String[] u = users.get(pos);
                boolean isAdmin = "1".equals(u[2]);
                h.tvName.setText(u[0] + (isAdmin ? " [管理员]" : ""));
                String time = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(Long.parseLong(u[3])));
                h.tvDesc.setText("昵称: " + u[1] + "  注册: " + time);
                h.itemView.setOnClickListener(v -> showUserMenu(u[0], isAdmin));
            }
            @Override public int getItemCount() { return users.size(); }
        });
    }

    private void showUserMenu(String username, boolean isAdmin) {
        new AlertDialog.Builder(getContext())
                .setTitle("管理用户: " + username)
                .setItems(new String[]{
                    isAdmin ? "取消管理员" : "设为管理员",
                    "删除用户"
                }, (dialog, which) -> {
                    if (which == 0) {
                        userDatabaseHelper.setAdminStatus(username, !isAdmin);
                        loadUsers();
                    } else if (which == 1) {
                        new AlertDialog.Builder(getContext())
                                .setTitle("确认删除")
                                .setMessage("确定要删除用户 " + username + " 吗？")
                                .setPositiveButton("删除", (d, w) -> {
                                    userDatabaseHelper.deleteUser(username);
                                    loadUsers();
                                })
                                .setNegativeButton("取消", null)
                                .show();
                    }
                })
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUsers();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc;
        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvGameName);
            tvDesc = v.findViewById(R.id.tvGameDescription);
        }
    }
}
