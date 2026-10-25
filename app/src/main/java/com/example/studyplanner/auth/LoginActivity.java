package com.example.studyplanner.auth;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studyplanner.R;
import com.example.studyplanner.databinding.ActivityLoginBinding;
import com.example.studyplanner.ui.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private EditText email, password;
    private Button loginBtn;
    private FirebaseAuth auth;
    private Dialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        email = binding.loginEmail;
        password = binding.loginPassword;
        loginBtn = binding.btnLogin;
        auth = FirebaseAuth.getInstance();

        loader = new Dialog(this);
        loader.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loader.setContentView(R.layout.dialog_loader);
        if (loader.getWindow() != null) {
            loader.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        loader.setCancelable(false);

        ImageView back = binding.btnBack;
        back.setOnClickListener(v -> finish());

        loginBtn.setOnClickListener(v -> loginUser());

        TextView forgotPassword = binding.forgotPassword;
        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (userEmail.isEmpty()) {
            email.setError("Enter email");
            email.requestFocus();
            return;
        }
        if (userPassword.isEmpty()) {
            password.setError("Enter password");
            password.requestFocus();
            return;
        }

        if (!isFinishing() && !isDestroyed()) {
            loader.show();
        }

        auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(task -> {
            if (!isFinishing() && !isDestroyed() && loader.isShowing()) {
                loader.dismiss();
            }

            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            } else {
                String errorMsg = task.getException() != null ? task.getException().getLocalizedMessage() : "Invalid email or password";
                Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loader != null && loader.isShowing()) {
            loader.dismiss();
        }
    }
}