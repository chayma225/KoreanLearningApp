package com.example.myapplication.models;

public class User {
    private int id;
    private String username;
    private int totalPoints;
    private int totalBadges;
    private int villagesUnlocked;
    private long lastLoginTime;

    public User(int id, String username, int totalPoints, int totalBadges,
                int villagesUnlocked, long lastLoginTime) {
        this.id = id;
        this.username = username;
        this.totalPoints = totalPoints;
        this.totalBadges = totalBadges;
        this.villagesUnlocked = villagesUnlocked;
        this.lastLoginTime = lastLoginTime;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public int getTotalBadges() {
        return totalBadges;
    }

    public void setTotalBadges(int totalBadges) {
        this.totalBadges = totalBadges;
    }

    public int getVillagesUnlocked() {
        return villagesUnlocked;
    }

    public void setVillagesUnlocked(int villagesUnlocked) {
        this.villagesUnlocked = villagesUnlocked;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public void addPoints(int points) {
        this.totalPoints += points;
    }

    public void addBadge() {
        this.totalBadges++;
    }
}
