package com.gojiyajayesh.chatvista.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.gojiyajayesh.chatvista.fragments.CallScreenFragment;
import com.gojiyajayesh.chatvista.fragments.ChatScreenFragment;
import com.gojiyajayesh.chatvista.fragments.StatusScreenFragment;

public class MainAdapter extends FragmentStateAdapter {

    public MainAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position==0)
            return new ChatScreenFragment();
        else if(position==1)
            return new StatusScreenFragment();
        else
            return new CallScreenFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
