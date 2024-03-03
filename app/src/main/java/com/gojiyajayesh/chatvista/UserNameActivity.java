package com.gojiyajayesh.chatvista;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import com.gojiyajayesh.chatvista.models.UserDetails;
import com.gojiyajayesh.chatvista.models.Users;
import com.gojiyajayesh.chatvista.utils.AndroidUtils;
import com.gojiyajayesh.chatvista.utils.FirebaseUtils;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import io.grpc.Context;

public class UserNameActivity extends AppCompatActivity {
    Button ContinueBtn;
    EditText Username, FullName;
    ImageView ProfilePic,UpdateProfilePic;
    TextView UserNameUnique;
    ImageView Icon;
    Users user;
    CheckBox condition;
    String PhoneOrEmail;
    String Password;
    String Uid;
    String ProfileId;
    boolean[] isExist;
    FirebaseStorage storage;
    FirebaseDatabase database;
    FirebaseFirestore firestore;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);
        ContinueBtn = findViewById(R.id.continueBtn);
        ContinueBtn.setEnabled(false);
        Username = findViewById(R.id.DetailPageUserIdentifyTextInput);
        FullName = findViewById(R.id.DetailPageFullNameTextInput);
        ProfilePic = findViewById(R.id.UserNameActivityProfilePic);
        UpdateProfilePic = findViewById(R.id.UpdateProfilePictureUserNameActivity);
        UserNameUnique = findViewById(R.id.uniqueUsername);
        Icon=findViewById(R.id.uniqueUsernameicon);
        condition = findViewById(R.id.temsConditionCheckbox);
        progressBar=findViewById(R.id.UserNameActivityProgressBar);
        storage=FirebaseStorage.getInstance();
        firestore =FirebaseFirestore.getInstance();
        database=FirebaseDatabase.getInstance();
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
        Picasso.get().load(user2.getProfileId()).placeholder(R.drawable.default_profile_picture).into(ProfilePic);
        Password=user2.getPassword();
        FullName.setText(user2.getUsername());
        UpdateProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,63);
            }
        });
        ProfilePic.setOnClickListener(view -> {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle(user2.getUsername());
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View dialogView = inflater.inflate(R.layout.profile_picture_view, null);
            ImageView profilePictureClick = dialogView.findViewById(R.id.profilePictureClick);
            Picasso.get().load(ProfileId).placeholder(R.drawable.default_profile_picture).into(profilePictureClick);
            builder.setView(dialogView);
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            builder.setNegativeButton("Update", (dialog, which) -> AndroidUtils.customToast(this, "Update now", Toast.LENGTH_LONG));
            android.app.AlertDialog dialog = builder.create();
            dialog.show();
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data.getData()!=null)
        {
            inProgress(true);
           Uri photoPath=data.getData();
           ProfilePic.setImageURI(photoPath);

           final StorageReference reference= storage.getReference().child("UserProfilePicture's").child(FirebaseUtils.currentUserId());
           reference.putFile(photoPath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
               @Override
               public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                       @Override
                       public void onSuccess(Uri uri) {
                           ProfileId=uri.toString();
                           inProgress(false);
                           AndroidUtils.customToast(getApplicationContext(),"Profile Photo Updated",1);
                       }
                   });
               }
           });
        }
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
    private void inProgress(boolean isProgress) {
        progressBar.setVisibility(isProgress ? View.VISIBLE : View.INVISIBLE);
        ContinueBtn.setVisibility(isProgress ? View.INVISIBLE : View.VISIBLE);
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
