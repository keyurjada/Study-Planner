package com.example.studyplanner.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studyplanner.R;
import com.example.studyplanner.models.ExamModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ExamViewHolder> {
    private final List<ExamModel> list;

    public ExamAdapter(List<ExamModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exam, parent, false);
        return new ExamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamViewHolder holder, int position) {
        ExamModel exam = list.get(position);
        if (exam == null) return;

        holder.examName.setText(exam.getName() != null ? exam.getName() : "Exam");
        holder.examDate.setText(exam.getDate() != null ? "📅 " + exam.getDate() : "");

        int score = exam.getReadinessScore();
        if (score > 0) {
            holder.txtReadinessBadge.setVisibility(View.VISIBLE);
            holder.txtReadinessBadge.setText("🎯 " + score + "% Ready");
            if (score >= 75) {
                holder.txtReadinessBadge.setBackgroundResource(R.drawable.bg_confidence_mastered_selected);
                holder.txtReadinessBadge.setTextColor(Color.parseColor("#16A34A"));
            } else if (score >= 40) {
                holder.txtReadinessBadge.setBackgroundResource(R.drawable.bg_confidence_shaky_selected);
                holder.txtReadinessBadge.setTextColor(Color.parseColor("#D97706"));
            } else {
                holder.txtReadinessBadge.setBackgroundResource(R.drawable.bg_badge_high_yield);
                holder.txtReadinessBadge.setTextColor(Color.parseColor("#DC2626"));
            }
        } else {
            String priority = exam.getPriority();
            if (priority != null && !priority.isEmpty()) {
                holder.txtReadinessBadge.setVisibility(View.VISIBLE);
                holder.txtReadinessBadge.setText(priority);
                if ("Important".equalsIgnoreCase(priority) || "High Stakes".equalsIgnoreCase(priority)) {
                    holder.txtReadinessBadge.setBackgroundResource(R.drawable.bg_badge_high_yield);
                    holder.txtReadinessBadge.setTextColor(Color.parseColor("#DC2626"));
                } else {
                    holder.txtReadinessBadge.setBackgroundResource(R.drawable.bg_confidence_shaky_selected);
                    holder.txtReadinessBadge.setTextColor(Color.parseColor("#D97706"));
                }
            } else {
                holder.txtReadinessBadge.setVisibility(View.GONE);
            }
        }

        if (exam.getTopics() != null && !exam.getTopics().isEmpty()) {
            holder.txtTopicSummary.setText("⚡ " + exam.getTopics().size() + " High-Yield Topics");
        } else if (exam.getSubject() != null && !exam.getSubject().isEmpty()) {
            holder.txtTopicSummary.setText("📚 " + exam.getSubject());
        } else {
            holder.txtTopicSummary.setText("⚡ Exam Attack Plan");
        }

        // Tap item to view full Attack Plan & Traps!
        holder.itemView.setOnClickListener(v -> showExamAttackPlanDialog(v.getContext(), exam));
    }

    private void showExamAttackPlanDialog(Context context, ExamModel exam) {
        StringBuilder sb = new StringBuilder();
        sb.append("📚 Subject: ").append(exam.getSubject() != null ? exam.getSubject() : "N/A").append("\n");
        sb.append("📅 Date: ").append(exam.getDate() != null ? exam.getDate() : "N/A").append("\n");
        if (exam.getReadinessScore() > 0) {
            sb.append("🎯 Readiness: ").append(exam.getReadinessScore()).append("%\n");
        }

        if (exam.getTraps() != null && !exam.getTraps().isEmpty()) {
            sb.append("\n⚠️ DEADLY PROFESSOR TRAPS:\n");
            int i = 1;
            for (String trap : exam.getTraps()) {
                sb.append(i++).append(". ").append(trap).append("\n\n");
            }
        } else if (exam.getTopics() != null && !exam.getTopics().isEmpty()) {
            sb.append("\n🎯 HIGH-YIELD TOPICS:\n");
            int i = 1;
            for (String topic : exam.getTopics()) {
                sb.append(i++).append(". ").append(topic).append("\n");
            }
        }

        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MyDialog)
                .setTitle(exam.getName() != null ? exam.getName() : "Exam Attack Plan")
                .setMessage(sb.toString().trim())
                .setPositiveButton("Got It", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ExamViewHolder extends RecyclerView.ViewHolder {
        TextView examName, examDate, txtReadinessBadge, txtTopicSummary;
        View examContainer;

        public ExamViewHolder(@NonNull View itemView) {
            super(itemView);
            examName = itemView.findViewById(R.id.examName);
            examDate = itemView.findViewById(R.id.examDate);
            txtReadinessBadge = itemView.findViewById(R.id.txtReadinessBadge);
            txtTopicSummary = itemView.findViewById(R.id.txtTopicSummary);
            examContainer = itemView.findViewById(R.id.examContainer);
        }
    }
}