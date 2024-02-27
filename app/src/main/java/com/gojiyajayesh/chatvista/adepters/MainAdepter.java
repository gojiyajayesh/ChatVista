package com.gojiyajayesh.chatvista.adepters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.gojiyajayesh.chatvista.fragments.CallsListFragment;
import com.gojiyajayesh.chatvista.fragments.ChatsListFragment;
import com.gojiyajayesh.chatvista.fragments.StatusesListFragment;

public class MainAdepter extends FragmentStateAdapter {

    public MainAdepter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position==0)
            return new ChatsListFragment();
        else if(position==1)
            return new StatusesListFragment();
        else
            return new CallsListFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
