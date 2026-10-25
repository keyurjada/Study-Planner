package com.example.studyplanner.data.repository;

import com.example.studyplanner.models.ExamModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class ExamRepository {
    private final FirebaseFirestore db;

    public interface ExamCallback {
        void onExamsLoaded(List<ExamModel> exams);
    }

    public ExamRepository() {
        db = FirebaseFirestore.getInstance();
    }

    private String getUserId() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return null;
    }

    public ListenerRegistration getExamsForDate(String date, boolean greaterThanOrEqual, ExamCallback callback) {
        String userId = getUserId();
        if (userId == null) {
            if (callback != null) callback.onExamsLoaded(new ArrayList<>());
            return null;
        }
        
        com.google.firebase.firestore.Query query = db.collection("users")
                .document(userId)
                .collection("exams");
                
        if (greaterThanOrEqual) {
            query = query.whereGreaterThanOrEqualTo("date", date);
        } else {
            query = query.whereEqualTo("date", date);
        }

        return query.addSnapshotListener((value, error) -> {
            if (error != null || value == null) return;
            List<ExamModel> examList = new ArrayList<>();
            for (DocumentSnapshot doc : value) {
                ExamModel exam = doc.toObject(ExamModel.class);
                if (exam != null) {
                    exam.setId(doc.getId());
                    examList.add(exam);
                }
            }
            if (callback != null) {
                callback.onExamsLoaded(examList);
            }
        });
    }
}
