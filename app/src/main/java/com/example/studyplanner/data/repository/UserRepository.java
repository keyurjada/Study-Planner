package com.example.studyplanner.data.repository;

import com.example.studyplanner.models.HistoryModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepository {
    private final FirebaseFirestore db;

    public interface UserCallback {
        void onUserLoaded(String name, String email);
    }
    
    public interface HistoryCallback {
        void onHistoryLoaded(List<HistoryModel> history);
    }

    public UserRepository() {
        db = FirebaseFirestore.getInstance();
    }

    private String getUserId() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return null;
    }

    public ListenerRegistration getUserData(UserCallback callback) {
        String userId = getUserId();
        if (userId == null) {
            if (callback != null) callback.onUserLoaded(null, null);
            return null;
        }
        return db.collection("users").document(userId)
                .addSnapshotListener((document, error) -> {
                    if (error != null || document == null) return;
                    if (document.exists()) {
                        String name = document.getString("name");
                        String email = document.getString("email");
                        if (callback != null) {
                            callback.onUserLoaded(name, email);
                        }
                    }
                });
    }
    
    public ListenerRegistration getHistory(HistoryCallback callback) {
        String userId = getUserId();
        if (userId == null) {
            if (callback != null) callback.onHistoryLoaded(new ArrayList<>());
            return null;
        }
        return db.collection("users")
                .document(userId)
                .collection("tasks")
                .addSnapshotListener((query, error) -> {
                    if (error != null || query == null) return;
                    Map<String, HistoryModel> map = new HashMap<>();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        String date = doc.getString("date");
                        String subject = doc.getString("subject");
                        Boolean completed = doc.getBoolean("completed");
                        if (date == null || completed == null) continue;
                        if (!map.containsKey(date)) {
                            map.put(date, new HistoryModel(date));
                        }
                        HistoryModel m = map.get(date);
                        if (m != null) {
                            if (subject != null && !subject.trim().isEmpty()) {
                                m.subjects.add(subject.trim());
                            }
                            if (completed) {
                                m.completedCount++;
                            } else {
                                m.pendingCount++;
                            }
                        }
                    }
                    List<HistoryModel> result = new ArrayList<>(map.values());
                    Collections.sort(result, (a, b) -> {
                        if (a.date == null) return 1;
                        if (b.date == null) return -1;
                        return b.date.compareTo(a.date);
                    });
                    if (result.size() > 4) {
                        result = new ArrayList<>(result.subList(0, 4));
                    }
                    if (callback != null) {
                        callback.onHistoryLoaded(result);
                    }
                });
    }
}
