package com.example.studyplanner.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.studyplanner.data.repository.ExamRepository;
import com.example.studyplanner.data.repository.TaskRepository;
import com.example.studyplanner.databinding.FragmentFocusBinding;
import com.example.studyplanner.models.ExamModel;
import com.example.studyplanner.models.TaskModel;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FocusFragment extends Fragment {
    private FragmentFocusBinding binding;
    private TaskRepository taskRepository;
    private ExamRepository examRepository;
    private String nearestExamName = "No upcoming exams";
    private long nearestDays = Long.MAX_VALUE;
    private final List<TaskModel> pendingTasks = new ArrayList<>();
    private ListenerRegistration examListener, taskListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFocusBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        taskRepository = new TaskRepository();
        examRepository = new ExamRepository();

        loadExams();
        loadTasks();

        return root;
    }

    private void loadExams() {
        if (examListener != null) {
            examListener.remove();
            examListener = null;
        }
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        examListener = examRepository.getExamsForDate(today, true, exams -> {
            if (!isAdded() || binding == null) return;
            nearestDays = Long.MAX_VALUE;
            nearestExamName = "No upcoming exams";
            if (exams != null) {
                for (ExamModel exam : exams) {
                    String dateStr = exam.getDate();
                    String name = exam.getName();
                    if (dateStr == null) continue;
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Date examDate = sdf.parse(dateStr);
                        Date todayDate = sdf.parse(today);
                        if (examDate != null && todayDate != null) {
                            long diff = (examDate.getTime() - todayDate.getTime()) / (1000 * 60 * 60 * 24);
                            if (diff >= 0 && diff < nearestDays) {
                                nearestDays = diff;
                                nearestExamName = name != null ? name : "Exam";
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            updateExamUI();
            generateSuggestion();
        });
    }

    private void updateExamUI() {
        if (binding == null) return;
        binding.txtExamName.setText(nearestExamName);
        if (nearestDays == Long.MAX_VALUE) {
            binding.txtExamDays.setText("No upcoming exams");
        } else if (nearestDays == 0) {
            binding.txtExamDays.setText("Today! 🎯");
        } else if (nearestDays == 1) {
            binding.txtExamDays.setText("Tomorrow! ⚡");
        } else {
            binding.txtExamDays.setText("In " + nearestDays + " days");
        }
    }

    private void loadTasks() {
        if (taskListener != null) {
            taskListener.remove();
            taskListener = null;
        }
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        taskListener = taskRepository.getTasksForDate(today, true, tasks -> {
            if (!isAdded() || binding == null) return;
            pendingTasks.clear();
            if (tasks != null) {
                for (TaskModel task : tasks) {
                    if (task != null && task.getName() != null) {
                        pendingTasks.add(task);
                    }
                }
            }
            updateTasksUI();
            generateSuggestion();
        });
    }

    private void updateTasksUI() {
        if (binding == null) return;
        if (pendingTasks.isEmpty()) {
            binding.txtTasks.setText("No pending missions today 🎉");
        } else {
            StringBuilder builder = new StringBuilder();
            for (TaskModel task : pendingTasks) {
                builder.append("• ").append(task.getName());
                if (task.getDurationMinutes() > 0) {
                    builder.append(" (⚡ ").append(task.getDurationMinutes()).append("m)");
                }
                if (task.getKickstartStep() != null && !task.getKickstartStep().trim().isEmpty()) {
                    builder.append("\n  🎯 Kickstart: ").append(task.getKickstartStep());
                }
                builder.append("\n");
            }
            binding.txtTasks.setText(builder.toString().trim());
        }
    }

    private void generateSuggestion() {
        if (binding == null) return;
        String suggestion;
        if (!pendingTasks.isEmpty()) {
            TaskModel first = pendingTasks.get(0);
            String kick = first.getKickstartStep();
            if (kick != null && !kick.trim().isEmpty()) {
                suggestion = "🚀 Launch Sprint: " + first.getName() + "\n🎯 2-Min Action: " + kick + " (Tap to Start)";
            } else {
                suggestion = "🚀 Start Sprint: " + first.getName() + " (Tap to Start)";
            }

            binding.txtSuggestion.setOnClickListener(v -> {
                Intent timerIntent = new Intent(requireContext(), SprintTimerActivity.class);
                timerIntent.putExtra(SprintTimerActivity.EXTRA_TASK_ID, first.getId());
                timerIntent.putExtra(SprintTimerActivity.EXTRA_TASK_NAME, first.getName());
                timerIntent.putExtra(SprintTimerActivity.EXTRA_TASK_SUBJECT, first.getSubject());
                timerIntent.putExtra(SprintTimerActivity.EXTRA_TASK_DURATION, first.getDurationMinutes());
                timerIntent.putExtra(SprintTimerActivity.EXTRA_TASK_KICKSTART, first.getKickstartStep());
                timerIntent.putExtra(SprintTimerActivity.EXTRA_TASK_ENERGY, first.getEnergyLevel());
                timerIntent.putExtra(SprintTimerActivity.EXTRA_TASK_TYPE, first.getMissionType());
                startActivity(timerIntent);
            });
        } else if (nearestDays != Long.MAX_VALUE) {
            suggestion = "Prepare for " + nearestExamName;
            binding.txtSuggestion.setOnClickListener(null);
        } else {
            suggestion = "You're all caught up. Take a break!";
            binding.txtSuggestion.setOnClickListener(null);
        }
        binding.txtSuggestion.setText(suggestion);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (examListener != null) {
            examListener.remove();
            examListener = null;
        }
        if (taskListener != null) {
            taskListener.remove();
            taskListener = null;
        }
        binding = null;
    }
}