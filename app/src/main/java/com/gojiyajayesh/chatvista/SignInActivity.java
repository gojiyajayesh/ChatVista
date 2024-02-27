package com.gojiyajayesh.chatvista;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.gojiyajayesh.chatvista.models.Users;
import com.gojiyajayesh.chatvista.utils.AndroidUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class SignInActivity extends AppCompatActivity {
    private final int RC_SIGN_IN = 63;
    EditText Email, Password;
    Button SignIn, GoogleSignInBtn, FacebookSignInBtn, PhoneSignInBtn;
    TextView goSignUp, ForgotPassword;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    CallbackManager fmCallbackManager;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initialization();
        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSignInBtn.setOnClickListener(view -> {
            LoginManager.getInstance().logInWithReadPermissions(SignInActivity.this, Arrays.asList("email", "public_profile"));
            LoginManager.getInstance().registerCallback(fmCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d("TAG", "facebook:onSuccess:" + loginResult);
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    Log.d("TAG", "facebook:onCancel");
                }

                @Override
                public void onError(FacebookException error) {
                    Log.d("TAG", "facebook:onError", error);
                }
            });
        });
        ForgotPassword.setOnClickListener(v -> {
                startActivity(new Intent(SignInActivity.this,ForgotPasswordActivity.class));
        });
        PhoneSignInBtn.setOnClickListener(v->startActivity(new Intent(SignInActivity.this,PhoneLogInActivity.class)));
        GoogleSignInBtn.setOnClickListener(v -> {
            signInGoogle();
        });
        goSignUp.setOnClickListener(v -> startActivity(new Intent(SignInActivity.this, SignUpActivity.class)));
        signIn();
    }

    private void initialization() {
        Email = findViewById(R.id.signInPageEmail);
        Password = findViewById(R.id.signInPagePassword);
        SignIn = findViewById(R.id.signIn);
        GoogleSignInBtn = findViewById(R.id.signInWithGoogle);
        FacebookSignInBtn = findViewById(R.id.signInWithFacebook);
        PhoneSignInBtn = findViewById(R.id.signInWithPhone);
        goSignUp = findViewById(R.id.goSignUp);
        ForgotPassword = findViewById(R.id.forgotPasswordSignInPage);
        progressBar = findViewById(R.id.signInProgressBar);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        fmCallbackManager = CallbackManager.Factory.create();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void inProgress(boolean isProgress) {
        if (isProgress) {
            progressBar.setVisibility(View.VISIBLE);
            SignIn.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            SignIn.setVisibility(View.VISIBLE);
        }
    }

    void signIn() {
        SignIn.setOnClickListener(view -> {
            inProgress(true);
            String email = Email.getText().toString().trim();
            String password = Password.getText().toString().trim();
            if(email.isEmpty()||password.isEmpty()){
                if (email.isEmpty()) {
                    Email.setError("Email cannot be empty");
                    inProgress(false);
                }
                if (password.isEmpty()) {
                    Password.setError("Password cannot be empty");
                    inProgress(false);
                }
            }
            else {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(SignInActivity.this, MainActivity.class));
                        inProgress(false);
                        finish();
                    } else {
                        AndroidUtils.customToast(getApplicationContext(), "Authentication Failed!" + task.getException().getMessage(), Toast.LENGTH_LONG);
                        inProgress(false);
                    }
                });
            }
        });
        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
        }
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("TAG", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success");
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    finish();
                } else {
                    Log.w("TAG", "signInWithCredential:failure", task.getException());
                    Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fmCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "FirebaseAuthWithGoogle: " + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        mAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d("TAG", "signInWithCredential:success");
                FirebaseUser firebaseUser = task.getResult().getUser();
                assert firebaseUser != null;
                String uid = firebaseUser.getUid();
                String email = firebaseUser.getEmail();
                String username = firebaseUser.getDisplayName();
                String profileId = firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null;
                createUserInDatabase(uid, username, email, profileId);
                startActivity(new Intent(SignInActivity.this,MainActivity.class));
                finish();
            } else {
                Log.w("TAG", "signInWithCredential:failure", task.getException());
            }
        });
    }
    private void createUserInDatabase(String uid, String username, String email, String profileId) {
        Users user = new Users(username,profileId,email,"JayeshAhir1168@1380");
        database.getReference().child("Users").child(uid).setValue(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d("TAG", "DataStore Successfully");
                })
                .addOnFailureListener(e -> {
                    Log.w("TAG", "Failed!", e);
                });
    }
}