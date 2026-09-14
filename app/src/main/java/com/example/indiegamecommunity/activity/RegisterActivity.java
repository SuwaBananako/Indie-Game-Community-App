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
import com.example.indiegamecommunity.network.RegisterRequest;

public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername, etPassword, etConfirmPassword;
    private CheckBox cbAdmin;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        cbAdmin = findViewById(R.id.cbAdmin);
        btnRegister = findViewById(R.id.btnRegister);
        TextView tvLogin = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "请填写所有信息", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 6) {
                Toast.makeText(this, "密码长度不能少于6位", Toast.LENGTH_SHORT).show();
                return;
            }

            btnRegister.setEnabled(false);

            ApiHelper.execute(() -> {
                retrofit2.Response<ApiResponse<String>> response =
                    ApiClient.getApiService().register(
                        new RegisterRequest(username, password, username, cbAdmin.isChecked())
                    ).execute();
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    return "ok";
                }
                throw new Exception(response.body() != null ? response.body().getMessage() : "注册失败");
            }, new ApiHelper.Callback<String>() {
                @Override
                public void onSuccess(String data) {
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                }
                @Override
                public void onError(String message) {
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                    btnRegister.setEnabled(true);
                }
            });
        });

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
