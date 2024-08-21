package com.gojiyajayesh.chatvista.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gojiyajayesh.chatvista.R;
import com.gojiyajayesh.chatvista.models.Users;
import com.gojiyajayesh.chatvista.utils.FirebaseUtils;
import com.squareup.picasso.Picasso;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.util.ArrayList;
import java.util.Collections;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.UserHolder> { // Changed to VideoListAdapter.UserHolder
    private final ArrayList<Users> userList;
    private final Context context;

    public VideoListAdapter(ArrayList<Users> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.video_call_sample, parent, false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) { // Updated UserHolder reference
        Users user = userList.get(position);

        holder.username.setText(user.getUsername());
        Picasso.get().load(user.getProfileId()).placeholder(R.drawable.default_profile_picture2).into(holder.profilePicture);
        holder.videoCall.setIsVideoCall(true);
        holder.videoCall.setResourceID("zego_uikit_call"); // Please fill in the resource ID name that has been configured in the ZEGOCLOUD's console here.
        holder.videoCall.setInvitees(Collections.singletonList(new ZegoUIKitUser(user.getUserId(), user.getUsername())));

        holder.audioCall.setIsVideoCall(false);
        holder.audioCall.setResourceID("zego_uikit_call"); // Please fill in the resource ID name that has been configured in the ZEGOCLOUD's console here.
        holder.audioCall.setInvitees(Collections.singletonList(new ZegoUIKitUser(user.getUserId(), user.getUsername())));

        if (user.getUserId().equals(FirebaseUtils.currentUserId())) {
            holder.audioCall.setClickable(false);
            holder.videoCall.setClickable(false);
        }

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserHolder extends RecyclerView.ViewHolder {
        ImageView profilePicture;
        TextView username;
        ZegoSendCallInvitationButton videoCall, audioCall;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.videoSampleProfile);
            username = itemView.findViewById(R.id.videosampleUsername);
            videoCall = itemView.findViewById(R.id.videoCallBtn);
            audioCall = itemView.findViewById(R.id.callBtn);
        }
    }
}
