package com.gojiyajayesh.chatvista.adepters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gojiyajayesh.chatvista.R;
import com.gojiyajayesh.chatvista.models.UserChatMessageModel;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class ChatMessageListAdepter extends RecyclerView.Adapter {
    private final int SENDER_FLAG = 1;
    private final int RECEIVER_FLAG = 2;
    SimpleDateFormat sdf;
    ArrayList<UserChatMessageModel> list;
    Context context;

    public ChatMessageListAdepter(ArrayList<UserChatMessageModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_FLAG) {
            View view = LayoutInflater.from(context).inflate(R.layout.adepter_sample_sender_layout, parent, false);
            return new SenderViewHolder(view);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.adepter_sample_receiver_layout, parent, false);
        return new ReceiverViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (Objects.equals(list.get(position).getUserId(), FirebaseAuth.getInstance().getUid()))
            return SENDER_FLAG;
        return RECEIVER_FLAG;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserChatMessageModel model = list.get(position);
        if (holder.getClass() == SenderViewHolder.class) {
            ((SenderViewHolder) holder).Message.setText(model.getMessage());
            sdf = new SimpleDateFormat("hh:mm a");
            String time = sdf.format(new Date(model.getMessageTime()));
            ((SenderViewHolder) holder).Time.setText(time);
        } else {
            ((ReceiverViewHolder) holder).Message.setText(model.getMessage());
            sdf = new SimpleDateFormat("hh:mm a");
            String time = sdf.format(new Date(model.getMessageTime()));
            ((ReceiverViewHolder) holder).Time.setText(time);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView Message, Time;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            Message = itemView.findViewById(R.id.senderMessageText);
            Time = itemView.findViewById(R.id.senderMessageTime);
        }
    }

    public static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView Message, Time;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            Message = itemView.findViewById(R.id.receiverMessageText);
            Time = itemView.findViewById(R.id.recieverMessageTime);
        }
    }
}
