package com.example.studyplanner.data.repository;

import com.example.studyplanner.models.ExamModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class ExamRepository {
    private FirebaseFirestore db;
    private String userId;

    public interface ExamCallback {
        void onExamsLoaded(List<ExamModel> exams);
    }

    public ExamRepository() {
        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
    }

    public ListenerRegistration getExamsForDate(String date, boolean greaterThanOrEqual, ExamCallback callback) {
        if (userId == null) return null;
        
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
                    examList.add(exam);
                }
            }
            callback.onExamsLoaded(examList);
        });
    }
}
