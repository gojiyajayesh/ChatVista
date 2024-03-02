package com.gojiyajayesh.chatvista;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    ProgressBar progressBar;
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize progress bar
        progressBar = findViewById(R.id.progressBar);
        startTaskSimulation();
    }

    private void startTaskSimulation() {
        // Show progress bar and start animation
        progressBar.setVisibility(View.VISIBLE);
        animation = AnimationUtils.loadAnimation(this, R.anim.progress_bar_anim);
        progressBar.startAnimation(animation);

        // Simulate task delay using Handler
        new Handler().postDelayed(() -> {
            // Hide progress bar and stop animation
            progressBar.setVisibility(View.GONE);
            progressBar.clearAnimation();
            animation = null;

            // Start the SignInActivity
            startActivity(new Intent(SplashActivity.this, SignInActivity.class));
            finish();
        }, 1200);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel animation to prevent memory leaks
        if (animation != null) {
            animation.cancel();
            animation = null;
        }
    }
}
