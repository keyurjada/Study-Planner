package com.example.studyplanner.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private CalendarView calendarView;
    private TextView txtSelectedDate;
    private RecyclerView recyclerTasks;
    private RecyclerView recyclerExams;

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

        calendarView = binding.calendarView;
        txtSelectedDate = binding.txtSelectedDate;
        recyclerTasks = binding.recyclerTasks;
        recyclerExams = binding.recyclerExams;

        taskRepository = new TaskRepository();
        examRepository = new ExamRepository();

        taskList = new ArrayList<>();
        examList = new ArrayList<>();
        taskAdapter = new TaskCalendarAdapter(taskList);
        examAdapter = new ExamAdapter(examList);

        recyclerTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerTasks.setAdapter(taskAdapter);
        recyclerTasks.setLayoutAnimation(
                AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_animation_fall_down)
        );

        recyclerExams.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerExams.setAdapter(examAdapter);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = sdf.format(new Date());
        txtSelectedDate.setText("📍 " + selectedDate);

        loadTasks(selectedDate);
        loadExams(selectedDate);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            txtSelectedDate.setAlpha(0f);
            txtSelectedDate.setText("📍 " + selectedDate);
            txtSelectedDate.animate().alpha(1f).setDuration(300);
            loadTasks(selectedDate);
            loadExams(selectedDate);
        });

        return root;
    }

    private void loadTasks(String date) {
        if (taskListener != null) {
            taskListener.remove();
        }
        taskListener = taskRepository.getTasksForDate(date, false, tasks -> {
            taskList.clear();
            if (tasks != null) {
                taskList.addAll(tasks);
            }
            taskAdapter.notifyDataSetChanged();
            recyclerTasks.scheduleLayoutAnimation();
            if (taskList.isEmpty()) {
                txtSelectedDate.setText("📍 " + date + " - No tasks 🎉");
            } else {
                txtSelectedDate.setText("📍 " + date);
            }
        });
    }

    private void loadExams(String date) {
        if (examListener != null) {
            examListener.remove();
        }
        examListener = examRepository.getExamsForDate(date, false, exams -> {
            examList.clear();
            if (exams != null) {
                examList.addAll(exams);
            }
            examAdapter.notifyDataSetChanged();
            recyclerExams.scheduleLayoutAnimation();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (taskListener != null) {
            taskListener.remove();
        }
        if (examListener != null) {
            examListener.remove();
        }
    }
}