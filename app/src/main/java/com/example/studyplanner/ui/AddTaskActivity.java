package com.example.studyplanner.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.studyplanner.R;
import com.example.studyplanner.data.service.SprintMissionHelper;
import com.example.studyplanner.databinding.ActivityAddTaskBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddTaskActivity extends AppCompatActivity {

    private ActivityAddTaskBinding binding;
    private FirebaseFirestore db;

    // Sprint State
    private String selectedSubject = "Operating Systems";
    private String selectedMissionType = "Problem Solving";
    private String selectedMissionEmoji = "🎯";
    private int selectedDuration = 25;
    private String selectedEnergy = "Balanced Focus";
    private String selectedDate;
    private String selectedKickstart = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        initDefaultDate();
        setupTopBar();
        setupInputWatchers();
        setupQuickSubjects();
        setupMissionTypeChips();
        setupDurationPills();
        setupKickstartSuggestions();
        setupEnergyCards();
        setupDateSelector();
        setupActionButtons();

        // Initial default kickstart from Problem Solving
        selectedKickstart = "Read question #1 and copy given values onto scratchpad";
        binding.editKickstart.setText(selectedKickstart);

        updateLiveDossier();
    }

    private void initDefaultDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = sdf.format(new Date());
        binding.txtDateDisplay.setText("Today (" + selectedDate + ")");
        binding.editSubject.setText(selectedSubject);
    }

    private void setupTopBar() {
        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void setupInputWatchers() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLiveDossier();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        binding.editMissionName.addTextChangedListener(watcher);
        binding.editSubject.addTextChangedListener(watcher);
        binding.editKickstart.addTextChangedListener(watcher);
    }

    private void setupQuickSubjects() {
        List<String> subjects = SprintMissionHelper.getQuickSubjects();
        binding.layoutQuickSubjects.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (String subject : subjects) {
            View chipView = inflater.inflate(R.layout.item_task_calendar, binding.layoutQuickSubjects, false);
            TextView chipText = chipView.findViewById(R.id.taskName);
            if (chipText == null && chipView instanceof TextView) {
                chipText = (TextView) chipView;
            }

            if (chipText != null) {
                chipText.setText(subject);
                chipText.setTextSize(12);
                chipText.setTextColor(ContextCompat.getColor(this, R.color.color_333));
                chipText.setBackgroundResource(R.drawable.bg_chip_subject);
                chipText.setPadding(24, 12, 24, 12);
                chipText.setClickable(true);
                chipText.setFocusable(true);

                chipText.setOnClickListener(v -> {
                    selectedSubject = subject;
                    binding.editSubject.setText(subject);
                    updateLiveDossier();
                });
            }
            binding.layoutQuickSubjects.addView(chipView);
        }
    }

    private void setupMissionTypeChips() {
        List<SprintMissionHelper.MissionTypeConfig> types = SprintMissionHelper.getMissionTypes();
        binding.layoutMissionTypes.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (SprintMissionHelper.MissionTypeConfig type : types) {
            View chipView = inflater.inflate(R.layout.item_task_calendar, binding.layoutMissionTypes, false);
            TextView chipText = chipView.findViewById(R.id.taskName);
            if (chipText == null && chipView instanceof TextView) {
                chipText = (TextView) chipView;
            }

            if (chipText != null) {
                chipText.setText(type.emoji + " " + type.displayName);
                chipText.setTextSize(12);
                chipText.setTextColor(ContextCompat.getColor(this, R.color.color_333));
                chipText.setBackgroundResource(R.drawable.bg_chip_subject);
                chipText.setPadding(24, 12, 24, 12);
                chipText.setClickable(true);
                chipText.setFocusable(true);

                chipText.setOnClickListener(v -> {
                    selectedMissionType = type.displayName;
                    selectedMissionEmoji = type.emoji;
                    selectDuration(type.defaultMinutes);
                    setEnergyLevel(type.defaultEnergy);

                    // Pick first kickstart suggestion
                    if (!type.kickstarts.isEmpty()) {
                        selectedKickstart = type.kickstarts.get(0);
                        binding.editKickstart.setText(selectedKickstart);
                    }
                    setupKickstartSuggestions();
                    updateLiveDossier();
                });
            }
            binding.layoutMissionTypes.addView(chipView);
        }
    }

    private void setupDurationPills() {
        binding.pillDuration15.setOnClickListener(v -> selectDuration(15));
        binding.pillDuration25.setOnClickListener(v -> selectDuration(25));
        binding.pillDuration45.setOnClickListener(v -> selectDuration(45));
        binding.pillDuration60.setOnClickListener(v -> selectDuration(60));
    }

    private void selectDuration(int minutes) {
        selectedDuration = minutes;
        binding.txtDurationSelected.setText("⚡ " + minutes + " min");

        resetPill(binding.pillDuration15);
        resetPill(binding.pillDuration25);
        resetPill(binding.pillDuration45);
        resetPill(binding.pillDuration60);

        if (minutes == 15) {
            highlightPill(binding.pillDuration15);
        } else if (minutes == 25) {
            highlightPill(binding.pillDuration25);
        } else if (minutes == 45) {
            highlightPill(binding.pillDuration45);
        } else if (minutes == 60) {
            highlightPill(binding.pillDuration60);
        }

        updateLiveDossier();
    }

    private void resetPill(TextView pill) {
        pill.setBackgroundResource(R.drawable.bg_pill_sprint_unselected);
        pill.setTextColor(ContextCompat.getColor(this, R.color.color_333));
    }

    private void highlightPill(TextView pill) {
        pill.setBackgroundResource(R.drawable.bg_pill_sprint_selected);
        pill.setTextColor(Color.WHITE);
    }

    private void setupKickstartSuggestions() {
        binding.layoutKickstartSuggestions.removeAllViews();
        List<SprintMissionHelper.MissionTypeConfig> types = SprintMissionHelper.getMissionTypes();

        List<String> suggestions = null;
        for (SprintMissionHelper.MissionTypeConfig config : types) {
            if (config.displayName.equalsIgnoreCase(selectedMissionType)) {
                suggestions = config.kickstarts;
                break;
            }
        }
        if (suggestions == null || suggestions.isEmpty()) {
            suggestions = types.get(0).kickstarts;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        for (String prompt : suggestions) {
            View chipView = inflater.inflate(R.layout.item_task_calendar, binding.layoutKickstartSuggestions, false);
            TextView chipText = chipView.findViewById(R.id.taskName);
            if (chipText == null && chipView instanceof TextView) {
                chipText = (TextView) chipView;
            }

            if (chipText != null) {
                chipText.setText(prompt);
                chipText.setTextSize(11);
                chipText.setTextColor(ContextCompat.getColor(this, R.color.color_333));
                chipText.setBackgroundResource(R.drawable.bg_chip_subject);
                chipText.setPadding(20, 10, 20, 10);
                chipText.setClickable(true);
                chipText.setFocusable(true);

                chipText.setOnClickListener(v -> {
                    selectedKickstart = prompt;
                    binding.editKickstart.setText(prompt);
                    updateLiveDossier();
                });
            }
            binding.layoutKickstartSuggestions.addView(chipView);
        }
    }

    private void setupEnergyCards() {
        binding.cardEnergyDeep.setOnClickListener(v -> setEnergyLevel("Deep Grind"));
        binding.cardEnergyBalanced.setOnClickListener(v -> setEnergyLevel("Balanced Focus"));
        binding.cardEnergyLow.setOnClickListener(v -> setEnergyLevel("Quick Win"));
    }

    private void setEnergyLevel(String level) {
        selectedEnergy = level;

        binding.cardEnergyDeep.setBackgroundResource(R.drawable.bg_energy_unselected);
        binding.cardEnergyBalanced.setBackgroundResource(R.drawable.bg_energy_unselected);
        binding.cardEnergyLow.setBackgroundResource(R.drawable.bg_energy_unselected);

        TextView txtDeep = (TextView) binding.cardEnergyDeep.getChildAt(1);
        TextView txtBalanced = (TextView) binding.cardEnergyBalanced.getChildAt(1);
        TextView txtLow = (TextView) binding.cardEnergyLow.getChildAt(1);

        txtDeep.setTextColor(ContextCompat.getColor(this, R.color.color_333));
        txtBalanced.setTextColor(ContextCompat.getColor(this, R.color.color_333));
        txtLow.setTextColor(ContextCompat.getColor(this, R.color.color_333));

        if ("Deep Grind".equalsIgnoreCase(level)) {
            binding.cardEnergyDeep.setBackgroundResource(R.drawable.bg_energy_selected);
            txtDeep.setTextColor(Color.WHITE);
        } else if ("Balanced Focus".equalsIgnoreCase(level)) {
            binding.cardEnergyBalanced.setBackgroundResource(R.drawable.bg_energy_selected);
            txtBalanced.setTextColor(Color.WHITE);
        } else {
            binding.cardEnergyLow.setBackgroundResource(R.drawable.bg_energy_selected);
            txtLow.setTextColor(Color.WHITE);
        }

        updateLiveDossier();
    }

    private void setupDateSelector() {
        binding.cardDateSelector.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(this,
                    (view, year, month, day) -> {
                        selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
                        binding.txtDateDisplay.setText(selectedDate);
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });
    }

    private void updateLiveDossier() {
        String name = binding.editMissionName.getText().toString().trim();
        if (name.isEmpty()) {
            name = selectedMissionEmoji + " " + selectedSubject + " Sprint";
        }
        binding.txtDossierTitle.setText(name);

        String subject = binding.editSubject.getText().toString().trim();
        if (subject.isEmpty()) subject = selectedSubject;

        binding.txtDossierBadge.setText("⚡ " + selectedDuration + " min • " + subject + " • " + selectedEnergy);

        String kick = binding.editKickstart.getText().toString().trim();
        if (!kick.isEmpty()) {
            binding.txtDossierKickstart.setText("🎯 First 2-Min: " + kick);
            binding.txtDossierKickstart.setVisibility(View.VISIBLE);
        } else {
            binding.txtDossierKickstart.setVisibility(View.GONE);
        }

        int focusXP = SprintMissionHelper.calculateFocusXP(selectedDuration, selectedEnergy);
        binding.txtDossierXP.setText("⭐ +" + focusXP + " XP");
    }

    private void setupActionButtons() {
        binding.btnLaunchSprint.setOnClickListener(v -> saveAndProceed(true));
        binding.btnScheduleMission.setOnClickListener(v -> saveAndProceed(false));
    }

    private void saveAndProceed(boolean launchNow) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String name = binding.editMissionName.getText().toString().trim();
        String subject = binding.editSubject.getText().toString().trim();
        String kickstart = binding.editKickstart.getText().toString().trim();

        if (name.isEmpty()) {
            name = selectedMissionEmoji + " " + (subject.isEmpty() ? selectedSubject : subject) + " Sprint";
        }
        if (subject.isEmpty()) {
            subject = selectedSubject;
        }
        if (kickstart.isEmpty()) {
            kickstart = selectedKickstart;
        }

        binding.btnLaunchSprint.setEnabled(false);
        binding.btnScheduleMission.setEnabled(false);

        Map<String, Object> task = new HashMap<>();
        task.put("name", name);
        task.put("subject", subject);
        task.put("date", selectedDate);
        task.put("completed", false);
        task.put("durationMinutes", selectedDuration);
        task.put("energyLevel", selectedEnergy);
        task.put("kickstartStep", kickstart);
        task.put("missionType", selectedMissionType);

        final String finalName = name;
        final String finalSubject = subject;
        final String finalKickstart = kickstart;

        db.collection("users")
                .document(currentUser.getUid())
                .collection("tasks")
                .add(task)
                .addOnSuccessListener(docRef -> {
                    if (launchNow) {
                        Intent timerIntent = new Intent(this, SprintTimerActivity.class);
                        timerIntent.putExtra(SprintTimerActivity.EXTRA_TASK_ID, docRef.getId());
                        timerIntent.putExtra(SprintTimerActivity.EXTRA_TASK_NAME, finalName);
                        timerIntent.putExtra(SprintTimerActivity.EXTRA_TASK_SUBJECT, finalSubject);
                        timerIntent.putExtra(SprintTimerActivity.EXTRA_TASK_DURATION, selectedDuration);
                        timerIntent.putExtra(SprintTimerActivity.EXTRA_TASK_KICKSTART, finalKickstart);
                        timerIntent.putExtra(SprintTimerActivity.EXTRA_TASK_ENERGY, selectedEnergy);
                        timerIntent.putExtra(SprintTimerActivity.EXTRA_TASK_TYPE, selectedMissionType);
                        startActivity(timerIntent);
                    } else {
                        Toast.makeText(this, "Mission Scheduled! 🚀", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                })
                .addOnFailureListener(e -> {
                    binding.btnLaunchSprint.setEnabled(true);
                    binding.btnScheduleMission.setEnabled(true);
                    String errorMsg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "Failed to save mission";
                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                });
    }
}