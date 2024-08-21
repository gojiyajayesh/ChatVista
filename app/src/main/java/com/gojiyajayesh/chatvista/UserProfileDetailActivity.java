package com.gojiyajayesh.chatvista;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gojiyajayesh.chatvista.models.Users;
import com.gojiyajayesh.chatvista.utils.AndroidUtils;
import com.squareup.picasso.Picasso;

public class UserProfileDetailActivity extends AppCompatActivity {
    ImageView ProfilePicture;
    LinearLayout AudioCall, VideoCall, ShareProfile;
    TextView Username, FullName, About;
    String ProfileId;
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_detail);
        initialization();
        Users user = AndroidUtils.getPassedIntentData(getIntent());
        Username.setText("~ " + user.getUsername());
        FullName.setText(user.getFullName());
        ProfileId = user.getProfileId();
        Picasso.get().load(ProfileId).placeholder(R.drawable.default_profile_picture2).into(ProfilePicture);
        ProfilePicture.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(user.getUsername());
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View dialogView = inflater.inflate(R.layout.profile_picture_view, null);
            ImageView profilePictureClick = dialogView.findViewById(R.id.profilePictureClick);
            Picasso.get().load(ProfileId).placeholder(R.drawable.default_profile_picture2).into(profilePictureClick);
            builder.setView(dialogView);
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        back.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    void initialization() {
        ProfilePicture = findViewById(R.id.UserDetailActivityProfilePhoto);
        AudioCall = findViewById(R.id.UserDetailActivityAudioCall);
        VideoCall = findViewById(R.id.UserDetailActivityVideoCall);
        ShareProfile = findViewById(R.id.UserDetailActivityShare);
        Username = findViewById(R.id.UserDetailActivityUsername);
        FullName = findViewById(R.id.UserDetailActivityFullName);
        About = findViewById(R.id.UserDetailActivityAbout);
        back = findViewById(R.id.UserDetailActivityBack);
    }
}