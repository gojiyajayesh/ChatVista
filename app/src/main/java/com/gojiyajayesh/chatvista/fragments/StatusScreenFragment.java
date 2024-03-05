package com.gojiyajayesh.chatvista.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gojiyajayesh.chatvista.R;
import com.gojiyajayesh.chatvista.adapters.StatusListAdapter;
import com.gojiyajayesh.chatvista.adapters.UserListAdapter;
import com.gojiyajayesh.chatvista.models.UserStatusModel;

import java.util.ArrayList;


public class StatusScreenFragment extends Fragment {
    RecyclerView statusRecyclerView;
    ArrayList<UserStatusModel> list;
    public StatusScreenFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status_list, container, false);
        statusRecyclerView = view.findViewById(R.id.StatusListRecyclerView);
        list = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            UserStatusModel model = new UserStatusModel("jayesh", "Jayesh Ahir", "hello", "12:30 AM");
            list.add(model);
        }
        StatusListAdapter adapter = new StatusListAdapter(list, getContext());
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        GridLayoutManager layoutManager=new GridLayoutManager(getContext(),2);
        statusRecyclerView.setLayoutManager(layoutManager);
        statusRecyclerView.setAdapter(adapter);
        return view;
    }
}
