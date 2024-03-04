package com.gojiyajayesh.chatvista.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gojiyajayesh.chatvista.IndividualChatActivity;
import com.gojiyajayesh.chatvista.R;
import com.gojiyajayesh.chatvista.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UserChatListAdapter extends RecyclerView.Adapter<UserChatListAdapter.UserHolder> {
    private final ArrayList<Users> list;
    private final Context context;
    private final SimpleDateFormat sdf;

    public UserChatListAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
        this.sdf = new SimpleDateFormat("hh:mm a");
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.user_list_sample_layout, parent, false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        Users users = list.get(position);
        FirebaseDatabase.getInstance().getReference().child("Chats").child(FirebaseAuth.getInstance().getUid() + users.getUserId()).orderByChild("timestamp").limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot i : snapshot.getChildren()) {
                        holder.lastMessage.setText(i.child("message").getValue(String.class));
                        String time = sdf.format(new Date(i.child("messageTime").getValue(Long.class)));
                        holder.messageArrivalTime.setText(time);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.username.setText(users.getFullName());
        Picasso.get().load(users.getProfileId()).placeholder(R.drawable.default_profile_picture).into(holder.profilePicture);
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, IndividualChatActivity.class);
            intent.putExtra("UserId", users.getUserId());
            intent.putExtra("Username", users.getUsername());
            intent.putExtra("ProfilePicture", users.getProfileId());
            intent.putExtra("fullName",users.getFullName());
            context.startActivity(intent);
        });

        holder.profilePicture.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(users.getUsername());
            LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
            View dialogView = inflater.inflate(R.layout.profile_picture_view, null);
            ImageView profilePictureClick = dialogView.findViewById(R.id.profilePictureClick);
            Picasso.get().load(users.getProfileId()).placeholder(R.drawable.default_profile_picture).into(profilePictureClick);
            builder.setView(dialogView);
            builder.setPositiveButton("OK", (dialog, which) -> {
                dialog.dismiss();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class UserHolder extends RecyclerView.ViewHolder {
        ImageView profilePicture;
        TextView username, lastMessage, unReadMessage, messageArrivalTime;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.sampleProfilePicture);
            lastMessage = itemView.findViewById(R.id.sampleLastMessage);
            username = itemView.findViewById(R.id.sampleUsername);
            unReadMessage = itemView.findViewById(R.id.sampleUnReadMessage);
            messageArrivalTime = itemView.findViewById(R.id.sampleTime);
        }
    }
}
