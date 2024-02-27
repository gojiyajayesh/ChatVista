package com.gojiyajayesh.chatvista.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gojiyajayesh.chatvista.R;
import com.gojiyajayesh.chatvista.adepters.UserChatListAdepter;
import com.gojiyajayesh.chatvista.models.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
public class ChatsListFragment extends Fragment {
    ArrayList<Users> list=new ArrayList<>();
    FirebaseDatabase database;
    RecyclerView rcl;
    public ChatsListFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_chat_list, container, false);
        rcl=view.findViewById(R.id.chatListRecyclerView);
        database=FirebaseDatabase.getInstance();
        UserChatListAdepter adepter=new UserChatListAdepter(list,getContext());
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        rcl.setLayoutManager(layoutManager);
        rcl.setAdapter(adepter);
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Users users=dataSnapshot.getValue(Users.class);
                    assert users != null;
                    users.setUserId(dataSnapshot.getKey());
                    list.add(users);
                }
                adepter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}