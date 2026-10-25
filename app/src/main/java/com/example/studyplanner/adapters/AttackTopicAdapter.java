package com.example.studyplanner.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studyplanner.R;
import com.example.studyplanner.databinding.ItemAttackTopicBinding;
import com.example.studyplanner.models.ExamTopic;

import java.util.List;

public class AttackTopicAdapter extends RecyclerView.Adapter<AttackTopicAdapter.TopicViewHolder> {

    public interface OnConfidenceChangedListener {
        void onConfidenceChanged();
    }

    private final List<ExamTopic> topics;
    private final OnConfidenceChangedListener listener;

    public AttackTopicAdapter(List<ExamTopic> topics, OnConfidenceChangedListener listener) {
        this.topics = topics;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAttackTopicBinding binding = ItemAttackTopicBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new TopicViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        ExamTopic topic = topics.get(position);
        if (topic == null) return;

        holder.binding.txtTopicIndex.setText("#" + (position + 1));
        holder.binding.txtTopicTitle.setText(topic.getTitle());
        holder.binding.txtTopicWeight.setText(topic.getWeight());
        holder.binding.txtTrapContent.setText(topic.getTrap());

        if (topic.getWeight() != null && topic.getWeight().contains("Core")) {
            holder.binding.txtTopicWeight.setBackgroundResource(R.drawable.bg_badge_core);
            holder.binding.txtTopicWeight.setTextColor(Color.parseColor("#4338CA"));
        } else {
            holder.binding.txtTopicWeight.setBackgroundResource(R.drawable.bg_badge_high_yield);
            holder.binding.txtTopicWeight.setTextColor(Color.parseColor("#DC2626"));
        }

        updateConfidenceButtons(holder, topic.getConfidenceState());

        holder.binding.btnConfidenceWeak.setOnClickListener(v -> {
            animateClick(v);
            topic.setConfidenceState(ExamTopic.CONFIDENCE_WEAK);
            updateConfidenceButtons(holder, ExamTopic.CONFIDENCE_WEAK);
            if (listener != null) listener.onConfidenceChanged();
        });

        holder.binding.btnConfidenceShaky.setOnClickListener(v -> {
            animateClick(v);
            topic.setConfidenceState(ExamTopic.CONFIDENCE_SHAKY);
            updateConfidenceButtons(holder, ExamTopic.CONFIDENCE_SHAKY);
            if (listener != null) listener.onConfidenceChanged();
        });

        holder.binding.btnConfidenceMastered.setOnClickListener(v -> {
            animateClick(v);
            topic.setConfidenceState(ExamTopic.CONFIDENCE_MASTERED);
            updateConfidenceButtons(holder, ExamTopic.CONFIDENCE_MASTERED);
            if (listener != null) listener.onConfidenceChanged();
        });
    }

    private void updateConfidenceButtons(TopicViewHolder holder, int state) {
        int unselectedColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.color_666);
        // Reset all to unselected
        holder.binding.btnConfidenceWeak.setBackgroundResource(R.drawable.bg_confidence_pill_unselected);
        holder.binding.btnConfidenceWeak.setTextColor(unselectedColor);

        holder.binding.btnConfidenceShaky.setBackgroundResource(R.drawable.bg_confidence_pill_unselected);
        holder.binding.btnConfidenceShaky.setTextColor(unselectedColor);

        holder.binding.btnConfidenceMastered.setBackgroundResource(R.drawable.bg_confidence_pill_unselected);
        holder.binding.btnConfidenceMastered.setTextColor(unselectedColor);

        if (state == ExamTopic.CONFIDENCE_WEAK) {
            holder.binding.btnConfidenceWeak.setBackgroundResource(R.drawable.bg_confidence_weak_selected);
            holder.binding.btnConfidenceWeak.setTextColor(Color.parseColor("#DC2626"));
        } else if (state == ExamTopic.CONFIDENCE_SHAKY) {
            holder.binding.btnConfidenceShaky.setBackgroundResource(R.drawable.bg_confidence_shaky_selected);
            holder.binding.btnConfidenceShaky.setTextColor(Color.parseColor("#D97706"));
        } else if (state == ExamTopic.CONFIDENCE_MASTERED) {
            holder.binding.btnConfidenceMastered.setBackgroundResource(R.drawable.bg_confidence_mastered_selected);
            holder.binding.btnConfidenceMastered.setTextColor(Color.parseColor("#16A34A"));
        }
    }

    private void animateClick(View view) {
        ScaleAnimation scale = new ScaleAnimation(
                0.92f, 1f, 0.92f, 1f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f
        );
        scale.setDuration(180);
        view.startAnimation(scale);
    }

    @Override
    public int getItemCount() {
        return topics != null ? topics.size() : 0;
    }

    static class TopicViewHolder extends RecyclerView.ViewHolder {
        final ItemAttackTopicBinding binding;

        TopicViewHolder(ItemAttackTopicBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
