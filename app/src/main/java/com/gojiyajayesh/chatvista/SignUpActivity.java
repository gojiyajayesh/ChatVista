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
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    private final int RC_SIGN_IN = 63;
    EditText Username, Email, Password;
    Button SignUp, GoogleSignUpBtn, FacebookSignUpBtn, PhoneSignUpBtn;
    TextView goSignIn;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseUser user;
    ProgressBar progressBar;
    CallbackManager mCallbackManager;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initialization();
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        FacebookSignUpBtn.setOnClickListener(view -> {
            LoginManager.getInstance().logInWithReadPermissions(SignUpActivity.this, Arrays.asList("email", "public_profile"));
            LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
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
        goSignIn.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SignInActivity.class)));
        GoogleSignUpBtn.setOnClickListener(view -> signUpGoogle());
        PhoneSignUpBtn.setOnClickListener(v -> startActivity(new Intent(SignUpActivity.this, PhoneLogInActivity.class)));
        signUp();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

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

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("TAG", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success");
                    user = mAuth.getCurrentUser();
                    assert user != null;
                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithCredential:failure", task.getException());
                    Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void initialization() {
        Username = findViewById(R.id.signUpPageUsername);
        Email = findViewById(R.id.signUpPageEmail);
        Password = findViewById(R.id.signUpPagePassword);
        SignUp = findViewById(R.id.signUp);
        GoogleSignUpBtn = findViewById(R.id.signUpWithGoogle);
        FacebookSignUpBtn = findViewById(R.id.signUpWithFacebook);
        PhoneSignUpBtn = findViewById(R.id.signUpWithPhone);
        goSignIn = findViewById(R.id.goSignIn);
        progressBar = findViewById(R.id.signUpProgressBar);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mCallbackManager = CallbackManager.Factory.create();
    }

    private void inProgress(boolean isProgress) {
        if (isProgress) {
            progressBar.setVisibility(View.VISIBLE);
            SignUp.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            SignUp.setVisibility(View.VISIBLE);
        }
    }

    private void signUpGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        mAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                AndroidUtils.customToast(getApplicationContext(), "User sign up successfully!", Toast.LENGTH_LONG);
                Log.d("TAG", "signInWithCredential:success");
                FirebaseUser firebaseUser = task.getResult().getUser();
                assert firebaseUser != null;
                String uid = firebaseUser.getUid();
                String email = firebaseUser.getEmail();
                String username = firebaseUser.getDisplayName();
                String profileId = firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null;
                createUserInDatabase(uid, username, email, profileId);
                startActivity(new Intent(SignUpActivity.this,MainActivity.class));
            } else {
                AndroidUtils.customToast(getApplicationContext(), "User sign up failed!", Toast.LENGTH_LONG);
                Log.w("TAG", "signInWithCredential:failure", task.getException());
            }
        });
    }
    private void createUserInDatabase(String uid, String username, String email, String profileId) {
        // Assuming 'Users' is the top-level node for storing user information in the database
        Users user = new Users(username, email, profileId);
        database.getReference().child("Users").child(uid).setValue(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d("TAG", "DataStore Successfully");
                })
                .addOnFailureListener(e -> {
                    Log.w("TAG", "Failed!", e);
                });
    }

    private void signUp() {
        SignUp.setOnClickListener(view -> {
            inProgress(true);
            String username = Username.getText().toString().trim();
            String email = Email.getText().toString().trim();
            String password = Password.getText().toString().trim();
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                if (username.isEmpty()) {
                    Username.setError("Username cannot be empty");
                    inProgress(false);
                }
                if (email.isEmpty()) {
                    Email.setError("Email cannot be empty");
                    inProgress(false);
                }
                if (password.isEmpty()) {
                    Password.setError("Password cannot be empty");
                    inProgress(false);
                }
            } else if (password.length() < 6) {
                Password.setError("Password must be at least 6 character");
            } else {
                mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(signUpTask -> {
                    if (signUpTask.isSuccessful()) {
                        boolean isNewUser = Objects.requireNonNull(signUpTask.getResult().getSignInMethods()).isEmpty();

                        if (!isNewUser) {
                            AndroidUtils.customToast(getApplicationContext(), "Email is already in use!", Toast.LENGTH_LONG);
                            inProgress(false);
                        } else {
                            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(createUserTask -> {
                                if (createUserTask.isSuccessful()) {
                                    Users user = new Users(username, email, password);
                                    String id = Objects.requireNonNull(createUserTask.getResult().getUser()).getUid();
                                    database.getReference().child("Users").child(id).setValue(user);
                                    AndroidUtils.customToast(getApplicationContext(), "User Created Successfully", Toast.LENGTH_LONG);
                                    inProgress(false);
                                    startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                                } else {
                                    AndroidUtils.customToast(getApplicationContext(), Objects.requireNonNull(createUserTask.getException()).getMessage(), Toast.LENGTH_LONG);
                                    inProgress(false);
                                }
                            });
                        }
                    } else {
                        AndroidUtils.customToast(getApplicationContext(), Objects.requireNonNull(signUpTask.getException()).getMessage(), Toast.LENGTH_LONG);
                        inProgress(false);
                    }
                });
            }
        });

    }
}
