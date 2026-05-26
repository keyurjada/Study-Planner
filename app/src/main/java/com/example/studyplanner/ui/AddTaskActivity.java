package com.example.studyplanner.ui;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studyplanner.databinding.ActivityAddTaskBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddTaskActivity extends AppCompatActivity {
    private ActivityAddTaskBinding binding;
    EditText taskName, taskDate, taskSubject;
    Button btnAdd;
    FirebaseFirestore db;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageView btnBack = binding.btnBack;
        taskName = binding.taskName;
        taskDate = binding.taskDate;
        taskSubject = binding.taskSubject;
        btnAdd = binding.btnAddTask;
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        taskDate.setText(currentDate);
        btnBack.setOnClickListener(v -> {
            finish();
        });
        btnAdd.setOnClickListener(v -> {
            Map<String, Object> task = new HashMap<>();
            task.put("name", taskName.getText().toString());
            task.put("subject", taskSubject.getText().toString());
            task.put("date", currentDate);
            task.put("completed", false);
            db.collection("users")
                    .document(userId)
                    .collection("tasks")
                    .add(task)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Task Added", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        });
    }
}