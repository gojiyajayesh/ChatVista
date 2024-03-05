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

import com.gojiyajayesh.chatvista.adapters.ChatMessageListAdapter;
import com.gojiyajayesh.chatvista.models.UserAvailabilityModel;
import com.gojiyajayesh.chatvista.models.UserChatMessageModel;
import com.gojiyajayesh.chatvista.models.Users;
import com.gojiyajayesh.chatvista.utils.AndroidUtils;
import com.gojiyajayesh.chatvista.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class IndividualChatActivity extends AppCompatActivity {
    private final ArrayList<UserChatMessageModel> chatMessageModels = new ArrayList<>();
    private ImageButton videoCall, audioCall, menuList, backMain, sendMessage;
    private ImageView profilePicture;
    private EditText message;
    private Toolbar toolbar;
    private String username;
    private String profileId, senderId, receiverId;
    private TextView usernameTextView, statusTextView;
    private FirebaseDatabase database;
    private RecyclerView recyclerView;
    private String senderToReceiverNode, receiverToSenderNode, fullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_chat);
        initialization();
        Intent intent = getIntent();
        username = intent.getStringExtra("Username");
        receiverId = intent.getStringExtra("UserId");
        fullName = intent.getStringExtra("fullName");
        usernameTextView.setText(username);
        DatabaseReference availabilityRef = FirebaseDatabase.getInstance().getReference().child("Availability").child(receiverId);
        availabilityRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserAvailabilityModel userAvailabilityModel = snapshot.getValue(UserAvailabilityModel.class);
                    if (userAvailabilityModel != null) {
                        boolean isOnline = userAvailabilityModel.getIsOnline();
                        if(isOnline)
                        {
                            statusTextView.setText("Online");
                        }
                        else
                        {
                            statusTextView.setText("Offline");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
        senderId = FirebaseAuth.getInstance().getUid();
        // database node
        senderToReceiverNode = senderId + receiverId;
        receiverToSenderNode = receiverId + senderId;
        profileId = intent.getStringExtra("ProfilePicture");
        Picasso.get().load(profileId).placeholder(R.drawable.default_profile_picture).into(profilePicture);

        profilePicture.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(username);
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View dialogView = inflater.inflate(R.layout.profile_picture_view, null);
            ImageView profilePictureClick = dialogView.findViewById(R.id.profilePictureClick);
            Picasso.get().load(profileId).placeholder(R.drawable.default_profile_picture).into(profilePictureClick);
            builder.setView(dialogView);
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        videoCall.setOnClickListener(view -> AndroidUtils.customToast(this, "Video Call Is Start Now", Toast.LENGTH_SHORT));
        audioCall.setOnClickListener(view -> AndroidUtils.customToast(this, "Audio Call Is Start Now", Toast.LENGTH_SHORT));
        menuList.setOnClickListener(view -> AndroidUtils.customToast(this, "Menu Show Now", Toast.LENGTH_SHORT));
        toolbar.setOnClickListener(view -> {
            Intent intent2 = new Intent(IndividualChatActivity.this, UserProfileDetailActivity.class);
            Users user = new Users();
            user.setFullName(fullName);
            user.setUsername(username);
            user.setProfileId(profileId);
            AndroidUtils.setPassedIntentData(intent2,user);
            startActivity(intent2);
        });
        backMain.setOnClickListener(view -> {
            onBackPressed();
            finish();
        });

        ChatMessageListAdapter adapter = new ChatMessageListAdapter(chatMessageModels, getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        database.getReference().child("Chats").child(senderToReceiverNode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatMessageModels.clear();
                for (DataSnapshot i : snapshot.getChildren()) {
                    UserChatMessageModel myMessageModel = i.getValue(UserChatMessageModel.class);
                    chatMessageModels.add(myMessageModel);
                }
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chatMessageModels.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });

        recyclerView.scrollToPosition(adapter.getItemCount() - 1);

        sendMessage.setOnClickListener(view -> {
            String text = message.getText().toString().trim();
            if (!text.isEmpty()) {
                long time = new Date().getTime();
                UserChatMessageModel dataStore = new UserChatMessageModel(text, senderId, receiverId, time);
                database.getReference().child("Chats").child(senderToReceiverNode).push().setValue(dataStore).addOnSuccessListener(aVoid -> {
                    int length = senderToReceiverNode.length();
                    int mid = length / 2;
                    String firstHalf = senderToReceiverNode.substring(0, mid);
                    String secondHalf = senderToReceiverNode.substring(mid);
                    boolean isEqual = firstHalf.equals(secondHalf);
                    if (!isEqual) {
                        database.getReference().child("Chats").child(receiverToSenderNode).push().setValue(dataStore);
                    }
                });
                message.setText("");
            }
        });
    }

    private void initialization() {
        videoCall = findViewById(R.id.videoCallButton);
        audioCall = findViewById(R.id.normalCallButton);
        menuList = findViewById(R.id.menuButton);
        sendMessage = findViewById(R.id.sendMessage);
        profilePicture = findViewById(R.id.sampleProfilePicture);
        usernameTextView = findViewById(R.id.ChatActivityUsername);
        statusTextView = findViewById(R.id.userChatStatus);
        backMain = findViewById(R.id.goBackMain);
        toolbar = findViewById(R.id.toolbar);
        message = findViewById(R.id.message);
        recyclerView = findViewById(R.id.MessageListRecyclerView);
        database = FirebaseDatabase.getInstance();
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateUserStatus(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateUserStatus(false);
    }
    private void updateUserStatus(boolean connected) {
        UserAvailabilityModel userAvailabilityModel = new UserAvailabilityModel();
        userAvailabilityModel.setIsOnline(connected);
        FirebaseDatabase.getInstance().getReference()
                .child("Availability")
                .child(FirebaseUtils.currentUserId())
                .setValue(userAvailabilityModel);
    }
}
