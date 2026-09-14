package com.example.indiegamecommunity.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.indiegamecommunity.R;
import com.example.indiegamecommunity.utils.SharedPreferencesHelper;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView tvSlogan = findViewById(R.id.tvSlogan);
        ProgressBar progressLoading = findViewById(R.id.progressLoading);

        // 阶段1: 文字淡入+放大 (0-1000ms)
        ObjectAnimator alphaIn = ObjectAnimator.ofFloat(tvSlogan, "alpha", 0f, 1f);
        alphaIn.setDuration(800);
        alphaIn.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(tvSlogan, "scaleX", 0.6f, 1.05f, 1f);
        scaleX.setDuration(800);
        scaleX.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(tvSlogan, "scaleY", 0.6f, 1.05f, 1f);
        scaleY.setDuration(800);
        scaleY.setInterpolator(new DecelerateInterpolator());

        alphaIn.start();
        scaleX.start();
        scaleY.start();

        // 阶段2: 进度条出现并填充 (800-2200ms)
        alphaIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator barAlpha = ObjectAnimator.ofFloat(progressLoading, "alpha", 0f, 1f);
                barAlpha.setDuration(300);
                barAlpha.start();

                ValueAnimator progressAnim = ValueAnimator.ofInt(0, 100);
                progressAnim.setDuration(1100);
                progressAnim.setInterpolator(new AccelerateDecelerateInterpolator());
                progressAnim.addUpdateListener(a ->
                        progressLoading.setProgress((int) a.getAnimatedValue()));
                progressAnim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        goToLogin();
                    }
                });
                progressAnim.start();
            }
        });
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
