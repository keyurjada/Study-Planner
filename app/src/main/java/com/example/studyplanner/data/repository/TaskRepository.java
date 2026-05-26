package com.example.studyplanner.data.repository;

import com.example.studyplanner.models.TaskModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class TaskRepository {
    private FirebaseFirestore db;
    private String userId;

    public interface TaskCallback {
        void onTasksLoaded(List<TaskModel> tasks);
    }
    
    public interface ProgressCallback {
        void onProgressLoaded(int completed, int total);
    }

    public TaskRepository() {
        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
    }

    public ListenerRegistration getTasksForDate(String date, boolean pendingOnly, TaskCallback callback) {
        if (userId == null) return null;
        
        com.google.firebase.firestore.Query query = db.collection("users")
                .document(userId)
                .collection("tasks")
                .whereEqualTo("date", date);
                
        if (pendingOnly) {
            query = query.whereEqualTo("completed", false);
        }

        return query.addSnapshotListener((value, error) -> {
            if (error != null || value == null) return;
            List<TaskModel> taskList = new ArrayList<>();
            for (DocumentSnapshot doc : value) {
                TaskModel task = doc.toObject(TaskModel.class);
                if (task != null) {
                    task.setId(doc.getId());
                    taskList.add(task);
                }
            }
            callback.onTasksLoaded(taskList);
        });
    }
    
    public ListenerRegistration getProgressForDate(String date, ProgressCallback callback) {
        if (userId == null) return null;
        
        return db.collection("users")
                .document(userId)
                .collection("tasks")
                .whereEqualTo("date", date)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    int total = value.size();
                    int completed = 0;
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Boolean done = doc.getBoolean("completed");
                        if (done != null && done) {
                            completed++;
                        }
                    }
                    callback.onProgressLoaded(completed, total);
                });
    }
    
    // For saving, updating, etc. (we can add as needed)
}
