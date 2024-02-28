package com.gojiyajayesh.chatvista;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gojiyajayesh.chatvista.adepters.ChatMessageListAdepter;
import com.gojiyajayesh.chatvista.models.UserChatMessageModel;
import com.gojiyajayesh.chatvista.utils.AndroidUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class IndividualChatActivity extends AppCompatActivity {
    ImageButton videoCall, audioCall, menuList, backMain, sendMessage;
    de.hdodenhof.circleimageview.CircleImageView profilePicture;
    EditText message;
    Toolbar toolbar;
    String username;
    String profileId, SenderId, ReceiverId;
    TextView Username, status;
    FirebaseDatabase database;
    ArrayList<UserChatMessageModel> chatMessageModels = new ArrayList<>();
    RecyclerView recyclerView;
    private String SenderToReceiverNode, ReceiverToSenderNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_chat);
        initialization();
        Intent intent = getIntent();
        username = intent.getStringExtra("Username");
        Username.setText(username);
        ReceiverId = intent.getStringExtra("UserId");
        SenderId = FirebaseAuth.getInstance().getUid();
        // database node
        SenderToReceiverNode = SenderId + ReceiverId;
        ReceiverToSenderNode = ReceiverId + SenderId;
        //
        profileId = intent.getStringExtra("ProfilePicture");
        Picasso.get().load(profileId).placeholder(R.drawable.dwarkadhish).into(profilePicture);
        profilePicture.setOnClickListener(view -> {
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
            builder.setNegativeButton("Update", (dialog, which) -> {
                AndroidUtils.customToast(this, "Update now", Toast.LENGTH_LONG);
            });
            builder.setNeutralButton("Exit", (dialog, which) -> {
                dialog.dismiss();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        videoCall.setOnClickListener(view -> {
            AndroidUtils.customToast(this, "Video Call Is Start Now", 0);
        });
        audioCall.setOnClickListener(view -> {
            AndroidUtils.customToast(this, "Audio Call Is Start Now", 0);
        });
        menuList.setOnClickListener(view -> {
            AndroidUtils.customToast(this, "Menu Show Now", 0);
        });
        toolbar.setOnClickListener(view -> {
            AndroidUtils.customToast(this, "ToolBar is show", 0);
        });
        backMain.setOnClickListener(view -> {
            onBackPressed();
            finish();
        });
        ChatMessageListAdepter adepter = new ChatMessageListAdepter(chatMessageModels, getApplicationContext());
        recyclerView.setAdapter(adepter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        database.getReference().child("Chats").child(SenderToReceiverNode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatMessageModels.clear();
                for (DataSnapshot i : snapshot.getChildren()) {
                    UserChatMessageModel myMessageModel = i.getValue(UserChatMessageModel.class);
                    chatMessageModels.add(myMessageModel);
                }
                adepter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chatMessageModels.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recyclerView.scrollToPosition(adepter.getItemCount() - 1);
        sendMessage.setOnClickListener(view -> {
            String text = message.getText().toString().trim();
            if (!text.isEmpty()) {
                Long time = new Date().getTime();
                UserChatMessageModel dataStore = new UserChatMessageModel(text, SenderId, time);
                database.getReference().child("Chats").child(SenderToReceiverNode).push().setValue(dataStore).addOnSuccessListener(aVoid -> {
                    int length = SenderToReceiverNode.length();
                    int mid = length / 2;
                    String firstHalf = SenderToReceiverNode.substring(0, mid);
                    String secondHalf = SenderToReceiverNode.substring(mid);
                    boolean isEqual = firstHalf.equals(secondHalf);
                    if (!isEqual) {
                        database.getReference().child("Chats").child(ReceiverToSenderNode).push().setValue(dataStore).addOnSuccessListener(unused -> {

                        });
                    }
                });
                message.setText("");
            }
        });
    }

    void initialization() {
        videoCall = findViewById(R.id.videoCallButton);
        audioCall = findViewById(R.id.normalCallButton);
        menuList = findViewById(R.id.menuButton);
        sendMessage = findViewById(R.id.sendMessage);
        profilePicture = findViewById(R.id.sampleProfilePicture);
        Username = findViewById(R.id.ChatActivityUsername);
        status = findViewById(R.id.userChatStatus);
        backMain = findViewById(R.id.goBackMain);
        toolbar = findViewById(R.id.toolbar);
        message = findViewById(R.id.message);
        recyclerView = findViewById(R.id.MessageListRecyclerView);
        database = FirebaseDatabase.getInstance();
    }
}