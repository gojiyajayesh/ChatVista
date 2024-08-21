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

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gojiyajayesh.chatvista.IndividualChatActivity;
import com.gojiyajayesh.chatvista.R;
import com.gojiyajayesh.chatvista.models.Users;
import com.gojiyajayesh.chatvista.utils.AndroidUtils;
import com.gojiyajayesh.chatvista.utils.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UserSearchAdapter extends FirestoreRecyclerAdapter<Users, UserSearchAdapter.UserViewHolder> {
    private static Context context = null;

    public UserSearchAdapter(@NonNull FirestoreRecyclerOptions<Users> options, Context context) {
        super(options);
        UserSearchAdapter.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull Users model) {
        holder.username.setText(model.getUsername());
        holder.fullName.setText(model.getFullName());
        Picasso.get().load(model.getProfileId()).placeholder(R.drawable.default_profile_picture2).into(holder.profilePicture);
        if (model.getUserId().equals(FirebaseUtils.currentUserId())) {
            holder.username.setText(model.getUsername() + " (You)");
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, IndividualChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            database.getReference().child("UserConnections").child(FirebaseUtils.currentUserId()).child(model.getUserId()).setValue(model);
            DatabaseReference userRef = database.getReference().child("Users").child(FirebaseUtils.currentUserId());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Users currentUser = dataSnapshot.getValue(Users.class);
                    if (currentUser != null) {
                        database.getReference().child("UserConnections").child(model.getUserId()).child(FirebaseUtils.currentUserId()).setValue(currentUser);
                    }
                    AndroidUtils.setPassedIntentData(intent, model);
                    context.startActivity(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        });
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_recycler_sample_layout, parent, false);
        return new UserViewHolder(view);
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePicture;
        TextView fullName, username;
        FirebaseDatabase database;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.searchSampleProfilePicture);
            username = itemView.findViewById(R.id.searchSampleUsername);
            fullName = itemView.findViewById(R.id.SearchSampleFullName);
            database = FirebaseDatabase.getInstance();
        }
    }
}
