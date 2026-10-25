package com.example.studyplanner.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studyplanner.R;
import com.example.studyplanner.models.HistoryModel;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private final Context context;
    private List<HistoryModel> list;

    public HistoryAdapter(Context context, List<HistoryModel> list) {
        this.context = context;
        this.list = list != null ? list : new ArrayList<>();
    }

    public void setList(List<HistoryModel> list) {
        this.list = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryModel model = list.get(position);
        if (model == null) return;

        holder.txtDate.setText(model.date != null ? model.date : "");

        if (model.subjects != null && !model.subjects.isEmpty()) {
            holder.txtSubjects.setVisibility(View.VISIBLE);
            holder.txtSubjects.setText("Subjects: " + TextUtils.join(", ", model.subjects));
        } else {
            holder.txtSubjects.setVisibility(View.GONE);
        }

        holder.txtCompleted.setText("Completed: " + model.completedCount);
        holder.txtPending.setText("Pending: " + model.pendingCount);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate, txtSubjects, txtCompleted, txtPending;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtSubjects = itemView.findViewById(R.id.txtSubjects);
            txtCompleted = itemView.findViewById(R.id.txtCompleted);
            txtPending = itemView.findViewById(R.id.txtPending);
        }
    }
}