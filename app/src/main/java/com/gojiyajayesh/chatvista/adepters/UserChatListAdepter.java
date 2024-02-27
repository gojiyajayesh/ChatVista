package com.gojiyajayesh.chatvista.adepters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gojiyajayesh.chatvista.IndividualChatActivity;
import com.gojiyajayesh.chatvista.R;
import com.gojiyajayesh.chatvista.models.Users;
import com.gojiyajayesh.chatvista.utils.AndroidUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserChatListAdepter extends RecyclerView.Adapter<UserChatListAdepter.UserHolder> {
    private ArrayList<Users> list;
    private Context context;

    public UserChatListAdepter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.user_raw_sample_layout, parent, false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        holder.lastMessage.setText(list.get(position).getLastMessage()!=null?list.get(position).getLastMessage():"Hello");
        holder.username.setText(list.get(position).getUsername());
        Picasso.get().load(list.get(position).getProfileId()).placeholder(R.drawable.dwarkadhish).into(holder.profilePicture);
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, IndividualChatActivity.class);
            intent.putExtra("Username", list.get(position).getUsername());
            intent.putExtra("ProfilePicture", list.get(position).getProfileId());
            context.startActivity(intent);
        });

        holder.profilePicture.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(list.get(position).getUsername());
            LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
            View dialogView = inflater.inflate(R.layout.profile_picture_view, null);
            ImageView profilePictureClick = dialogView.findViewById(R.id.profilePictureClick);
            Picasso.get().load(list.get(position).getProfileId()).placeholder(R.drawable.dwarkadhish).into(profilePictureClick);
            builder.setView(dialogView);
            builder.setPositiveButton("OK", (dialog, which) -> {
                dialog.dismiss();
            });
            builder.setNegativeButton("Update",(dialog, which) -> {
                AndroidUtils.customToast(context,"Update now", Toast.LENGTH_LONG);
            });
            builder.setNeutralButton("Exit" ,(dialog, which) -> {
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
