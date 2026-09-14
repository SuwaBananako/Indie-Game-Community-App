package com.example.indiegamecommunity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.indiegamecommunity.R;
import com.example.indiegamecommunity.network.ApiClient;
import com.example.indiegamecommunity.network.ApiHelper;
import com.example.indiegamecommunity.network.ApiResponse;
import com.example.indiegamecommunity.network.LoginRequest;
import com.example.indiegamecommunity.network.LoginResponse;
import com.example.indiegamecommunity.utils.SharedPreferencesHelper;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private CheckBox cbAdmin;
    private Button btnLogin;
    private SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferencesHelper = new SharedPreferencesHelper(this);
        ApiClient.init(() -> sharedPreferencesHelper.getToken());

        if (sharedPreferencesHelper.isLoggedIn()) {
            startMainOrAdmin();
            return;
        }

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        cbAdmin = findViewById(R.id.cbAdmin);
        btnLogin = findViewById(R.id.btnLogin);
        TextView tvRegister = findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "请填写用户名和密码", Toast.LENGTH_SHORT).show();
                return;
            }

            btnLogin.setEnabled(false);

            ApiHelper.execute(() -> {
                retrofit2.Response<ApiResponse<LoginResponse>> response =
                    ApiClient.getApiService().login(new LoginRequest(username, password)).execute();
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    return response.body().getData();
                }
                throw new Exception("用户名或密码错误");
            }, new ApiHelper.Callback<LoginResponse>() {
                @Override
                public void onSuccess(LoginResponse data) {
                    if (cbAdmin.isChecked() && !data.isAdmin) {
                        Toast.makeText(LoginActivity.this, "该账号不是管理员", Toast.LENGTH_SHORT).show();
                        btnLogin.setEnabled(true);
                        return;
                    }
                    sharedPreferencesHelper.saveLoginInfo(
                        data.getUsername(), data.getToken(), data.getNickname(), data.getAvatar(), data.isAdmin
                    );
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    if (cbAdmin.isChecked() && data.isAdmin) {
                        startAdminActivity();
                    } else {
                        startMainActivity();
                    }
                }
                @Override
                public void onError(String message) {
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    btnLogin.setEnabled(true);
                }
            });
        });

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });
    }

    private void startMainOrAdmin() {
        if (sharedPreferencesHelper.isAdmin()) startAdminActivity(); else startMainActivity();
    }
    private void startMainActivity() { startActivity(new Intent(this, MainActivity.class)); finish(); }
    private void startAdminActivity() { startActivity(new Intent(this, AdminActivity.class)); finish(); }
}
