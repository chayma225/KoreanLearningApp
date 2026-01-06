package com.example.myapplication.models;

public class Lesson {
    private int id;
    private int villageId;
    private String title;
    private String type;
    private String content;
    private String koreanText;
    private String translation;
    private int points;
    private boolean completed;
    private int imageResId;

    public Lesson(int id, int villageId, String title, String type, String content,
                  String koreanText, String translation, int points, boolean completed,
                  int imageResId) {
        this.id = id;
        this.villageId = villageId;
        this.title = title;
        this.type = type;
        this.content = content;
        this.koreanText = koreanText;
        this.translation = translation;
        this.points = points;
        this.completed = completed;
        this.imageResId = imageResId;
    }

    // Getters & Setters
    public int getId() { return id; }
    public int getVillageId() { return villageId; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public String getContent() { return content; }
    public String getKoreanText() { return koreanText; }
    public String getTranslation() { return translation; }
    public int getPoints() { return points; }
    public boolean isCompleted() { return completed; }
    public int getImageResId() { return imageResId; }

    public void setId(int id) { this.id = id; }
    public void setVillageId(int villageId) { this.villageId = villageId; }
    public void setTitle(String title) { this.title = title; }
    public void setType(String type) { this.type = type; }
    public void setContent(String content) { this.content = content; }
    public void setKoreanText(String koreanText) { this.koreanText = koreanText; }
    public void setTranslation(String translation) { this.translation = translation; }
    public void setPoints(int points) { this.points = points; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
}
