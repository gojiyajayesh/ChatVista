package com.gojiyajayesh.chatvista.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gojiyajayesh.chatvista.R;
import com.gojiyajayesh.chatvista.adapters.UserListAdapter;
import com.gojiyajayesh.chatvista.adapters.VideoListAdapter;
import com.gojiyajayesh.chatvista.models.Users;
import com.gojiyajayesh.chatvista.utils.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CallScreenFragment extends Fragment {
    ArrayList<Users> userList = new ArrayList<>();
    VideoListAdapter adapter;
    RecyclerView recyclerView;

    public CallScreenFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        recyclerView = view.findViewById(R.id.chatListRecyclerView);
        adapter = new VideoListAdapter(userList, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        fetchUserList();

        return view;
    }
    private void fetchUserList() {
        String currentUserId = FirebaseUtils.currentUserId();
        if (currentUserId != null) {
            FirebaseDatabase.getInstance().getReference().child("UserConnections").child(currentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String userId = dataSnapshot.child("userId").getValue(String.class);
                        String username = dataSnapshot.child("username").getValue(String.class);
                        String fullName = dataSnapshot.child("fullName").getValue(String.class);
                        String profileId = dataSnapshot.child("profileId").getValue(String.class);
                        Users user = new Users(userId, username, fullName, profileId);
                        if (!userId.equals(currentUserId)) {
                            userList.add(user);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled
                }
            });
        }
    }
}