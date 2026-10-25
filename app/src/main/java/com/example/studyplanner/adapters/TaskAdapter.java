package com.example.studyplanner.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studyplanner.R;
import com.example.studyplanner.models.TaskModel;
import com.example.studyplanner.ui.SprintTimerActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private final List<TaskModel> list;

    public TaskAdapter(List<TaskModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskModel task = list.get(position);
        if (task == null) return;

        holder.taskName.setText(task.getName() != null ? task.getName() : "Task");
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(task.isCompleted());

        // Tags
        String subject = task.getSubject() != null && !task.getSubject().isEmpty() ? task.getSubject() : "Study";
        holder.txtTaskSubject.setText(subject);

        int duration = task.getDurationMinutes() > 0 ? task.getDurationMinutes() : 25;
        holder.txtTaskDuration.setText("⚡ " + duration + "m");

        String energy = task.getEnergyLevel();
        if (energy != null && !energy.isEmpty()) {
            holder.txtTaskEnergy.setVisibility(View.VISIBLE);
            if (energy.contains("Deep")) {
                holder.txtTaskEnergy.setText("🧠 Deep");
            } else if (energy.contains("Balanced")) {
                holder.txtTaskEnergy.setText("⚡ Balanced");
            } else {
                holder.txtTaskEnergy.setText("☕ Quick");
            }
        } else {
            holder.txtTaskEnergy.setVisibility(View.GONE);
        }

        // Kickstart snippet
        String kickstart = task.getKickstartStep();
        if (kickstart != null && !kickstart.trim().isEmpty() && !task.isCompleted()) {
            holder.txtTaskKickstart.setVisibility(View.VISIBLE);
            holder.txtTaskKickstart.setText("🎯 First: " + kickstart);
        } else {
            holder.txtTaskKickstart.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            v.animate().scaleX(0.96f).scaleY(0.96f).setDuration(80).withEndAction(() ->
                    v.animate().scaleX(1f).scaleY(1f).setDuration(80)
            );
        });

        if (task.isCompleted()) {
            holder.taskName.setTextColor(androidx.core.content.ContextCompat.getColor(holder.itemView.getContext(), R.color.text_muted));
            holder.taskName.setPaintFlags(
                    holder.taskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
            );
            holder.btnPlaySprint.setVisibility(View.GONE);
        } else {
            holder.taskName.setTextColor(androidx.core.content.ContextCompat.getColor(holder.itemView.getContext(), R.color.text_primary));
            holder.taskName.setPaintFlags(
                    holder.taskName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG
            );
            holder.btnPlaySprint.setVisibility(View.VISIBLE);
            holder.btnPlaySprint.setOnClickListener(v -> {
                Intent timerIntent = new Intent(holder.itemView.getContext(), SprintTimerActivity.class);
                timerIntent.putExtra(SprintTimerActivity.EXTRA_TASK_ID, task.getId());
                timerIntent.putExtra(SprintTimerActivity.EXTRA_TASK_NAME, task.getName());
                timerIntent.putExtra(SprintTimerActivity.EXTRA_TASK_SUBJECT, task.getSubject());
                timerIntent.putExtra(SprintTimerActivity.EXTRA_TASK_DURATION, task.getDurationMinutes());
                timerIntent.putExtra(SprintTimerActivity.EXTRA_TASK_KICKSTART, task.getKickstartStep());
                timerIntent.putExtra(SprintTimerActivity.EXTRA_TASK_ENERGY, task.getEnergyLevel());
                timerIntent.putExtra(SprintTimerActivity.EXTRA_TASK_TYPE, task.getMissionType());
                holder.itemView.getContext().startActivity(timerIntent);
            });
        }

        holder.checkBox.setOnClickListener(v -> {
            holder.checkBox.setChecked(false);
            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION && currentPosition < list.size()) {
                showConfirmDialog(holder.itemView, task);
            }
        });
    }

    private void showConfirmDialog(View view, TaskModel task) {
        new MaterialAlertDialogBuilder(view.getContext())
                .setTitle("Complete Mission 🎉")
                .setMessage("Mark \"" + (task.getName() != null ? task.getName() : "this mission") + "\" as completed?")
                .setIcon(android.R.drawable.checkbox_on_background)
                .setPositiveButton("Yes", (dialog, which) -> markTaskCompleted(view, task))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void markTaskCompleted(View view, TaskModel task) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || task.getId() == null) {
            Toast.makeText(view.getContext(), "Unable to update mission", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore.getInstance().collection("users")
                .document(user.getUid())
                .collection("tasks")
                .document(task.getId())
                .update("completed", true)
                .addOnFailureListener(e -> {
                    Toast.makeText(view.getContext(), "Failed to update: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskName;
        CheckBox checkBox;
        TextView txtTaskSubject;
        TextView txtTaskDuration;
        TextView txtTaskEnergy;
        TextView txtTaskKickstart;
        ImageView btnPlaySprint;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.taskName);
            checkBox = itemView.findViewById(R.id.checkTask);
            txtTaskSubject = itemView.findViewById(R.id.txtTaskSubject);
            txtTaskDuration = itemView.findViewById(R.id.txtTaskDuration);
            txtTaskEnergy = itemView.findViewById(R.id.txtTaskEnergy);
            txtTaskKickstart = itemView.findViewById(R.id.txtTaskKickstart);
            btnPlaySprint = itemView.findViewById(R.id.btnPlaySprint);
        }
    }
}