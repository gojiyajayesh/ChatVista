package com.gojiyajayesh.chatvista;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StatusViewActivity extends AppCompatActivity {
    ImageView StatusPhoto, ProfilePhoto;
    TextView lastUpdateTime, Username;
    ProgressBar progressBar;
    Animation animation;
    ImageButton back;
    SimpleDateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_view);
        StatusPhoto = findViewById(R.id.StatusViewImage);
        lastUpdateTime = findViewById(R.id.LastStatusUpdateTime);
        progressBar = findViewById(R.id.StatusprogressBar);
        ProfilePhoto = findViewById(R.id.StatusProfilePicture);
        Username = findViewById(R.id.StatusUsername);
        back = findViewById(R.id.StatusgoBackMain);
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        Username.setText(username);
        String profileId = intent.getStringExtra("profileId");
        Picasso.get().load(profileId).placeholder(R.drawable.default_profile_picture2).into(ProfilePhoto);
        back.setOnClickListener(view -> {
            onBackPressed();
            finish();
        });
        String StatusId = intent.getStringExtra("statusUrl");
        Picasso.get().load(StatusId).placeholder(R.drawable.default_profile_picturestatus).into(StatusPhoto);
        Long Time = getIntent().getLongExtra("lastStatusUpdateTime", 0L);
        sdf = new SimpleDateFormat("hh:mm a");
        String time = sdf.format(new Date(Time));
        lastUpdateTime.setText("Status Update time " + time);
        startTaskSimulation();
    }

    private void startTaskSimulation() {
        progressBar.setVisibility(View.VISIBLE);
        animation = AnimationUtils.loadAnimation(this, R.anim.status_progress_bar);
        progressBar.startAnimation(animation);
        new Handler().postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            progressBar.clearAnimation();
            animation = null;
            finish();
        }, 12100);
    }
}