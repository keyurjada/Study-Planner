package com.example.studyplanner.ui;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studyplanner.R;
import com.example.studyplanner.adapters.ExamAdapter;
import com.example.studyplanner.adapters.TaskAdapter;
import com.example.studyplanner.data.repository.ExamRepository;
import com.example.studyplanner.data.repository.TaskRepository;
import com.example.studyplanner.data.repository.UserRepository;
import com.example.studyplanner.databinding.FragmentDashboardBinding;
import com.example.studyplanner.models.ExamModel;
import com.example.studyplanner.models.TaskModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {
    List<TaskModel> taskList;
    TaskAdapter adapter;
    List<ExamModel> examList;
    ExamAdapter examAdapter;
    FirebaseFirestore db;
    TaskRepository taskRepository;
    ExamRepository examRepository;
    UserRepository userRepository;
    String userId;
    ListenerRegistration taskListener,examListener,progressListener;
    private FragmentDashboardBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        
        
        
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        db = FirebaseFirestore.getInstance();
        taskRepository = new TaskRepository();
        examRepository = new ExamRepository();
        userRepository = new UserRepository();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        
        binding.imgProfileDashboard.setImageResource(R.drawable.logo);
        if(hour < 12){
            binding.txtGreeting.setText(R.string.str_good_morning);
        } else if(hour < 17){
            binding.txtGreeting.setText(R.string.str_good_afternoon);
        } else {
            binding.txtGreeting.setText(R.string.str_good_evening);
        }
        binding.txtName.setText("User");
        loadUserData();
        
        taskList = new ArrayList<>();
        adapter = new TaskAdapter(taskList);
        binding.recyclerTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerTasks.setAdapter(adapter);
        binding.recyclerTasks.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerTasks.setNestedScrollingEnabled(false);
        binding.recyclerTasks.getItemAnimator().setAddDuration(300);
        binding.recyclerTasks.getItemAnimator().setMoveDuration(300);
        
        examList = new ArrayList<>();
        examAdapter = new ExamAdapter(examList);
        binding.recyclerExams.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerExams.setAdapter(examAdapter);
        binding.recyclerExams.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerExams.setNestedScrollingEnabled(false);
        
        
        return root;
    }
    @Override
    public void onStart() {
        super.onStart();
        loadTasksRealtime();
        loadExamsRealtime();
        loadProgressRealtime();
    }
    @Override
    public void onStop() {
        super.onStop();
        if (taskListener != null) taskListener.remove();
        if (examListener != null) examListener.remove();
        if (progressListener != null) progressListener.remove();
    }
    private void loadUserData() {
        userRepository.getUserData((name, email) -> {
            if (name != null && !name.isEmpty()) {
                binding.txtName.setText(name);
            }
        });
    }
    private void loadTasksRealtime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = sdf.format(new Date());
        taskListener = taskRepository.getTasksForDate(todayDate, true, tasks -> {
            taskList.clear();
            taskList.addAll(tasks);
            adapter.notifyDataSetChanged();
            binding.recyclerTasks.scheduleLayoutAnimation();
        });
    }
    private void loadProgressRealtime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = sdf.format(new Date());
        progressListener = taskRepository.getProgressForDate(todayDate, (completed, total) -> {
            if (total == 0) {
                binding.txtProgress.setText(R.string.str_no_tasks_today);
                binding.progressBar.setProgress(0);
                binding.txtCompletion.setText("0%");
                return;
            }
            int progress = (completed * 100) / total;
            binding.txtProgress.setText(completed + " / " + total + " tasks");
            binding.progressBar.setProgress(progress);
            binding.txtCompletion.setText(progress + "%");
        });
    }
    private void loadExamsRealtime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = sdf.format(new Date());
        examListener = examRepository.getExamsForDate(todayDate, true, exams -> {
            examList.clear();
            examList.addAll(exams);
            examAdapter.notifyDataSetChanged();
        });
    }
}