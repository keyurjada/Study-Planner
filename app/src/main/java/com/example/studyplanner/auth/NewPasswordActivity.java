package com.example.studyplanner.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studyplanner.databinding.ActivityNewPasswordBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class NewPasswordActivity extends AppCompatActivity {
    private ActivityNewPasswordBinding binding;
    private EditText pass, confirm;
    private Button save;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        pass = binding.newPassword;
        confirm = binding.confirmPassword;
        save = binding.savePasswordBtn;
        backBtn = binding.backBtn;

        backBtn.setOnClickListener(v -> finish());

        save.setOnClickListener(v -> {
            String p = pass.getText().toString().trim();
            String c = confirm.getText().toString().trim();

            if (p.length() < 6) {
                pass.setError("Minimum 6 characters");
                pass.requestFocus();
                return;
            }
            if (!p.equals(c)) {
                confirm.setError("Passwords do not match");
                confirm.requestFocus();
                return;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                user.updatePassword(p).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        navigateToSuccess();
                    } else {
                        String msg = task.getException() != null ? task.getException().getLocalizedMessage() : "Failed to update password";
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                navigateToSuccess();
            }
        });
    }

    private void navigateToSuccess() {
        Intent intent = new Intent(NewPasswordActivity.this, PasswordSuccessActivity.class);
        startActivity(intent);
        finish();
    }
}