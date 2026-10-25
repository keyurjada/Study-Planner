package com.example.studyplanner.ui;

import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studyplanner.R;
import com.example.studyplanner.adapters.AttackTopicAdapter;
import com.example.studyplanner.data.service.ExamTopicSynthesizer;
import com.example.studyplanner.databinding.ActivityAddExamBinding;
import com.example.studyplanner.models.ExamTopic;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddExamActivity extends AppCompatActivity {

    private ActivityAddExamBinding binding;
    private FirebaseFirestore db;
    private final List<ExamTopic> currentTopics = new ArrayList<>();
    private AttackTopicAdapter topicAdapter;
    private String currentSubject = "Operating Systems";
    private String selectedDateStr;
    private int currentReadinessScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddExamBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        setupTopBar();
        setupRecyclerView();
        setupQuickSubjectChips();
        setupDateSelector();
        setupActionListeners();

        // Load default initial subject: Operating Systems
        loadSubjectPlan("Operating Systems", ExamTopicSynthesizer.synthesize("Operating Systems"));
    }

    private void setupTopBar() {
        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        topicAdapter = new AttackTopicAdapter(currentTopics, this::recalculateReadiness);
        binding.recyclerAttackTopics.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerAttackTopics.setAdapter(topicAdapter);
    }

    private void setupQuickSubjectChips() {
        List<ExamTopicSynthesizer.SubjectTemplate> templates = ExamTopicSynthesizer.getQuickTemplates();
        binding.layoutQuickChips.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);
        for (ExamTopicSynthesizer.SubjectTemplate template : templates) {
            View chipView = inflater.inflate(R.layout.item_task_calendar, binding.layoutQuickChips, false);
            TextView chipText;
            if (chipView instanceof TextView) {
                chipText = (TextView) chipView;
            } else {
                chipText = chipView.findViewById(android.R.id.text1);
                if (chipText == null) {
                    chipText = new TextView(this);
                }
            }

            chipText.setText(template.emoji + " " + template.displayName);
            chipText.setTextSize(12);
            chipText.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.color_333));
            chipText.setBackgroundResource(R.drawable.bg_chip_subject);
            chipText.setPadding(28, 14, 28, 14);
            chipText.setClickable(true);
            chipText.setFocusable(true);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(0, 0, 16, 0);
            chipText.setLayoutParams(lp);

            chipText.setOnClickListener(v -> {
                binding.editSubjectInput.setText(template.displayName);
                loadSubjectPlan(template.displayName, template.topics);
            });

            binding.layoutQuickChips.addView(chipText);
        }
    }

    private void setupDateSelector() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();

        // Default: 3 days from now
        cal.add(Calendar.DAY_OF_YEAR, 3);
        selectedDateStr = sdf.format(cal.getTime());
        binding.examDate.setText(selectedDateStr);

        binding.chipTomorrow.setOnClickListener(v -> {
            updateDateChipStyles(binding.chipTomorrow);
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_YEAR, 1);
            selectedDateStr = sdf.format(c.getTime());
            binding.examDate.setText(selectedDateStr);
        });

        binding.chip3Days.setOnClickListener(v -> {
            updateDateChipStyles(binding.chip3Days);
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_YEAR, 3);
            selectedDateStr = sdf.format(c.getTime());
            binding.examDate.setText(selectedDateStr);
        });

        binding.chipNextWeek.setOnClickListener(v -> {
            updateDateChipStyles(binding.chipNextWeek);
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_YEAR, 7);
            selectedDateStr = sdf.format(c.getTime());
            binding.examDate.setText(selectedDateStr);
        });

        binding.examDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(this,
                    (view, year, month, day) -> {
                        selectedDateStr = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
                        binding.examDate.setText(selectedDateStr);
                        resetDateChipStyles();
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH));

            dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            dialog.show();
        });
    }

    private void updateDateChipStyles(TextView selectedChip) {
        resetDateChipStyles();
        selectedChip.setBackgroundResource(R.drawable.bg_chip_subject_selected);
        selectedChip.setTextColor(Color.WHITE);
    }

    private void resetDateChipStyles() {
        int unselectedColor = androidx.core.content.ContextCompat.getColor(this, R.color.color_666);
        binding.chipTomorrow.setBackgroundResource(R.drawable.bg_chip_subject);
        binding.chipTomorrow.setTextColor(unselectedColor);
        binding.chip3Days.setBackgroundResource(R.drawable.bg_chip_subject);
        binding.chip3Days.setTextColor(unselectedColor);
        binding.chipNextWeek.setBackgroundResource(R.drawable.bg_chip_subject);
        binding.chipNextWeek.setTextColor(unselectedColor);
    }

    private void setupActionListeners() {
        binding.btnGeneratePlan.setOnClickListener(v -> generateFromInput());

        binding.editSubjectInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                generateFromInput();
                return true;
            }
            return false;
        });

        binding.btnAddExam.setOnClickListener(v -> validateAndSaveAttackPlan());
    }

    private void generateFromInput() {
        String input = binding.editSubjectInput.getText().toString().trim();
        if (input.isEmpty()) {
            binding.editSubjectInput.setError("Enter a course or subject");
            binding.editSubjectInput.requestFocus();
            return;
        }

        List<ExamTopic> synthesized = ExamTopicSynthesizer.synthesize(input);
        loadSubjectPlan(input, synthesized);
        Toast.makeText(this, "⚡ Attack Plan synthesized for " + input, Toast.LENGTH_SHORT).show();
    }

    private void loadSubjectPlan(String subjectName, List<ExamTopic> topics) {
        this.currentSubject = subjectName;
        binding.txtSubjectDisplay.setText(subjectName + " Exam");
        binding.editSubjectInput.setText(subjectName);

        currentTopics.clear();
        currentTopics.addAll(topics);
        topicAdapter.notifyDataSetChanged();

        recalculateReadiness();
    }

    private void recalculateReadiness() {
        if (currentTopics.isEmpty()) {
            currentReadinessScore = 0;
            updateReadinessUI(0, 0, 0, 0);
            return;
        }

        int weakCount = 0;
        int shakyCount = 0;
        int masteredCount = 0;
        int totalPoints = 0;

        for (ExamTopic topic : currentTopics) {
            int state = topic.getConfidenceState();
            if (state == ExamTopic.CONFIDENCE_WEAK) {
                weakCount++;
                totalPoints += 0;
            } else if (state == ExamTopic.CONFIDENCE_SHAKY) {
                shakyCount++;
                totalPoints += 50;
            } else if (state == ExamTopic.CONFIDENCE_MASTERED) {
                masteredCount++;
                totalPoints += 100;
            }
        }

        int maxPossible = currentTopics.size() * 100;
        currentReadinessScore = maxPossible > 0 ? (totalPoints * 100) / maxPossible : 0;

        updateReadinessUI(currentReadinessScore, weakCount, shakyCount, masteredCount);
    }

    private void updateReadinessUI(int score, int weak, int shaky, int mastered) {
        ObjectAnimator progressAnim = ObjectAnimator.ofInt(binding.progressReadiness, "progress", score);
        progressAnim.setDuration(300);
        progressAnim.setInterpolator(new DecelerateInterpolator());
        progressAnim.start();

        binding.txtReadinessScore.setText(score + "% Ready");

        if (score < 40) {
            binding.txtReadinessScore.setBackgroundResource(R.drawable.bg_badge_high_yield);
            binding.txtReadinessScore.setTextColor(Color.parseColor("#DC2626"));
            binding.txtReadinessStatus.setText("🚨 High Risk Zone: Prioritize the weak topics & traps!");
        } else if (score < 75) {
            binding.txtReadinessScore.setBackgroundResource(R.drawable.bg_confidence_shaky_selected);
            binding.txtReadinessScore.setTextColor(Color.parseColor("#D97706"));
            binding.txtReadinessStatus.setText("⚡ Moderate Readiness: Review shaky topics once more.");
        } else {
            binding.txtReadinessScore.setBackgroundResource(R.drawable.bg_confidence_mastered_selected);
            binding.txtReadinessScore.setTextColor(Color.parseColor("#16A34A"));
            binding.txtReadinessStatus.setText("🎯 High Mastery: You are well prepared for curveballs!");
        }

        binding.txtWeakCount.setText("🚨 " + weak + " Weak");
        binding.txtShakyCount.setText("🟡 " + shaky + " Shaky");
        binding.txtMasteredCount.setText("🟢 " + mastered + " Mastered");

        binding.btnAddExam.setText("🚀 Lock In Attack Plan (" + score + "% Ready)");
    }

    private void validateAndSaveAttackPlan() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (currentSubject == null || currentSubject.trim().isEmpty()) {
            Toast.makeText(this, "Please choose or enter a subject", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedDateStr == null || selectedDateStr.trim().isEmpty()) {
            Toast.makeText(this, "Please choose an exam date", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedRadioId = binding.priorityGroup.getCheckedRadioButtonId();
        RadioButton selectedRadio = findViewById(selectedRadioId);
        String priority = (selectedRadio != null && selectedRadio.getText() != null)
                ? selectedRadio.getText().toString()
                : "Important";

        List<String> topicTitles = new ArrayList<>();
        List<String> weakTitles = new ArrayList<>();
        List<String> traps = new ArrayList<>();

        for (ExamTopic topic : currentTopics) {
            topicTitles.add(topic.getTitle());
            traps.add(topic.getTrap());
            if (topic.getConfidenceState() == ExamTopic.CONFIDENCE_WEAK) {
                weakTitles.add(topic.getTitle());
            }
        }

        String examName = currentSubject + " Exam Attack Plan";

        new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MyDialog)
                .setTitle("Confirm Exam Attack Plan")
                .setMessage("⚡ " + examName + "\n" +
                        "🎯 Readiness: " + currentReadinessScore + "%\n" +
                        "📅 Target Date: " + selectedDateStr + "\n" +
                        "🔥 High-Yield Topics: " + currentTopics.size() + "\n" +
                        "⚠️ Traps to Watch: " + traps.size())
                .setPositiveButton("Lock In", (dialog, which) -> {
                    binding.btnAddExam.setEnabled(false);

                    Map<String, Object> exam = new HashMap<>();
                    exam.put("name", examName);
                    exam.put("subject", currentSubject);
                    exam.put("date", selectedDateStr);
                    exam.put("priority", priority);
                    exam.put("readinessScore", currentReadinessScore);
                    exam.put("topics", topicTitles);
                    exam.put("weakTopics", weakTitles);
                    exam.put("traps", traps);
                    exam.put("notes", "Auto-generated Reverse Syllabus Attack Plan");

                    db.collection("users")
                            .document(currentUser.getUid())
                            .collection("exams")
                            .add(exam)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "🎉 Attack Plan Locked In! (" + currentReadinessScore + "% Ready)", Toast.LENGTH_LONG).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                binding.btnAddExam.setEnabled(true);
                                String errorMsg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "Failed to save plan";
                                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                            });
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
}