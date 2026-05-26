package com.example.studyplanner.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.studyplanner.data.repository.ExamRepository;
import com.example.studyplanner.data.repository.TaskRepository;
import com.example.studyplanner.databinding.FragmentFocusBinding;
import com.example.studyplanner.models.ExamModel;
import com.example.studyplanner.models.TaskModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FocusFragment extends Fragment {
    private FragmentFocusBinding binding;
    TextView txtExamName, txtExamDays, txtTasks, txtSuggestion;
    FirebaseAuth auth;
    FirebaseFirestore db;
    TaskRepository taskRepository;
    ExamRepository examRepository;
    String nearestExamName = "No upcoming exams";
    long nearestDays = Long.MAX_VALUE;
    List<String> pendingTasks = new ArrayList<>();
    ListenerRegistration examListener, taskListener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFocusBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        txtExamName = binding.txtExamName;
        txtExamDays = binding.txtExamDays;
        txtTasks = binding.txtTasks;
        txtSuggestion = binding.txtSuggestion;
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        taskRepository = new TaskRepository();
        examRepository = new ExamRepository();
        loadExams();
        loadTasks();
        
        return root;
    }
    private void loadExams() {
        if (examListener != null) examListener.remove();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        examListener = examRepository.getExamsForDate(today, true, exams -> {
            nearestDays = Long.MAX_VALUE;
            nearestExamName = "No upcoming exams";
            for (ExamModel exam : exams) {
                String dateStr = exam.getDate();
                String name = exam.getName();
                if (dateStr == null) continue;
                try {
                    Date examDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr);
                    Date todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(today);
                    long diff = (examDate.getTime() - todayDate.getTime()) / (1000 * 60 * 60 * 24);
                    if (diff >= 0 && diff < nearestDays) {
                        nearestDays = diff;
                        nearestExamName = name;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            updateExamUI();
            generateSuggestion();
        });
    }
    private void updateExamUI() {
        txtExamName.setText(nearestExamName);
        if (nearestDays == Long.MAX_VALUE) {
            txtExamDays.setText("No upcoming exams");
        } else {
            txtExamDays.setText("In " + nearestDays + " days");
        }
    }
    private void loadTasks() {
        if (taskListener != null) taskListener.remove();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        taskListener = taskRepository.getTasksForDate(today, true, tasks -> {
            pendingTasks.clear();
            for (TaskModel task : tasks) {
                pendingTasks.add(task.getName());
            }
            updateTasksUI();
            generateSuggestion();
        });
    }
    private void updateTasksUI() {
        if (pendingTasks.isEmpty()) {
            txtTasks.setText("No pending tasks today 🎉");
        } else {
            StringBuilder builder = new StringBuilder();
            for (String task : pendingTasks) {
                builder.append("• ").append(task).append("\n");
            }
            txtTasks.setText(builder.toString());
        }
    }
    private void generateSuggestion() {
        String suggestion;
        if (!pendingTasks.isEmpty()) {
            suggestion = "Start: " + pendingTasks.get(0);
        } else if (nearestDays != Long.MAX_VALUE) {
            suggestion = "Prepare for " + nearestExamName;
        } else {
            suggestion = "You're all caught up. Take a break!";
        }
        txtSuggestion.setText(suggestion);
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (examListener != null) examListener.remove();
        if (taskListener != null) taskListener.remove();
    }
}