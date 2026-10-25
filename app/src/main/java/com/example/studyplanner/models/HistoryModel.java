package com.example.studyplanner.models;

import java.util.HashSet;
import java.util.Set;

public class HistoryModel {
    public String date;
    public Set<String> subjects;
    public int completedCount;
    public int pendingCount;

    public HistoryModel() {
        this.subjects = new HashSet<>();
    }

    public HistoryModel(String date) {
        this.date = date;
        this.subjects = new HashSet<>();
    }
}