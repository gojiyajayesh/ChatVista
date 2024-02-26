package com.gojiyajayesh.chatvista.adepters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gojiyajayesh.chatvista.R;
import com.gojiyajayesh.chatvista.models.Users;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserChatListAdepter extends RecyclerView.Adapter<UserChatListAdepter.UserHolder> {
    ArrayList<Users> list;
   public  UserChatListAdepter(ArrayList<Users> list){
        this.list=list;
    }
    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view =inflater.inflate(R.layout.user_raw_sample_layout,parent,false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
                holder.lastMessage.setText(list.get(position).getLastMessage());
                holder.username.setText(list.get(position).getUsername());
//                holder.profilePicture.setImageResource(list.get(position).getProfile());
        Picasso.get().load(list.get(position).getProfileId()).placeholder(R.drawable.facebook).into(holder.profilePicture);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class UserHolder extends RecyclerView.ViewHolder {
        ImageView profilePicture;
        TextView username,lastMessage,unReadMessage,messageArrivalTime;
        public UserHolder(@NonNull View itemView) {
            super(itemView);
            profilePicture= itemView.findViewById(R.id.sampleProfilePicture);
            lastMessage= itemView.findViewById(R.id.sampleLastMessage);
            username= itemView.findViewById(R.id.sampleUsername);
            unReadMessage= itemView.findViewById(R.id.sampleUnReadMessage);
            messageArrivalTime= itemView.findViewById(R.id.sampleTime);
        }
    }
}
