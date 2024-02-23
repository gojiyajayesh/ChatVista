package com.gojiyajayesh.chatvista;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

public class PhoneLogInActivity extends AppCompatActivity {
    CountryCodePicker CountryCode;
    EditText PhoneNumber;
    Button ContinueBtn;
    TextView Back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_log_in);
        initialization();
        CountryCode.registerCarrierNumberEditText(PhoneNumber);
        ContinueBtn.setOnClickListener(view->{
            if(!CountryCode.isValidFullNumber()){
               PhoneNumber.setError("Invalid Mobile Number !");
               return;
            }
            Intent intent=new Intent(PhoneLogInActivity.this,OtpVerifyActivity.class);
            intent.putExtra("mobileNumber",CountryCode.getFullNumberWithPlus());
            startActivity(intent);
        });
        Back.setOnClickListener(view-> startActivity(new Intent(PhoneLogInActivity.this,SignInActivity.class)));
    }
    void initialization(){
        CountryCode=findViewById(R.id.countryCodePicker2);
        ContinueBtn=findViewById(R.id.continue_btn);
        PhoneNumber=findViewById(R.id.mobileNumber);
        Back=findViewById(R.id.backMainPage);
    }

}