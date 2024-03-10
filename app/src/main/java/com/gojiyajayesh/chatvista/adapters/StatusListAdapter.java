package com.gojiyajayesh.chatvista.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gojiyajayesh.chatvista.R;
import com.gojiyajayesh.chatvista.StatusViewActivity;
import com.gojiyajayesh.chatvista.models.Users;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StatusListAdapter extends RecyclerView.Adapter<StatusListAdapter.StatusViewHolder> {
    ArrayList<Users> userStatusModel;
    Context context;
    SimpleDateFormat sdf;

    public StatusListAdapter(ArrayList<Users> userStatusModel, Context context) {
        this.userStatusModel = userStatusModel;
        this.context = context;
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
        Users model = userStatusModel.get(position);
        holder.Username.setText(model.getUsername());
        sdf = new SimpleDateFormat("hh:mm a");
        String time = sdf.format(new Date(model.getLastStatusUpdateTime() != null ? model.getLastStatusUpdateTime() : 1219021212L));
        holder.LastStatusUpdateTime.setText(time);
        Picasso.get().load(model.getProfileId()).placeholder(R.drawable.default_profile_picture).into(holder.ProfilePic);
        holder.itemView.setOnClickListener(view -> {
            String Username = model.getUsername();
            String ProfileId = model.getProfileId();
            String StatusUrl = model.getStatusUrl();
            Long lastTime = model.getLastStatusUpdateTime();
            Intent in = new Intent(context, StatusViewActivity.class);
            in.putExtra("username", Username);
            in.putExtra("profileId", ProfileId);
            in.putExtra("statusUrl", StatusUrl);
            in.putExtra("lastStatusUpdateTime", lastTime);
            context.startActivity(in);

        });
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
