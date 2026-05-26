package com.example.studyplanner.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studyplanner.databinding.ActivityForgotPasswordBinding;

import java.util.Random;
public class ForgotPasswordActivity extends AppCompatActivity {
    private ActivityForgotPasswordBinding binding;
    EditText email;
    Button sendOtp;
    ImageView back;
    public static String generatedOtp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        email = binding.emailInput;
        sendOtp = binding.sendOtpBtn;
        back = binding.backBtn;
        back.setOnClickListener(v -> finish());
        sendOtp.setOnClickListener(v -> {
            String userEmail = email.getText().toString();
            if(userEmail.isEmpty()){
                email.setError("Enter email");
                return;
            }
            Random random = new Random();
            generatedOtp = String.valueOf(1000 + random.nextInt(9000));
            Toast.makeText(this, "OTP: " + generatedOtp, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ForgotPasswordActivity.this, OtpActivity.class);
            intent.putExtra("email", userEmail);
            startActivity(intent);
        });
    }
}