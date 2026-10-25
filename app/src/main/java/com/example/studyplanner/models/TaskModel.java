package com.example.studyplanner.models;

public class TaskModel {
    private String id;
    private String name;
    private String subject;
    private String date;
    private boolean completed;
    private int durationMinutes = 25;
    private String energyLevel = "Balanced Focus";
    private String kickstartStep = "";
    private String missionType = "Problem Solving";

    public TaskModel() {}

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSubject() {
        return subject;
    }

    public String getDate() {
        return date;
    }

    public boolean isCompleted() {   // 🔥 IMPORTANT NAME
        return completed;
    }

    public int getDurationMinutes() {
        return durationMinutes > 0 ? durationMinutes : 25;
    }

    public String getEnergyLevel() {
        return energyLevel != null && !energyLevel.isEmpty() ? energyLevel : "Balanced Focus";
    }

    public String getKickstartStep() {
        return kickstartStep != null ? kickstartStep : "";
    }

    public String getMissionType() {
        return missionType != null && !missionType.isEmpty() ? missionType : "Problem Solving";
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCompleted(boolean completed) { // 🔥 IMPORTANT NAME
        this.completed = completed;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public void setEnergyLevel(String energyLevel) {
        this.energyLevel = energyLevel;
    }

    public void setKickstartStep(String kickstartStep) {
        this.kickstartStep = kickstartStep;
    }

    public void setMissionType(String missionType) {
        this.missionType = missionType;
    }
}