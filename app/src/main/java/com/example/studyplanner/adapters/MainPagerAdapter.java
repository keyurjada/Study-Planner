package com.example.studyplanner.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.studyplanner.ui.CalendarFragment;
import com.example.studyplanner.ui.DashboardFragment;
import com.example.studyplanner.ui.FocusFragment;
import com.example.studyplanner.ui.ProfileFragment;

/**
 * Adapter providing horizontal swipe navigation across the 4 primary app tabs:
 * 0: Home (Dashboard)
 * 1: Calendar
 * 2: Focus Sprint
 * 3: Profile & Scholar Rank
 */
public class MainPagerAdapter extends FragmentStateAdapter {

    public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new CalendarFragment();
            case 2:
                return new FocusFragment();
            case 3:
                return new ProfileFragment();
            case 0:
            default:
                return new DashboardFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
