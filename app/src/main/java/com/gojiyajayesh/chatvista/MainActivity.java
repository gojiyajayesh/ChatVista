package com.gojiyajayesh.chatvista;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.gojiyajayesh.chatvista.adapters.MainAdapter;
import com.gojiyajayesh.chatvista.models.UserAvailabilityModel;
import com.gojiyajayesh.chatvista.utils.FirebaseUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager2);
        tabLayout = findViewById(R.id.tabLayout);

        MainAdapter adapter = new MainAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Chat");
                    break;
                case 1:
                    tab.setText("Status");
                    break;
                case 2:
                    tab.setText("Calls");
                    break;
            }
        }).attach();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserStatus(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateUserStatus(false);
    }

    private void updateUserStatus(boolean connected) {
        UserAvailabilityModel userAvailabilityModel = new UserAvailabilityModel();
        userAvailabilityModel.setIsOnline(connected);
        FirebaseDatabase.getInstance().getReference()
                .child("Availability")
                .child(FirebaseUtils.currentUserId())
                .setValue(userAvailabilityModel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.logoutAccount) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
            return true;
        } else if (itemId == R.id.search) {
            startActivity(new Intent(MainActivity.this, SearchUserActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
