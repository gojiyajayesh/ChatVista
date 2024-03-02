package com.gojiyajayesh.chatvista;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import com.gojiyajayesh.chatvista.models.UserDetails;
import com.gojiyajayesh.chatvista.models.Users;
import com.gojiyajayesh.chatvista.utils.AndroidUtils;
import com.gojiyajayesh.chatvista.utils.FirebaseUtils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class UserNameActivity extends AppCompatActivity {
    Button ContinueBtn;
    EditText Username, FullName;
    ImageView ProfilePic;
    TextView UserNameUnique;
    ImageView Icon;
    Users user;
    CheckBox condition;
    String PhoneOrEmail;
    String Password;
    String Uid;
    String ProfileId;
    boolean[] isExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);
        ContinueBtn = findViewById(R.id.continueBtn);
        ContinueBtn.setEnabled(false);
        Username = findViewById(R.id.DetailPageUserIdentifyTextInput);
        FullName = findViewById(R.id.DetailPageFullNameTextInput);
        ProfilePic = findViewById(R.id.UserNameActivityProfilePic);
        UserNameUnique = findViewById(R.id.uniqueUsername);
        Icon=findViewById(R.id.uniqueUsernameicon);
        condition = findViewById(R.id.temsConditionCheckbox);
        condition.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showTermsAndConditionsDialog();
            } else {
                ContinueBtn.setEnabled(true);
            }
        });
        Users user2 = AndroidUtils.getPassedIntentData(getIntent());
        PhoneOrEmail = user2.getPhoneOrEmail();
        Uid = user2.getUserId();
        ProfileId = user2.getProfileId();
        Password=user2.getPassword();
        getUsername();
        ContinueBtn.setOnClickListener(view -> {
            if (!isExist[0]) setUsername();
        });
        Username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String username = Username.getText().toString();
                if (username.length() >= 3) {
                    checkUsernameAvailability(username).addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            boolean isAvailable = task.getResult();
                            if (!isAvailable) {
                                UserNameUnique.setText("Username already in use");
                                UserNameUnique.setTextColor(getResources().getColor(R.color.progress_red));
                                Icon.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.reject_img));
                                isExist = new boolean[]{true};
                            } else {
                                UserNameUnique.setText("Username is available");
                                UserNameUnique.setTextColor(getResources().getColor(R.color.progress_green));
                                Icon.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.aprove_img));
                                isExist = new boolean[]{false};
                            }
                        } else {
                            AndroidUtils.customToast(getApplicationContext(), "Error checking username availability", Toast.LENGTH_SHORT);
                        }
                    });
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    Task<Boolean> checkUsernameAvailability(String username) {
        return FirebaseFirestore.getInstance().collection("UserDetails").whereEqualTo("username", username).get().continueWith(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    UserDetails userDetails = document.toObject(UserDetails.class);
                    assert userDetails != null;
                    if (!userDetails.getUserId().equals(FirebaseUtils.currentUserId())) {
                        return false;
                    }
                }
                return true;
            } else {
                throw Objects.requireNonNull(task.getException());
            }
        });
    }

    void setUsername() {
        String userName = Username.getText().toString().trim();
        String fullName = FullName.getText().toString().trim();

        if (user != null) {
            user.setUsername(userName);
            user.setFullName(fullName);
        } else {
            user = new Users(PhoneOrEmail,Password,ProfileId,userName, fullName, FirebaseUtils.currentUserId());
        }
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseUtils.currentUserId()).setValue(user);
        UserDetails userDetails = new UserDetails(FirebaseUtils.currentUserId(), Username.getText().toString());
        FirebaseFirestore.getInstance().collection("UserDetails").document(FirebaseUtils.currentUserId()).set(userDetails);
        FirebaseUtils.currentUserDetail().set(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                startActivity(new Intent(UserNameActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    void getUsername() {
        FirebaseUtils.currentUserDetail().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user = task.getResult().toObject(Users.class);
                if (user != null) {
                    Username.setText(user.getUsername());
                    FullName.setText(user.getFullName());
                    Picasso.get().load(user.getProfileId()).placeholder(R.drawable.default_profile_picture).into(ProfilePic);
                }
            }
        });
    }
    private void showTermsAndConditionsDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        String termsAndConditions = getString(R.string.terms_and_conditions);
        dialogBuilder.setMessage(Html.fromHtml(termsAndConditions)).setCancelable(false).setPositiveButton("Accept", (dialog, id) -> {
            dialog.dismiss();
            condition.setChecked(true);
            ContinueBtn.setEnabled(true);
        }).setNegativeButton("Decline", (dialog, id) -> {
            dialog.dismiss();
            condition.setChecked(false);
            ContinueBtn.setEnabled(false);
        });

        AlertDialog alert = dialogBuilder.create();
        alert.show();
        TextView textView = alert.findViewById(android.R.id.message);
        if (textView != null) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            textView.setTextColor(ContextCompat.getColor(this, R.color.black));
        }
    }
}
