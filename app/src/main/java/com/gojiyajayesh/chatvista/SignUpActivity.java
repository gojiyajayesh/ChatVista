package com.gojiyajayesh.chatvista;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
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
    ProgressBar progressBar;
    CallbackManager fmCallbackManager;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //initialization
        initializeComponents();

        FacebookSdk.sdkInitialize(getApplicationContext());
        fmCallbackManager = CallbackManager.Factory.create();
        initializeFacebookLogin();
        goSignIn.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SignInActivity.class)));
        GoogleSignUpBtn.setOnClickListener(view -> signUpWithGoogle());
        PhoneSignUpBtn.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, PhoneLogInActivity.class));
            finish();
        });
        signUp();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fmCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            handleGoogleSignInResult(data);
        }
    }

    private void initializeFacebookLogin() {
        FacebookSignUpBtn.setOnClickListener(view -> {
            LoginManager.getInstance().logInWithReadPermissions(SignUpActivity.this, Arrays.asList("email", "public_profile"));
            LoginManager.getInstance().registerCallback(fmCallbackManager, new FacebookCallback<LoginResult>() {
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
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                startActivity(new Intent(SignUpActivity.this, UserNameActivity.class));
                finish();
            } else {
                Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                Log.w("TAG", "signInWithCredential:failure", task.getException());
            }
        });
    }

    void initializeComponents() {
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
    }

    private void signUpWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleGoogleSignInResult(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            Log.w("TAG", "Google sign in failed", e);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            mAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = task.getResult().getUser();
                    assert firebaseUser != null;
                    String uid = firebaseUser.getUid();
                    String email = firebaseUser.getEmail();
                    String displayName = firebaseUser.getDisplayName();
                    String profileId = firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null;
                    String password = "googleLogin";
                    Intent intent = new Intent(SignUpActivity.this, UserNameActivity.class);
                    Users user = new Users();
                    user.setUserId(uid);
                    user.setPhoneOrEmail(email);
                    user.setPassword(password);
                    user.setProfileId(profileId);
                    user.setUsername(displayName);
                    AndroidUtils.setPassedIntentData(intent, user);
                    startActivity(intent);
                    finish();
                } else {
                    Log.w("TAG", "signInWithCredential:failure", task.getException());
                    AndroidUtils.customToast(getApplicationContext(), "User sign up failed!", Toast.LENGTH_LONG);
                }
            });
        } else {
            mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            });
        }
    }

    private void createUserInDatabase(String uid, String username, String email, String profileId) {
        Users user = new Users(uid, username, profileId, email, "JayeshAhir1168@1380");
        database.getReference().child("Users").child(uid).setValue(user).addOnSuccessListener(aVoid -> {
            Log.d("TAG", "DataStore Successfully");
        }).addOnFailureListener(e -> {
            Log.w("TAG", "Failed!", e);
        });
    }

    private void signUp() {
        SignUp.setOnClickListener(view -> {
            String username = Username.getText().toString().trim();
            String email = Email.getText().toString().trim();
            String password = Password.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                if (username.isEmpty()) {
                    Username.setError("Username cannot be empty");
                }
                if (email.isEmpty()) {
                    Email.setError("Email cannot be empty");
                }
                if (password.isEmpty()) {
                    Password.setError("Password cannot be empty");
                }
            } else if (password.length() < 6) {
                Password.setError("Password must be at least 6 characters");
            } else {
                mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(signUpTask -> {
                    if (signUpTask.isSuccessful()) {
                        boolean isNewUser = Objects.requireNonNull(signUpTask.getResult().getSignInMethods()).isEmpty();

                        if (!isNewUser) {
                            AndroidUtils.customToast(getApplicationContext(), "Email is already in use!", Toast.LENGTH_LONG);
                        } else {
                            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(createUserTask -> {
                                if (createUserTask.isSuccessful()) {
                                    String id = Objects.requireNonNull(createUserTask.getResult().getUser()).getUid();
                                    Users user = new Users(id, username, null, email, password);
                                    database.getReference().child("Users").child(id).setValue(user);
                                    AndroidUtils.customToast(getApplicationContext(), "User Created Successfully", Toast.LENGTH_LONG);
                                    Intent intent = new Intent(SignUpActivity.this, UserNameActivity.class);
                                    intent.putExtra("EmailOrPhone", email);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    AndroidUtils.customToast(getApplicationContext(), Objects.requireNonNull(createUserTask.getException()).getMessage(), Toast.LENGTH_LONG);
                                }
                            });
                        }
                    } else {
                        AndroidUtils.customToast(getApplicationContext(), Objects.requireNonNull(signUpTask.getException()).getMessage(), Toast.LENGTH_LONG);
                    }
                });
            }
        });

    }
}
