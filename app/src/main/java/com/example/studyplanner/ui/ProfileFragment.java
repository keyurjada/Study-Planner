package com.example.studyplanner.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studyplanner.R;
import com.example.studyplanner.adapters.HistoryAdapter;
import com.example.studyplanner.auth.LoginActivity;
import com.example.studyplanner.data.repository.UserRepository;
import com.example.studyplanner.databinding.FragmentProfileBinding;
import com.example.studyplanner.models.HistoryModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private HistoryAdapter adapter;
    private List<HistoryModel> list;
    private UserRepository userRepository;
    private ListenerRegistration userListener;
    private ListenerRegistration historyListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        userRepository = new UserRepository();
        list = new ArrayList<>();
        adapter = new HistoryAdapter(requireContext(), list);

        binding.recyclerHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerHistory.setItemAnimator(null);
        binding.recyclerHistory.setAdapter(adapter);

        binding.btnLogout.setOnClickListener(v -> {
            if (!isAdded()) return;
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        if (getActivity() != null) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        // Dark Mode Interactive Toggle
        android.content.SharedPreferences prefs = requireContext().getSharedPreferences("StudifyPrefs", android.content.Context.MODE_PRIVATE);
        int currentNightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        boolean isSystemDark = (currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES);
        boolean isDark = prefs.getBoolean("dark_mode", isSystemDark);

        binding.switchDarkMode.setChecked(isDark);
        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                    isChecked ? androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES : androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        loadUser();
        loadHistory();

        return root;
    }

    private void loadUser() {
        if (userListener != null) {
            userListener.remove();
            userListener = null;
        }
        userListener = userRepository.getUserData((name, email) -> {
            if (!isAdded() || binding == null) return;
            if (name != null) binding.txtName.setText(name);
            if (email != null) binding.txtEmail.setText(email);
            binding.imgProfile.setImageResource(R.drawable.logo);
        });
    }

    private void loadHistory() {
        if (historyListener != null) {
            historyListener.remove();
            historyListener = null;
        }
        historyListener = userRepository.getHistory(history -> {
            if (!isAdded() || binding == null) return;
            if (history == null || history.isEmpty()) {
                binding.txtEmpty.setVisibility(View.VISIBLE);
            } else {
                binding.txtEmpty.setVisibility(View.GONE);
            }
            adapter.setList(history != null ? history : new ArrayList<>());
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userListener != null) {
            userListener.remove();
            userListener = null;
        }
        if (historyListener != null) {
            historyListener.remove();
            historyListener = null;
        }
        binding = null;
    }
}