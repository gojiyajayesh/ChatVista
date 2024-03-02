package com.gojiyajayesh.chatvista.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gojiyajayesh.chatvista.R;
import com.gojiyajayesh.chatvista.adapters.UserChatListAdapter;
import com.gojiyajayesh.chatvista.models.Users;
import com.gojiyajayesh.chatvista.utils.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatsListFragment extends Fragment {
    ArrayList<Users> list = new ArrayList<>();
    FirebaseDatabase database;
    RecyclerView recyclerView;

    public ChatsListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        recyclerView = view.findViewById(R.id.chatListRecyclerView);
        database = FirebaseDatabase.getInstance();
        UserChatListAdapter adapter=new UserChatListAdapter(list,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        String currentUserId = FirebaseUtils.currentUserId();
        if (currentUserId != null) {
            database.getReference().child("UserConnections").child(currentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String userId = dataSnapshot.child("userId").getValue(String.class);
                        String username = dataSnapshot.child("username").getValue(String.class);
                        String fullName = dataSnapshot.child("fullName").getValue(String.class);
                        String profileId=dataSnapshot.child("profileId").getValue(String.class);
                        Users users = new Users();
                        users.setUserId(userId);
                        users.setUsername(username);
                        users.setFullName(fullName);
                        users.setProfileId(profileId);
                        if (users.getUserId().equals(currentUserId)) {
                            users.setUsername(username+" (Me)");
                        }
                        list.add(users);
                    }
                    adapter.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        return view;
    }
}
