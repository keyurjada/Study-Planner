package com.example.studyplanner.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studyplanner.R;
import com.example.studyplanner.adapters.ExamAdapter;
import com.example.studyplanner.adapters.TaskCalendarAdapter;
import com.example.studyplanner.data.repository.ExamRepository;
import com.example.studyplanner.data.repository.TaskRepository;
import com.example.studyplanner.databinding.FragmentCalendarBinding;
import com.example.studyplanner.models.ExamModel;
import com.example.studyplanner.models.TaskModel;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {
    private FragmentCalendarBinding binding;
    private List<TaskModel> taskList;
    private List<ExamModel> examList;
    private TaskCalendarAdapter taskAdapter;
    private ExamAdapter examAdapter;
    private TaskRepository taskRepository;
    private ExamRepository examRepository;
    private String selectedDate;
    private ListenerRegistration taskListener;
    private ListenerRegistration examListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        taskRepository = new TaskRepository();
        examRepository = new ExamRepository();

        taskList = new ArrayList<>();
        examList = new ArrayList<>();
        taskAdapter = new TaskCalendarAdapter(taskList);
        examAdapter = new ExamAdapter(examList);

        binding.recyclerTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerTasks.setAdapter(taskAdapter);
        binding.recyclerTasks.setLayoutAnimation(
                AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_animation_fall_down)
        );

        binding.recyclerExams.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerExams.setAdapter(examAdapter);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = sdf.format(new Date());
        binding.txtSelectedDate.setText("📍 " + selectedDate);

        loadTasks(selectedDate);
        loadExams(selectedDate);

        binding.calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            if (binding != null) {
                binding.txtSelectedDate.setAlpha(0f);
                binding.txtSelectedDate.setText("📍 " + selectedDate);
                binding.txtSelectedDate.animate().alpha(1f).setDuration(300);
            }
            loadTasks(selectedDate);
            loadExams(selectedDate);
        });

        return root;
    }

    private void loadTasks(String date) {
        if (taskListener != null) {
            taskListener.remove();
            taskListener = null;
        }
        taskListener = taskRepository.getTasksForDate(date, false, tasks -> {
            if (!isAdded() || binding == null) return;
            taskList.clear();
            if (tasks != null) {
                taskList.addAll(tasks);
            }
            taskAdapter.notifyDataSetChanged();
            binding.recyclerTasks.scheduleLayoutAnimation();
            if (taskList.isEmpty()) {
                binding.txtSelectedDate.setText("📍 " + date + " - No tasks 🎉");
            } else {
                binding.txtSelectedDate.setText("📍 " + date);
            }
        });
    }

    private void loadExams(String date) {
        if (examListener != null) {
            examListener.remove();
            examListener = null;
        }
        examListener = examRepository.getExamsForDate(date, false, exams -> {
            if (!isAdded() || binding == null) return;
            examList.clear();
            if (exams != null) {
                examList.addAll(exams);
            }
            examAdapter.notifyDataSetChanged();
            binding.recyclerExams.scheduleLayoutAnimation();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (taskListener != null) {
            taskListener.remove();
            taskListener = null;
        }
        if (examListener != null) {
            examListener.remove();
            examListener = null;
        }
        binding = null;
    }
}