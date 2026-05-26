package com.example.studyplanner.auth;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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
    EditText email, password;
    Button loginBtn;
    FirebaseAuth auth;
    Dialog loader;
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
        loader.setContentView(R.layout.dialog_loader);
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
        if(userEmail.isEmpty()){
            email.setError("Enter email");
            return;
        }
        if(userPassword.isEmpty()){
            password.setError("Enter password");
            return;
        }
        loader.show();
        auth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(task -> {
                    loader.dismiss();
                    if(task.isSuccessful()){
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if(user != null){
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }
                    }else{
                        Toast.makeText(this, "Invalid email or password", Toast.LENGTH_LONG).show();
                    }
                });
    }
}