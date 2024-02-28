package com.gojiyajayesh.chatvista;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.gojiyajayesh.chatvista.adepters.MainAdepter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    ViewPager2 pager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pager = findViewById(R.id.viewPager2);
        tabLayout = findViewById(R.id.tabLayout);
        MainAdepter adepter = new MainAdepter(getSupportFragmentManager(), getLifecycle());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int ItemId=item.getItemId();
        if(ItemId==R.id.logoutAccount)
        {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this,SignInActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}