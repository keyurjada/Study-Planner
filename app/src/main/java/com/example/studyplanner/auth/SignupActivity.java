package com.example.studyplanner.auth;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studyplanner.R;
import com.example.studyplanner.databinding.ActivitySignupBinding;
import com.example.studyplanner.ui.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;
    private EditText name, email, password;
    private Button signupBtn;
    private FirebaseAuth auth;
    private Dialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        name = binding.etName;
        email = binding.etEmail;
        password = binding.etPassword;
        signupBtn = binding.btnSignup;
        auth = FirebaseAuth.getInstance();

        loader = new Dialog(this);
        loader.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loader.setContentView(R.layout.dialog_loader);
        if (loader.getWindow() != null) {
            loader.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        loader.setCancelable(false);

        signupBtn.setOnClickListener(v -> signupUser());
        
        TextView signin = binding.txtSignin;
        signin.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void signupUser() {
        String userName = name.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (userName.isEmpty()) {
            name.setError("Enter name");
            name.requestFocus();
            return;
        }
        if (userEmail.isEmpty()) {
            email.setError("Enter email");
            email.requestFocus();
            return;
        }
        if (userPassword.length() < 6) {
            password.setError("Password must be 6+ characters");
            password.requestFocus();
            return;
        }

        if (!isFinishing() && !isDestroyed()) {
            loader.show();
        }

        auth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("name", userName);
                            userMap.put("email", userEmail);

                            db.collection("users").document(uid).set(userMap)
                                    .addOnCompleteListener(dbTask -> {
                                        if (!isFinishing() && !isDestroyed() && loader.isShowing()) {
                                            loader.dismiss();
                                        }
                                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    });
                        } else {
                            if (!isFinishing() && !isDestroyed() && loader.isShowing()) {
                                loader.dismiss();
                            }
                            Toast.makeText(this, "Failed to initialize user session", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (!isFinishing() && !isDestroyed() && loader.isShowing()) {
                            loader.dismiss();
                        }
                        String errorMsg = task.getException() != null ? task.getException().getLocalizedMessage() : "Sign up failed";
                        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
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