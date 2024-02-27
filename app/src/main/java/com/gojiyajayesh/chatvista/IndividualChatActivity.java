package com.gojiyajayesh.chatvista;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gojiyajayesh.chatvista.utils.AndroidUtils;
import com.squareup.picasso.Picasso;

public class IndividualChatActivity extends AppCompatActivity {
ImageButton videoCall,audioCall,menuList,backMain,sendMessage;
de.hdodenhof.circleimageview.CircleImageView profilePicture;
EditText message;
Toolbar toolbar;
TextView Username,status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_chat);
        initialization();
        Intent intent=getIntent();
        String username=intent.getStringExtra("Username");
        Username.setText(username);
        String profileId=intent.getStringExtra("ProfilePicture");
        Picasso.get().load(profileId).placeholder(R.drawable.dwarkadhish).into(profilePicture);
        profilePicture.setOnClickListener(view->{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(username);
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View dialogView = inflater.inflate(R.layout.profile_picture_view, null);
            ImageView profilePictureClick = dialogView.findViewById(R.id.profilePictureClick);
            Picasso.get().load(profileId).placeholder(R.drawable.dwarkadhish).into(profilePictureClick);
            builder.setView(dialogView);
            builder.setPositiveButton("OK", (dialog, which) -> {
                dialog.dismiss();
            });
            builder.setNegativeButton("Update",(dialog, which) -> {
                AndroidUtils.customToast(this,"Update now", Toast.LENGTH_LONG);
            });
            builder.setNeutralButton("Exit" ,(dialog, which) -> {
                dialog.dismiss();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        videoCall.setOnClickListener(view->{
            AndroidUtils.customToast(this,"Video Call Is Start Now",0);
        });
        audioCall.setOnClickListener(view->{
            AndroidUtils.customToast(this,"Audio Call Is Start Now",0);
        });
        menuList.setOnClickListener(view->{
            AndroidUtils.customToast(this,"Menu Show Now",0);
        });
        toolbar.setOnClickListener(view->{
            AndroidUtils.customToast(this,"ToolBar is show",0);
        });
        backMain.setOnClickListener(view->{
            onBackPressed();
           finish();
        });
    }
    void initialization(){
        videoCall=findViewById(R.id.videoCallButton);
        audioCall=findViewById(R.id.normalCallButton);
        menuList=findViewById(R.id.menuButton);
        profilePicture=findViewById(R.id.sampleProfilePicture);
        Username=findViewById(R.id.ChatActivityUsername);
        status=findViewById(R.id.userChatStatus);
        backMain=findViewById(R.id.goBackMain);
        toolbar=findViewById(R.id.toolbar);
    }
}