package com.example.myapplication.models;

public class Village {
    private int id;
    private String name;
    private String koreanName;
    private String description;
    private int imageResId;
    private int totalLessons;
    private int completedLessons;
    private boolean unlocked;

    public Village(int id, String name, String koreanName, String description,
                   int imageResId, int totalLessons, int completedLessons, boolean unlocked) {
        this.id = id;
        this.name = name;
        this.koreanName = koreanName;
        this.description = description;
        this.imageResId = imageResId;
        this.totalLessons = totalLessons;
        this.completedLessons = completedLessons;
        this.unlocked = unlocked;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public void setKoreanName(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public int getTotalLessons() {
        return totalLessons;
    }

    public void setTotalLessons(int totalLessons) {
        this.totalLessons = totalLessons;
    }

    public int getCompletedLessons() {
        return completedLessons;
    }

    public void setCompletedLessons(int completedLessons) {
        this.completedLessons = completedLessons;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public float getProgressPercentage() {
        if (totalLessons == 0) return 0;
        return (float) completedLessons / totalLessons * 100;
    }

}
