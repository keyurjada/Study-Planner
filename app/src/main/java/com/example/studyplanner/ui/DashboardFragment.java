package com.example.studyplanner.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;
    private List<TaskModel> taskList;
    private TaskAdapter taskAdapter;
    private List<ExamModel> examList;
    private ExamAdapter examAdapter;
    private TaskRepository taskRepository;
    private ExamRepository examRepository;
    private UserRepository userRepository;
    private ListenerRegistration userListener, taskListener, examListener, progressListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        taskRepository = new TaskRepository();
        examRepository = new ExamRepository();
        userRepository = new UserRepository();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return root;
        }

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour < 12) {
            binding.txtGreeting.setText(R.string.str_good_morning);
        } else if (hour < 17) {
            binding.txtGreeting.setText(R.string.str_good_afternoon);
        } else {
            binding.txtGreeting.setText(R.string.str_good_evening);
        }
        binding.txtName.setText(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "User");
        binding.imgProfileDashboard.setImageResource(R.drawable.logo);

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList);
        binding.recyclerTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerTasks.setAdapter(taskAdapter);
        binding.recyclerTasks.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerTasks.setNestedScrollingEnabled(false);

        examList = new ArrayList<>();
        examAdapter = new ExamAdapter(examList);
        binding.recyclerExams.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerExams.setAdapter(examAdapter);
        binding.recyclerExams.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerExams.setNestedScrollingEnabled(false);

        loadUserData();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            loadTasksRealtime();
            loadExamsRealtime();
            loadProgressRealtime();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        removeListeners();
    }

    private void removeListeners() {
        if (userListener != null) {
            userListener.remove();
            userListener = null;
        }
        if (taskListener != null) {
            taskListener.remove();
            taskListener = null;
        }
        if (examListener != null) {
            examListener.remove();
            examListener = null;
        }
        if (progressListener != null) {
            progressListener.remove();
            progressListener = null;
        }
    }

    private void loadUserData() {
        userListener = userRepository.getUserData((name, email) -> {
            if (!isAdded() || binding == null) return;
            if (name != null && !name.trim().isEmpty()) {
                binding.txtName.setText(name);
            }
        });
    }

    private void loadTasksRealtime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = sdf.format(new Date());
        taskListener = taskRepository.getTasksForDate(todayDate, true, tasks -> {
            if (!isAdded() || binding == null) return;
            taskList.clear();
            if (tasks != null) {
                taskList.addAll(tasks);
            }
            taskAdapter.notifyDataSetChanged();
            binding.recyclerTasks.scheduleLayoutAnimation();
        });
    }

    private void loadProgressRealtime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = sdf.format(new Date());
        progressListener = taskRepository.getProgressForDate(todayDate, (completed, total) -> {
            if (!isAdded() || binding == null) return;
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
            if (!isAdded() || binding == null) return;
            examList.clear();
            if (exams != null) {
                examList.addAll(exams);
            }
            examAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeListeners();
        binding = null;
    }
}