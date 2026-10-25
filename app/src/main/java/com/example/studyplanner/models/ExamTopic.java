package com.example.studyplanner.models;

public class ExamTopic {
    public static final int CONFIDENCE_UNRATED = 0;
    public static final int CONFIDENCE_WEAK = 1;     // Need Review 🚨
    public static final int CONFIDENCE_SHAKY = 2;    // So-so 🟡
    public static final int CONFIDENCE_MASTERED = 3; // Mastered 🟢

    private String title;
    private String trap;
    private String weight; // "High-Yield 80/20", "Core Exam Focus", etc.
    private int confidenceState;

    public ExamTopic() {
        this.confidenceState = CONFIDENCE_UNRATED;
    }

    public ExamTopic(String title, String trap, String weight) {
        this.title = title;
        this.trap = trap;
        this.weight = weight;
        this.confidenceState = CONFIDENCE_UNRATED;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTrap() {
        return trap;
    }

    public void setTrap(String trap) {
        this.trap = trap;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public int getConfidenceState() {
        return confidenceState;
    }

    public void setConfidenceState(int confidenceState) {
        this.confidenceState = confidenceState;
    }
}
