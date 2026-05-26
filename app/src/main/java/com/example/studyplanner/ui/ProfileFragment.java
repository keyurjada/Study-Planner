package com.example.studyplanner.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studyplanner.R;
import com.example.studyplanner.adapters.HistoryAdapter;
import com.example.studyplanner.data.repository.UserRepository;
import com.example.studyplanner.databinding.FragmentProfileBinding;
import com.example.studyplanner.models.HistoryModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    ImageView imgProfile;
    TextView txtName, txtEmail, txtEmpty;
    RecyclerView recyclerHistory;
    Button btnLogout;
    HistoryAdapter adapter;
    List<HistoryModel> list;
    FirebaseFirestore db;
    UserRepository userRepository;
    String userId;

    @Override
    public void onStart() {
        super.onStart();
        loadUser();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        imgProfile = binding.imgProfile;
        txtName = binding.txtName;
        txtEmail = binding.txtEmail;
        txtEmpty = binding.txtEmpty;
        recyclerHistory = binding.recyclerHistory;
        btnLogout = binding.btnLogout;
        recyclerHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        list = new ArrayList<>();
        adapter = new HistoryAdapter(requireContext(), list);
        recyclerHistory.setItemAnimator(null);
        recyclerHistory.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();
        userRepository = new UserRepository();
        userId = FirebaseAuth.getInstance().getUid();
        loadHistory();
        btnLogout.setOnClickListener(v -> {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(requireContext(), com.example.studyplanner.auth.LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });
        
        return root;
    }
    private void loadUser(){
        userRepository.getUserData((name, email) -> {
            if (name != null) txtName.setText(name);
            if (email != null) txtEmail.setText(email);
            imgProfile.setImageResource(R.drawable.logo);
        });
    }
    private void loadHistory(){
        userRepository.getHistory(history -> {
            if (history.isEmpty()) {
                txtEmpty.setVisibility(View.VISIBLE);
            } else {
                txtEmpty.setVisibility(View.GONE);
            }
            adapter.setList(history);
        });
    }
    
}