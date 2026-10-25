package com.example.studyplanner.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studyplanner.databinding.ActivityPasswordSuccessBinding;

public class PasswordSuccessActivity extends AppCompatActivity {
    private ActivityPasswordSuccessBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPasswordSuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String email = getIntent().getStringExtra("email");
        if (email != null && !email.trim().isEmpty()) {
            binding.txtDescription.setText("We've sent a password reset link to " + email.trim() + ".\n\nClick the link in your email to choose a new password, then return here to sign in.");
        }

        binding.goHomeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(PasswordSuccessActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}