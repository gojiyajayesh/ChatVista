package com.gojiyajayesh.chatvista;

import android.app.Application;
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
import com.gojiyajayesh.chatvista.utils.FirebaseUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;

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
        startServiceForZegoCloud(FirebaseUtils.currentUserId(),FirebaseUtils.currentUserName());
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

    void startServiceForZegoCloud(String UID,String un) {

        //this method is set user name with one id and we can identify individual user with his userId.

        Application application = getApplication(); // Android's application context
        long appID = 1381029436;   // yourAppID
        String appSign = "105aeb6aed2d2a0edb9c00a446c340821bffea42ccecb4b35bf9c17ef44797b1";  // yourAppSign
        String userID = UID; // yourUserID, userID should only contain numbers, English characters, and '_'.
        String userName = un;   // yourUserName

        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();

        ZegoUIKitPrebuiltCallService.init(getApplication(), appID, appSign, userID, userName, callInvitationConfig);

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
    protected void onDestroy() {
        super.onDestroy();
        ZegoUIKitPrebuiltCallInvitationService.unInit();            // this method is performs cleanup operations specific to this zegocloud service .
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId==R.id.profile){
            startActivity(new Intent(MainActivity.this, UserNameActivity.class));
        }
        else  if (itemId == R.id.logoutAccount) {
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
