package com.gojiyajayesh.chatvista;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

public class SplashActivity extends AppCompatActivity {
ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressBar = findViewById(R.id.progressBar);
        simulateTask();
    }
    private void simulateTask() {
        progressBar.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.progress_bar_anim);
        progressBar.startAnimation(anim);
        new Handler().postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            progressBar.clearAnimation();
            startActivity(new Intent(SplashActivity.this, SignInActivity.class));
            finish();
        }, 1500);
    }
}