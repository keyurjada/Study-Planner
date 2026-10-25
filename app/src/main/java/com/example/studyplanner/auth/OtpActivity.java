package com.example.studyplanner.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studyplanner.databinding.ActivityOtpBinding;

import java.util.Random;

public class OtpActivity extends AppCompatActivity {
    private ActivityOtpBinding binding;
    private EditText otp1, otp2, otp3, otp4;
    private TextView timerText;
    private ImageView backBtn;
    private int seconds = 30;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    private String currentOtp;
    private String userEmail;

    private void clearOtpFields() {
        otp1.setText("");
        otp2.setText("");
        otp3.setText("");
        otp4.setText("");
        otp1.requestFocus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userEmail = getIntent().getStringExtra("email");
        currentOtp = getIntent().getStringExtra("otp");
        if (currentOtp == null) {
            currentOtp = ForgotPasswordActivity.generatedOtp;
        }

        otp1 = binding.otp1;
        otp2 = binding.otp2;
        otp3 = binding.otp3;
        otp4 = binding.otp4;
        backBtn = binding.backBtn;
        timerText = binding.resendCode;

        runnable = new Runnable() {
            @Override
            public void run() {
                if (seconds > 0) {
                    timerText.setEnabled(false);
                    timerText.setText("You can resend the code in " + seconds + "s");
                    seconds--;
                    handler.postDelayed(this, 1000);
                } else {
                    timerText.setText("Resend Code");
                    timerText.setEnabled(true);
                }
            }
        };

        timerText.setOnClickListener(v -> {
            if (seconds <= 0) {
                Random random = new Random();
                currentOtp = String.valueOf(1000 + random.nextInt(9000));
                ForgotPasswordActivity.generatedOtp = currentOtp;
                Toast.makeText(this, "New OTP: " + currentOtp, Toast.LENGTH_LONG).show();
                seconds = 30;
                handler.removeCallbacks(runnable);
                handler.post(runnable);
            }
        });

        // Start timer once
        handler.post(runnable);

        setupOtpTextWatchers();
        setupKeyListeners();

        backBtn.setOnClickListener(v -> finish());
    }

    private void setupOtpTextWatchers() {
        otp1.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    otp2.requestFocus();
                }
            }
        });

        otp2.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    otp3.requestFocus();
                }
            }
        });

        otp3.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    otp4.requestFocus();
                }
            }
        });

        otp4.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    verifyOtp();
                }
            }
        });
    }

    private void setupKeyListeners() {
        otp2.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN && otp2.getText().length() == 0) {
                otp1.requestFocus();
                return true;
            }
            return false;
        });

        otp3.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN && otp3.getText().length() == 0) {
                otp2.requestFocus();
                return true;
            }
            return false;
        });

        otp4.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN && otp4.getText().length() == 0) {
                otp3.requestFocus();
                return true;
            }
            return false;
        });
    }

    private void verifyOtp() {
        String enteredOtp = otp1.getText().toString().trim() +
                otp2.getText().toString().trim() +
                otp3.getText().toString().trim() +
                otp4.getText().toString().trim();

        if (enteredOtp.equals(currentOtp)) {
            Intent intent = new Intent(OtpActivity.this, NewPasswordActivity.class);
            intent.putExtra("email", userEmail);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(OtpActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
            clearOtpFields();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }
}