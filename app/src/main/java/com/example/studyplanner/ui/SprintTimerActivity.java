package com.example.studyplanner.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studyplanner.data.service.SprintMissionHelper;
import com.example.studyplanner.databinding.ActivitySprintTimerBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class SprintTimerActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "extra_task_id";
    public static final String EXTRA_TASK_NAME = "extra_task_name";
    public static final String EXTRA_TASK_SUBJECT = "extra_task_subject";
    public static final String EXTRA_TASK_DURATION = "extra_task_duration";
    public static final String EXTRA_TASK_KICKSTART = "extra_task_kickstart";
    public static final String EXTRA_TASK_ENERGY = "extra_task_energy";
    public static final String EXTRA_TASK_TYPE = "extra_task_type";

    private ActivitySprintTimerBinding binding;
    private String taskId;
    private String taskName;
    private String taskSubject;
    private int durationMinutes = 25;
    private String kickstart;
    private String energy;
    private String missionType;

    private CountDownTimer countDownTimer;
    private long totalMillis;
    private long millisRemaining;
    private boolean isPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySprintTimerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        extractIntentData();
        setupViews();
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                confirmExit();
            }
        });
        startTimer(millisRemaining);
    }

    private void extractIntentData() {
        taskId = getIntent().getStringExtra(EXTRA_TASK_ID);
        taskName = getIntent().getStringExtra(EXTRA_TASK_NAME);
        if (taskName == null || taskName.trim().isEmpty()) {
            taskName = "Study Sprint";
        }
        taskSubject = getIntent().getStringExtra(EXTRA_TASK_SUBJECT);
        if (taskSubject == null || taskSubject.trim().isEmpty()) {
            taskSubject = "General Study";
        }
        durationMinutes = getIntent().getIntExtra(EXTRA_TASK_DURATION, 25);
        if (durationMinutes <= 0) durationMinutes = 25;

        kickstart = getIntent().getStringExtra(EXTRA_TASK_KICKSTART);
        energy = getIntent().getStringExtra(EXTRA_TASK_ENERGY);
        if (energy == null || energy.isEmpty()) energy = "Balanced Focus";

        missionType = getIntent().getStringExtra(EXTRA_TASK_TYPE);
        if (missionType == null || missionType.isEmpty()) missionType = "Problem Solving";

        totalMillis = durationMinutes * 60 * 1000L;
        millisRemaining = totalMillis;
    }

    private void setupViews() {
        binding.btnCloseTimer.setOnClickListener(v -> confirmExit());

        binding.txtTimerSubjectBadge.setText(taskSubject);
        binding.txtTimerMissionTitle.setText(taskName);
        binding.txtTimerMeta.setText(missionType + " • " + energy);

        if (kickstart != null && !kickstart.trim().isEmpty()) {
            binding.cardKickstartSpotlight.setVisibility(View.VISIBLE);
            binding.txtTimerKickstartText.setText(kickstart);
        } else {
            binding.cardKickstartSpotlight.setVisibility(View.GONE);
        }

        binding.btnPauseResume.setOnClickListener(v -> togglePauseResume());
        binding.btnQuitSprint.setOnClickListener(v -> confirmExit());
        binding.btnCompleteSprint.setOnClickListener(v -> completeSprint());
    }

    private void startTimer(long duration) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisRemaining = millisUntilFinished;
                updateTimerDisplay();
            }

            @Override
            public void onFinish() {
                millisRemaining = 0;
                updateTimerDisplay();
                binding.txtTimerStateStatus.setText("🎉 SPRINT TIME REACHED!");
                completeSprint();
            }
        }.start();

        isPaused = false;
        binding.btnPauseResume.setText("⏸️ Pause");
        binding.txtTimerStateStatus.setText("⚡ SPRINT IN PROGRESS");
    }

    private void updateTimerDisplay() {
        int seconds = (int) (millisRemaining / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;

        binding.txtTimerDigits.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));

        int progressPercent = (int) ((millisRemaining * 100) / totalMillis);
        binding.progressCircularTimer.setProgress(Math.max(0, Math.min(100, progressPercent)));
    }

    private void togglePauseResume() {
        if (isPaused) {
            startTimer(millisRemaining);
        } else {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            isPaused = true;
            binding.btnPauseResume.setText("▶️ Resume");
            binding.txtTimerStateStatus.setText("⏸️ SPRINT PAUSED");
        }
    }

    private void completeSprint() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        int earnedXP = SprintMissionHelper.calculateFocusXP(durationMinutes, energy);

        // Mark as completed in Firestore if taskId exists
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && taskId != null && !taskId.isEmpty()) {
            FirebaseFirestore.getInstance().collection("users")
                    .document(user.getUid())
                    .collection("tasks")
                    .document(taskId)
                    .update("completed", true)
                    .addOnFailureListener(e -> {
                        // ignore or log
                    });
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("🎉 Mission Accomplished!")
                .setMessage("Outstanding effort! You crushed your sprint for \"" + taskName + "\" and earned +" + earnedXP + " Focus XP! 🚀")
                .setPositiveButton("Awesome!", (dialog, which) -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Mission Complete! +" + earnedXP + " XP", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void confirmExit() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Stop Sprint?")
                .setMessage("Your current sprint is active. Do you want to cancel this focus session?")
                .setPositiveButton("Stop Sprint", (dialog, which) -> {
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    finish();
                })
                .setNegativeButton("Keep Going", (dialog, which) -> dialog.dismiss())
                .show();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        binding = null;
    }
}
