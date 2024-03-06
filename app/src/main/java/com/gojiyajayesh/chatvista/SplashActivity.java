package com.gojiyajayesh.chatvista;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.gojiyajayesh.chatvista.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    ProgressBar progressBar;
    Animation animation;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressBar = findViewById(R.id.progressBar);
        mAuth=FirebaseAuth.getInstance();
        startTaskSimulation();
    }

    private void startTaskSimulation() {
        progressBar.setVisibility(View.VISIBLE);
        animation = AnimationUtils.loadAnimation(this, R.anim.progress_bar_anim);
        progressBar.startAnimation(animation);
        new Handler().postDelayed(() -> {
            // Hide progress bar and stop animation
            progressBar.setVisibility(View.GONE);
            progressBar.clearAnimation();
            animation = null;
                startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                finish();
        }, 1200);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (animation != null) {
            animation.cancel();
            animation = null;
        }
    }
}
