package com.example.studyplanner.models;

public class ExamModel {
    private String id;
    private String name;
    private String subject;
    private String date;
    private String priority;
    private String notes;
    private int readinessScore;
    private java.util.List<String> topics;
    private java.util.List<String> weakTopics;
    private java.util.List<String> traps;

    public ExamModel() {}

    public ExamModel(String name, String subject, String date, String priority, String notes) {
        this.name = name;
        this.subject = subject;
        this.date = date;
        this.priority = priority;
        this.notes = notes;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getPriority() { return priority; }

    public void setPriority(String priority) { this.priority = priority; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public int getReadinessScore() { return readinessScore; }
    public void setReadinessScore(int readinessScore) { this.readinessScore = readinessScore; }

    public java.util.List<String> getTopics() { return topics; }
    public void setTopics(java.util.List<String> topics) { this.topics = topics; }

    public java.util.List<String> getWeakTopics() { return weakTopics; }
    public void setWeakTopics(java.util.List<String> weakTopics) { this.weakTopics = weakTopics; }

    public java.util.List<String> getTraps() { return traps; }
    public void setTraps(java.util.List<String> traps) { this.traps = traps; }
}