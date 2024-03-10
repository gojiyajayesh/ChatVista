package com.gojiyajayesh.chatvista;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.gojiyajayesh.chatvista.adapters.MainAdapter;
import com.gojiyajayesh.chatvista.models.UserAvailabilityModel;
import com.gojiyajayesh.chatvista.utils.AndroidUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager2);
        tabLayout = findViewById(R.id.tabLayout);
        mAuth = FirebaseAuth.getInstance();

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
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            UserAvailabilityModel userAvailabilityModel = new UserAvailabilityModel();
            userAvailabilityModel.setIsOnline(connected);
            FirebaseDatabase.getInstance().getReference().child("Availability").child(userId).setValue(userAvailabilityModel);
        } else {
            // Handle the case where the user is not authenticated or currentUser is null
            Log.e("MainActivity", "User not authenticated");
        }
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
            updateUserStatus(false);
            mAuth.signOut();
            AndroidUtils.customToast(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT);
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
