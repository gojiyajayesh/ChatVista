package com.gojiyajayesh.chatvista.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.gojiyajayesh.chatvista.R;

public class CallScreenFragment extends Fragment {
    public CallScreenFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calls_list, container, false);
    }
}