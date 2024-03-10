package com.gojiyajayesh.chatvista.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gojiyajayesh.chatvista.R;
import com.gojiyajayesh.chatvista.models.UserChatMessageModel;
import com.gojiyajayesh.chatvista.utils.AndroidUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class ChatMessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int SENDER_FLAG = 1;
    private final int RECEIVER_FLAG = 2;
    SimpleDateFormat sdf;
    ArrayList<UserChatMessageModel> list;
    Context context;

    public ChatMessageListAdapter(ArrayList<UserChatMessageModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_FLAG) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adepter_sample_sender_layout, parent, false);
            return new SenderViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adepter_sample_receiver_layout, parent, false);
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
        if (holder instanceof SenderViewHolder) {
            ((SenderViewHolder) holder).Message.setText(model.getMessage());
            sdf = new SimpleDateFormat("hh:mm a");
            String time = sdf.format(new Date(model.getMessageTime()));
            ((SenderViewHolder) holder).Time.setText(time);
            ((SenderViewHolder) holder).itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                    builder.setTitle("Delete message");
                    builder.setMessage("Are you sure you want to delete this message?");
                    builder.setNegativeButton("Delete for me", (dialog, which) -> {
                        DatabaseReference chatRef = (DatabaseReference) FirebaseDatabase.getInstance().getReference().child("Chats").child(FirebaseAuth.getInstance().getUid() + model.getReceiverId());
                        Query query = chatRef.orderByChild("message").equalTo(model.getMessage());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                                    long messageTime = messageSnapshot.child("messageTime").getValue(Long.class);
                                    if (model.getMessageTime() == messageTime) {
                                        messageSnapshot.getRef().removeValue().addOnSuccessListener(aVoid -> {
                                        }).addOnFailureListener(e -> {
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle potential errors
                            }
                        });
                    });
                    builder.setPositiveButton("Delete for everyone", (dialog, which) -> {
                        DatabaseReference chatRef = (DatabaseReference) FirebaseDatabase.getInstance().getReference().child("Chats").child(FirebaseAuth.getInstance().getUid() + model.getReceiverId());
                        Query query = chatRef.orderByChild("message").equalTo(model.getMessage());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                                    long messageTime = messageSnapshot.child("messageTime").getValue(Long.class);
                                    if (model.getMessageTime() == messageTime) {
                                        messageSnapshot.getRef().removeValue().addOnSuccessListener(aVoid -> {
                                        }).addOnFailureListener(e -> {
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle potential errors
                            }
                        });
                        DatabaseReference chatRef2 = (DatabaseReference) FirebaseDatabase.getInstance().getReference().child("Chats").child(model.getReceiverId() + FirebaseAuth.getInstance().getUid());
                        Query query2 = chatRef2.orderByChild("message").equalTo(model.getMessage());
                        query2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                                    long messageTime = messageSnapshot.child("messageTime").getValue(Long.class);
                                    if (model.getMessageTime() == messageTime) {
                                        messageSnapshot.getRef().removeValue().addOnSuccessListener(aVoid -> {
                                        }).addOnFailureListener(e -> {
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle potential errors
                            }
                        });
                    });
                    builder.setNeutralButton("Exit", (dialog, which) -> {
                        AndroidUtils.customToast(context, "Exit", 1);
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
            });
        } else if (holder instanceof ReceiverViewHolder) {
            ((ReceiverViewHolder) holder).Message.setText(model.getMessage());
            sdf = new SimpleDateFormat("hh:mm a");
            String time = sdf.format(new Date(model.getMessageTime()));
            ((ReceiverViewHolder) holder).Time.setText(time);
            ((ReceiverViewHolder) holder).itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                    builder.setTitle("Delete message");
                    builder.setMessage("Are you sure you want to delete this message?");
                    builder.setPositiveButton("Delete for me", (dialog, which) -> {
//                        AndroidUtils.customToast(context, "Delete me", 1);
                        DatabaseReference chatRef = (DatabaseReference) FirebaseDatabase.getInstance().getReference().child("Chats").child(FirebaseAuth.getInstance().getUid() + model.getUserId());
                        Query query = chatRef.orderByChild("message").equalTo(model.getMessage());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                                    long messageTime = messageSnapshot.child("messageTime").getValue(Long.class);
                                    if (model.getMessageTime() == messageTime) {
                                        messageSnapshot.getRef().removeValue().addOnSuccessListener(aVoid -> {
                                        }).addOnFailureListener(e -> {
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle potential errors
                            }
                        });
                    });
                    builder.setNegativeButton("Exit", (dialog, which) -> {
                        AndroidUtils.customToast(context, "Exit", 1);
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
            });
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
