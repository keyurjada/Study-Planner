package com.example.studyplanner.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studyplanner.R;
import com.example.studyplanner.databinding.ActivityForgotPasswordBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ActivityForgotPasswordBinding binding;
    private EditText email;
    private Button sendResetBtn;
    private ImageView back;
    public static String generatedOtp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        email = binding.emailInput;
        sendResetBtn = binding.sendOtpBtn;
        back = binding.backBtn;

        back.setOnClickListener(v -> finish());

        sendResetBtn.setOnClickListener(v -> {
            String userEmail = email.getText().toString().trim();
            if (userEmail.isEmpty()) {
                email.setError("Enter registered email");
                email.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                email.setError("Please enter a valid email address");
                email.requestFocus();
                return;
            }

            // Disable button while processing
            sendResetBtn.setEnabled(false);
            sendResetBtn.setText("Sending Link...");

            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.useAppLanguage();
            auth.sendPasswordResetEmail(userEmail)
                    .addOnCompleteListener(task -> {
                        sendResetBtn.setEnabled(true);
                        sendResetBtn.setText(R.string.str_send_reset_link);

                        if (task.isSuccessful()) {
                            Intent intent = new Intent(ForgotPasswordActivity.this, PasswordSuccessActivity.class);
                            intent.putExtra("email", userEmail);
                            startActivity(intent);
                            finish();
                        } else {
                            Exception exception = task.getException();
                            String message = "Failed to send reset link. Please try again.";
                            if (exception instanceof FirebaseAuthInvalidUserException) {
                                message = "No account found with this email address.";
                            } else if (exception != null && exception.getLocalizedMessage() != null) {
                                message = exception.getLocalizedMessage();
                            }
                            Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}