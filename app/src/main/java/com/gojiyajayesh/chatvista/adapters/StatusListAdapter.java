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
import com.gojiyajayesh.chatvista.models.UserStatusModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StatusListAdapter extends RecyclerView.Adapter<StatusListAdapter.StatusViewHolder> {
    ArrayList<UserStatusModel> userStatusModel;
    Context context;

    public StatusListAdapter(ArrayList<UserStatusModel> userStatusModel,Context context) {
        this.userStatusModel = userStatusModel;
        this.context=context;
    }

    public StatusListAdapter() {
    }

    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.status_sample_raw_layout, parent, false);
        return new StatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder holder, int position) {
           UserStatusModel model=userStatusModel.get(position);
           holder.Username.setText(model.getUsername());
           holder.LastStatusUpdateTime.setText(model.getStatusUpdateTime());
        Picasso.get().load(model.getProfileId()).placeholder(R.drawable.default_profile_picture).into(holder.ProfilePic);
    }

    @Override
    public int getItemCount() {
        return userStatusModel.size();
    }

    public static class StatusViewHolder extends RecyclerView.ViewHolder {
        ImageView ProfilePic;
        TextView Username, LastStatusUpdateTime;

        public StatusViewHolder(@NonNull View itemView) {
            super(itemView);
            ProfilePic = itemView.findViewById(R.id.StatusProfilePhoto);
            Username = itemView.findViewById(R.id.StatusUsername);
            LastStatusUpdateTime = itemView.findViewById(R.id.StatusLastUpdate);
        }
    }
}
