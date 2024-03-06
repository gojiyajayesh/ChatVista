package com.gojiyajayesh.chatvista;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

public class PhoneLogInActivity extends AppCompatActivity {
    private CountryCodePicker countryCodePicker;
    private EditText phoneNumber;
    private Button continueBtn;
    private TextView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_log_in);

        // Initialize views
        initialization();

        // Register EditText for country code
        countryCodePicker.registerCarrierNumberEditText(phoneNumber);

        // Continue button click listener
        continueBtn.setOnClickListener(view -> {
            // Validate phone number
            if (!countryCodePicker.isValidFullNumber()) {
                phoneNumber.setError("Invalid Mobile Number!");
                return;
            }

            // Proceed to OTP verification activity
            Intent intent = new Intent(PhoneLogInActivity.this, OtpVerifyActivity.class);
            intent.putExtra("mobileNumber", countryCodePicker.getFullNumberWithPlus());
            startActivity(intent);
            finish();
        });

        // Back button click listener
        back.setOnClickListener(view -> startActivity(new Intent(PhoneLogInActivity.this, SignInActivity.class)));
    }

    // Method to initialize views
    private void initialization() {
        countryCodePicker = findViewById(R.id.countryCodePicker2);
        continueBtn = findViewById(R.id.continue_btn);
        phoneNumber = findViewById(R.id.mobileNumber);
        back = findViewById(R.id.backMainPage);
    }
}
