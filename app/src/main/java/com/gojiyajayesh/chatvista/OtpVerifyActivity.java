package com.gojiyajayesh.chatvista;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.gojiyajayesh.chatvista.utils.AndroidUtils;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class OtpVerifyActivity extends AppCompatActivity {
    TextView PhoneNumberTextView;
    FirebaseAuth mAuth;
    String phoneNumber;
    TextView resendOtp, resendTime;
    Button VerifyBtn;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    EditText[] otpEditTexts;
    ProgressBar progressBar;
    private String mVerificationId, otpCode;
    private long timeoutSeconds = 60L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);
        initialization();
        VerifyBtn.setOnClickListener(view -> {
            inProgress(true);
            otpCode = getOtp();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otpCode);
            signInWithPhoneAuthCredential(credential);
        });
        resendOtp.setOnClickListener(v -> {
            sendOtp(true);
        });
        otpInputChanger();
        sendOtp(false);
    }

    void otpInputChanger() {
        for (int i = 0; i < otpEditTexts.length; i++) {
            final int index = i;
            otpEditTexts[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < otpEditTexts.length - 1) {
                        otpEditTexts[index + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            otpEditTexts[i].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN && index > 0 && otpEditTexts[index].getText().toString().isEmpty()) {
                    otpEditTexts[index - 1].requestFocus();
                    return true;
                }
                return false;
            });
        }
    }


    void initialization() {
        PhoneNumberTextView = findViewById(R.id.phone_txt_view);
        phoneNumber = getIntent().getStringExtra("mobileNumber");
        PhoneNumberTextView.setText(phoneNumber);
        mAuth = FirebaseAuth.getInstance();
        VerifyBtn = findViewById(R.id.verify_btn);
        otpEditTexts = new EditText[]{findViewById(R.id.otp_1), findViewById(R.id.otp_2), findViewById(R.id.otp_3), findViewById(R.id.otp_4), findViewById(R.id.otp_5), findViewById(R.id.otp_6)};
        progressBar = findViewById(R.id.progress_bar_otp);
        resendOtp = findViewById(R.id.resend_otp_txt);
        resendTime = findViewById(R.id.resendTime);
    }

    private void inProgress(boolean isProgress) {
        if (isProgress) {
            progressBar.setVisibility(View.VISIBLE);
            VerifyBtn.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            VerifyBtn.setVisibility(View.VISIBLE);
        }
    }

    String getOtp() {
        StringBuilder sb = new StringBuilder();
        for (EditText i : otpEditTexts) {
            sb.append(i.getText().toString().trim());
        }
        return sb.toString().trim();
    }

    void startResendTimer() {
        resendOtp.setEnabled(false);
        resendOtp.setText("Resend OTP in");
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeoutSeconds--;
                runOnUiThread(() -> {
                    resendTime.setText("( 00:" + String.format("%02d", timeoutSeconds) + " )"); // Format seconds to display leading zero
                });
                if (timeoutSeconds <= 0) {
                    timer.cancel();
                    runOnUiThread(() -> {
                        resendOtp.setEnabled(true);
                        resendTime.setVisibility(View.GONE);
                        resendOtp.setText("Resend OTP");
                    });
                }
            }
        }, 0, 1000);
    }


    private void sendOtp(boolean isRend) {
        startResendTimer();
        inProgress(true);
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(credential);
                inProgress(false);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                AndroidUtils.customToast(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG);
                inProgress(false);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;
                AndroidUtils.customToast(getApplicationContext(), "OTP send successfully", Toast.LENGTH_LONG);
                inProgress(false);
            }
        };
        PhoneAuthOptions options;
        if (isRend) {
            options = PhoneAuthOptions.newBuilder(mAuth).setPhoneNumber(phoneNumber).setTimeout(60L, TimeUnit.SECONDS).setActivity(this).setCallbacks(mCallbacks).setForceResendingToken(mResendToken).build();
        } else {
            options = PhoneAuthOptions.newBuilder(mAuth).setPhoneNumber(phoneNumber).setTimeout(60L, TimeUnit.SECONDS).setActivity(this).setCallbacks(mCallbacks).build();
        }
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                inProgress(false);
                AndroidUtils.customToast(getApplicationContext(), "Sign In Successfully", Toast.LENGTH_LONG);
                startActivity(new Intent(OtpVerifyActivity.this, MainActivity.class));
            } else {
                inProgress(false);
                AndroidUtils.customToast(getApplicationContext(), "Verification Failed!", Toast.LENGTH_LONG);
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    AndroidUtils.customToast(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG);
                }
            }
        });
    }
}