package com.example.myapplication.models;

/**
 * Modèle pour les badges du système de progression
 */
public class Badge {
    private int id;
    private String name;
    private String description;
    private String icon; // Resource ID ou URL
    private String type; // VILLAGE_COMPLETE, POINTS_MILESTONE, STREAK, etc.
    private int requiredValue; // Points ou autres critères
    private boolean unlocked;
    private long unlockedDate;

    public Badge(int id, String name, String description, String icon, String type,
                 int requiredValue, boolean unlocked, long unlockedDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.type = type;
        this.requiredValue = requiredValue;
        this.unlocked = unlocked;
        this.unlockedDate = unlockedDate;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getRequiredValue() { return requiredValue; }
    public void setRequiredValue(int requiredValue) { this.requiredValue = requiredValue; }

    public boolean isUnlocked() { return unlocked; }
    public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }

    public long getUnlockedDate() { return unlockedDate; }
    public void setUnlockedDate(long unlockedDate) { this.unlockedDate = unlockedDate; }
}
