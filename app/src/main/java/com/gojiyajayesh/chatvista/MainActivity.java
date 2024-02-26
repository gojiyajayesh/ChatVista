package com.gojiyajayesh.chatvista;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.gojiyajayesh.chatvista.adepters.MainWorkAdepter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {
    ViewPager2 pager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pager = findViewById(R.id.viewPager2);
        tabLayout = findViewById(R.id.tabLayout);
        MainWorkAdepter adepter = new MainWorkAdepter(getSupportFragmentManager(), getLifecycle());
        pager.setAdapter(adepter);
        new TabLayoutMediator(tabLayout, pager, (tab, position) -> {
            if (position == 0)
                tab.setText("Chat");
            else if (position == 1)
                tab.setText("Status");
            else
                tab.setText("Calls");
        }).attach();
    }
}