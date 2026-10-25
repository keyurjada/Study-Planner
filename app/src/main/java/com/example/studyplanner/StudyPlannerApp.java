package com.example.studyplanner;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class StudyPlannerApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Check user's saved theme preference, defaulting to system theme
        SharedPreferences prefs = getSharedPreferences("StudifyPrefs", MODE_PRIVATE);
        if (prefs.contains("dark_mode")) {
            boolean isDark = prefs.getBoolean("dark_mode", false);
            AppCompatDelegate.setDefaultNightMode(isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }
}
