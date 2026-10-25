package com.example.studyplanner.adapters;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studyplanner.R;
import com.example.studyplanner.models.TaskModel;

import java.util.List;

public class TaskCalendarAdapter extends RecyclerView.Adapter<TaskCalendarAdapter.ViewHolder> {
    private final List<TaskModel> list;

    public TaskCalendarAdapter(List<TaskModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task_calendar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskModel task = list.get(position);
        if (task == null) return;

        holder.taskName.setText(task.getName() != null ? task.getName() : "Task");

        if (task.isCompleted()) {
            holder.taskName.setTextColor(androidx.core.content.ContextCompat.getColor(holder.itemView.getContext(), R.color.text_muted));
            holder.taskName.setPaintFlags(
                    holder.taskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
            );
            holder.taskName.setAlpha(0.6f);
        } else {
            holder.taskName.setTextColor(androidx.core.content.ContextCompat.getColor(holder.itemView.getContext(), R.color.text_primary));
            holder.taskName.setPaintFlags(
                    holder.taskName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG
            );
            holder.taskName.setAlpha(1f);
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.taskName);
        }
    }
}