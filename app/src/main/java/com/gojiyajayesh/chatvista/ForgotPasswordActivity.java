package com.gojiyajayesh.chatvista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gojiyajayesh.chatvista.utils.AndroidUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.List;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText emailEditText;
    private TextView goBackTextView;
    private Button resetPasswordButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        // Initialization
        initializeViews();

        mAuth = FirebaseAuth.getInstance();

        resetPasswordButton.setOnClickListener(view -> {
            inProgress(true);
            email = emailEditText.getText().toString().trim();
            if (email.isEmpty()) {
                emailEditText.setError("Email cannot be empty");
                inProgress(false);
            } else {
                resetPassword();
            }
        });

        goBackTextView.setOnClickListener(view -> startActivity(new Intent(ForgotPasswordActivity.this, SignInActivity.class)));
    }

    private void resetPassword() {
        inProgress(true);
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                SignInMethodQueryResult result = task.getResult();
                List<String> signInMethods = result.getSignInMethods();
                if (signInMethods == null || signInMethods.isEmpty()) {
                    emailEditText.setError("Email is not registered");
                    inProgress(false);
                } else {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task1 -> {
                        AndroidUtils.customToast(getApplicationContext(), "Reset password link has been sent to the registered email", Toast.LENGTH_LONG);
                        inProgress(false);
                        startActivity(new Intent(ForgotPasswordActivity.this, SignInActivity.class));
                        finish();
                    }).addOnFailureListener(e -> {
                        AndroidUtils.customToast(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                        inProgress(false);
                    });
                }
            } else {
                AndroidUtils.customToast(getApplicationContext(), "Error fetching sign-in methods", Toast.LENGTH_LONG);
                inProgress(false);
            }
        });
    }

    private void inProgress(boolean isProgress) {
        if (isProgress) {
            progressBar.setVisibility(View.VISIBLE);
            resetPasswordButton.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            resetPasswordButton.setVisibility(View.VISIBLE);
        }
    }

    private void initializeViews() {
        emailEditText = findViewById(R.id.ForgetPageEmail);
        goBackTextView = findViewById(R.id.backForgetToMainPage);
        resetPasswordButton = findViewById(R.id.ForgotPasswordBtn);
        progressBar = findViewById(R.id.ForgetProgressBar);
    }
}
