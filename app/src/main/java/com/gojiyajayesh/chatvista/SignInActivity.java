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
import com.gojiyajayesh.chatvista.utils.FirebaseUtils;
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

public class SignInActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 63;

    private EditText emailEditText, passwordEditText;
    private Button signInButton, googleSignInButton, facebookSignInButton, phoneSignInButton;
    private TextView signUpTextView, forgotPasswordTextView;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private CallbackManager callbackManager;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initializeComponents();
        FacebookSdk.sdkInitialize(getApplicationContext());

        facebookSignInButton.setOnClickListener(view -> {
            LoginManager.getInstance().logInWithReadPermissions(SignInActivity.this, Arrays.asList("email", "public_profile"));
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
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

        forgotPasswordTextView.setOnClickListener(v -> startActivity(new Intent(SignInActivity.this, ForgotPasswordActivity.class)));
        phoneSignInButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, PhoneLogInActivity.class);
            finish();
        });
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());
        signUpTextView.setOnClickListener(v -> startActivity(new Intent(SignInActivity.this, SignUpActivity.class)));
        signIn();
    }

    private void initializeComponents() {
        emailEditText = findViewById(R.id.signInPageEmail);
        passwordEditText = findViewById(R.id.signInPagePassword);
        signInButton = findViewById(R.id.signIn);
        googleSignInButton = findViewById(R.id.signInWithGoogle);
        facebookSignInButton = findViewById(R.id.signInWithFacebook);
        phoneSignInButton = findViewById(R.id.signInWithPhone);
        signUpTextView = findViewById(R.id.goSignUp);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordSignInPage);
        progressBar = findViewById(R.id.signInProgressBar);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        callbackManager = CallbackManager.Factory.create();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void inProgress(boolean isProgress) {
        progressBar.setVisibility(isProgress ? View.VISIBLE : View.INVISIBLE);
        signInButton.setVisibility(isProgress ? View.INVISIBLE : View.VISIBLE);
    }

    private void signIn() {
        signInButton.setOnClickListener(view -> {
            inProgress(true);
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            if (email.isEmpty() || password.isEmpty()) {
                if (email.isEmpty()) {
                    emailEditText.setError("Email cannot be empty");
                }
                if (password.isEmpty()) {
                    passwordEditText.setError("Password cannot be empty");
                }
                inProgress(false);
            } else {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(SignInActivity.this, UserNameActivity.class);
                        Users user = new Users();
                        user.setUserId(FirebaseUtils.currentUserId());
                        user.setPhoneOrEmail(email);
                        user.setPassword(password);
                        AndroidUtils.setPassedIntentData(intent, user);
                        startActivity(intent);
                        finish();
                    } else {
                        AndroidUtils.customToast(getApplicationContext(), "Authentication Failed!" + task.getException().getMessage(), Toast.LENGTH_LONG);
                    }
                    inProgress(false);
                });
            }
        });

        if (FirebaseUtils.isLoggedIn()) {
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
        }
    }

    private void signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                assert firebaseUser != null;
                String email = firebaseUser.getEmail();
                Intent intent = new Intent(SignInActivity.this, UserNameActivity.class);
                Users user = new Users();
                user.setUserId(FirebaseUtils.currentUserId());
                user.setPhoneOrEmail(email);
                user.setPassword("facebookLogin");
                AndroidUtils.setPassedIntentData(intent, user);
                startActivity(intent);
                finish();
            } else {
                Log.w("TAG", "signInWithCredential:failure", task.getException());
                Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
                FirebaseUser firebaseUser = task.getResult().getUser();
                assert firebaseUser != null;
                String uid = firebaseUser.getUid();
                String email = firebaseUser.getEmail();
                String profileId = firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null;
                String password = "googleLogin";
                Intent intent = new Intent(SignInActivity.this, UserNameActivity.class);
                Users user = new Users();
                user.setUserId(uid);
                user.setPhoneOrEmail(email);
                user.setPassword(password);
                user.setProfileId(profileId);
                AndroidUtils.setPassedIntentData(intent, user);
                startActivity(intent);
                finish();
            } else {
                Log.w("TAG", "signInWithCredential:failure", task.getException());
            }
        });
    }
}
