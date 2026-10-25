package com.example.studyplanner.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.viewpager2.widget.ViewPager2;

import com.example.studyplanner.R;
import com.example.studyplanner.adapters.MainPagerAdapter;
import com.example.studyplanner.auth.LoginActivity;
import com.example.studyplanner.auth.OnboardingActivity;
import com.example.studyplanner.databinding.ActivityMainBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Handle splash screen transition
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        // Pre-inflation auth & onboarding routing checks to avoid unnecessary view/fragment lifecycle execution
        SharedPreferences prefs = getSharedPreferences("StudifyPrefs", MODE_PRIVATE);
        boolean firstTime = prefs.getBoolean("firstTime", true);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (firstTime) {
            startActivity(new Intent(this, OnboardingActivity.class));
            finish();
            return;
        } else if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FloatingActionButton fabAdd = binding.fabAdd;

        // Setup ViewPager2 with MainPagerAdapter for swipeable screen navigation
        MainPagerAdapter pagerAdapter = new MainPagerAdapter(this);
        binding.viewPagerMain.setAdapter(pagerAdapter);
        binding.viewPagerMain.setOffscreenPageLimit(3); // Keep all 4 tabs responsive & alive in memory

        // Sync ViewPager2 horizontal page swipes with BottomNavigationView
        binding.viewPagerMain.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                int itemId;
                switch (position) {
                    case 1:
                        itemId = R.id.nav_calendar;
                        break;
                    case 2:
                        itemId = R.id.nav_focus;
                        break;
                    case 3:
                        itemId = R.id.nav_profile;
                        break;
                    case 0:
                    default:
                        itemId = R.id.nav_home;
                        break;
                }

                if (binding.bottomNav.getSelectedItemId() != itemId) {
                    binding.bottomNav.setSelectedItemId(itemId);
                }

                if (position == 0) {
                    fabAdd.show();
                } else {
                    fabAdd.hide();
                }
            }
        });

        // Sync BottomNavigationView tab selection with ViewPager2
        binding.bottomNav.setOnItemSelectedListener(item -> {
            int targetIndex;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                targetIndex = 0;
            } else if (itemId == R.id.nav_calendar) {
                targetIndex = 1;
            } else if (itemId == R.id.nav_focus) {
                targetIndex = 2;
            } else if (itemId == R.id.nav_profile) {
                targetIndex = 3;
            } else {
                return false;
            }

            if (binding.viewPagerMain.getCurrentItem() != targetIndex) {
                binding.viewPagerMain.setCurrentItem(targetIndex, true);
            }
            return true;
        });

        // Optional convenience: swipe gesture detection directly across the bottom navigation bar itself
        GestureDetector bottomNavGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 60;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null || e2 == null) return false;
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();
                if (Math.abs(diffX) > Math.abs(diffY)
                        && Math.abs(diffX) > SWIPE_THRESHOLD
                        && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    int currentItem = binding.viewPagerMain.getCurrentItem();
                    if (diffX < 0 && currentItem < 3) {
                        // Swiped Left -> go to next tab
                        binding.viewPagerMain.setCurrentItem(currentItem + 1, true);
                        return true;
                    } else if (diffX > 0 && currentItem > 0) {
                        // Swiped Right -> go to previous tab
                        binding.viewPagerMain.setCurrentItem(currentItem - 1, true);
                        return true;
                    }
                }
                return false;
            }
        });

        binding.bottomNav.setOnTouchListener((v, event) -> {
            bottomNavGestureDetector.onTouchEvent(event);
            return false; // Return false so tab item clicks continue to process normally
        });

        // Ergonomic back press: return to Home tab first before exiting
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.viewPagerMain.getCurrentItem() != 0) {
                    binding.viewPagerMain.setCurrentItem(0, true);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        fabAdd.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fab_bounce));
            BottomSheetDialog dialog = new BottomSheetDialog(this);
            View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_add, null);
            dialog.setContentView(sheetView);

            View btnTask = sheetView.findViewById(R.id.btnTask);
            if (btnTask != null) {
                btnTask.setOnClickListener(v1 -> {
                    startActivity(new Intent(this, AddTaskActivity.class));
                    dialog.dismiss();
                });
            }

            View btnExam = sheetView.findViewById(R.id.btnExam);
            if (btnExam != null) {
                btnExam.setOnClickListener(v1 -> {
                    startActivity(new Intent(this, AddExamActivity.class));
                    dialog.dismiss();
                });
            }

            dialog.show();
        });
    }
}