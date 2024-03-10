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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserHolder> {
    private final ArrayList<Users> userList;
    private final Context context;
    private final SimpleDateFormat sdf;

    public UserListAdapter(ArrayList<Users> userList, Context context) {
        this.userList = userList;
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
        Users user = userList.get(position);

        holder.username.setText(user.getUsername());
        holder.lastMessage.setText(user.getLastMessage());

        // Check if lastMessageTime is not null before formatting
        if (user.getLastMessageTime() != null) {
            holder.messageArrivalTime.setText(sdf.format(new Date(user.getLastMessageTime())));
        } else {
            holder.messageArrivalTime.setText("No message time");
            holder.lastMessage.setText("Tap to chat");
        }

        Picasso.get().load(user.getProfileId()).placeholder(R.drawable.default_profile_picture).into(holder.profilePicture);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, IndividualChatActivity.class);
            intent.putExtra("UserId", user.getUserId());
            intent.putExtra("Username", user.getUsername());
            intent.putExtra("ProfilePicture", user.getProfileId());
            intent.putExtra("fullName", user.getFullName());
            context.startActivity(intent);
        });

        holder.profilePicture.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(user.getUsername());
            LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
            View dialogView = inflater.inflate(R.layout.profile_picture_view, null);
            ImageView profilePictureClick = dialogView.findViewById(R.id.profilePictureClick);
            Picasso.get().load(user.getProfileId()).placeholder(R.drawable.default_profile_picture).into(profilePictureClick);
            builder.setView(dialogView);
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
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
