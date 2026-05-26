package com.example.studyplanner.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studyplanner.databinding.ActivityNewPasswordBinding;
public class NewPasswordActivity extends AppCompatActivity {
    private ActivityNewPasswordBinding binding;
    EditText pass,confirm;
    Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        pass=binding.newPassword;
        confirm=binding.confirmPassword;
        save=binding.savePasswordBtn;
        save.setOnClickListener(v->{
            String p=pass.getText().toString();
            String c=confirm.getText().toString();
            if(p.length()<6){
                pass.setError("Minimum 6 characters");
                return;
            }
            if(!p.equals(c)){
                confirm.setError("Passwords not match");
                return;
            }
            startActivity(new Intent(NewPasswordActivity.this, PasswordSuccessActivity.class));
        });
    }
}