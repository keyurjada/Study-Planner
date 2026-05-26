package com.example.studyplanner.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.studyplanner.R;
import com.example.studyplanner.auth.OnboardingActivity;
import com.example.studyplanner.auth.SignupActivity;
import com.example.studyplanner.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Handle the splash screen transition.
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences prefs = getSharedPreferences("StudifyPrefs", MODE_PRIVATE);
        boolean firstTime = prefs.getBoolean("firstTime", true);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        
        // Router logic
        if(firstTime){
            startActivity(new Intent(this, OnboardingActivity.class));
            finish();
            return;
        } else if(auth.getCurrentUser() == null){
            startActivity(new Intent(this, SignupActivity.class));
            finish();
            return;
        }

        // If logged in, setup Bottom Navigation with NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            
            NavigationUI.setupWithNavController(binding.bottomNav, navController);

            FloatingActionButton fabAdd = binding.fabAdd;
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getId() == R.id.nav_home) {
                    fabAdd.show();
                } else {
                    fabAdd.hide();
                }
            });

            fabAdd.setOnClickListener(v -> {
                v.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fab_bounce));
                BottomSheetDialog dialog = new BottomSheetDialog(this);
                View view = getLayoutInflater().inflate(R.layout.bottom_sheet_add, null);
                dialog.setContentView(view);
                view.findViewById(R.id.btnTask).setOnClickListener(v1 -> {
                    startActivity(new Intent(this, AddTaskActivity.class));
                    dialog.dismiss();
                });
                view.findViewById(R.id.btnExam).setOnClickListener(v1 -> {
                    startActivity(new Intent(this, AddExamActivity.class));
                    dialog.dismiss();
                });
                dialog.show();
            });

        }
    }
}