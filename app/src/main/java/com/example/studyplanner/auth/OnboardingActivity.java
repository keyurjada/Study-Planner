package com.example.studyplanner.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.studyplanner.R;
import com.example.studyplanner.adapters.OnboardingAdapter;
import com.example.studyplanner.databinding.ActivityOnboardingBinding;

public class OnboardingActivity extends AppCompatActivity {
    private ActivityOnboardingBinding binding;
    private ViewPager2 viewPager;
    private Button btnNext, btnSkip;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int totalPages = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dotsLayout = binding.dotsLayout;
        viewPager = binding.viewPager;
        btnNext = binding.btnNext;
        btnSkip = binding.btnSkip;

        OnboardingAdapter adapter = new OnboardingAdapter(this);
        totalPages = adapter.getItemCount();
        viewPager.setAdapter(adapter);

        addDotsIndicator(0);

        btnNext.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < totalPages - 1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                finishOnboarding();
            }
        });

        btnSkip.setOnClickListener(v -> finishOnboarding());

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                addDotsIndicator(position);
                if (position == totalPages - 1) {
                    btnNext.setText(R.string.str_sign_up);
                } else {
                    btnNext.setText(R.string.str_continue);
                }
            }
        });
    }

    private void finishOnboarding() {
        SharedPreferences prefs = getSharedPreferences("StudifyPrefs", MODE_PRIVATE);
        prefs.edit().putBoolean("firstTime", false).apply();
        Intent intent = new Intent(this, SignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void addDotsIndicator(int position) {
        dots = new TextView[totalPages];
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText("•");
            dots[i].setTextSize(35);
            dots[i].setPadding(8, 0, 8, 0);
            dots[i].setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > position && position >= 0) {
            dots[position].setTextColor(ContextCompat.getColor(this, R.color.teal_700));
        }
    }
}