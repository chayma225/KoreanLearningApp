package com.example.myapplication.models;

import java.util.List;

/**
 * Modèle pour les articles culturels
 * Affichés après la complétion des 3 leçons d'un village
 */
public class CulturalArticle {
    private int id;
    private int villageId;
    private String title;
    private String titleKorean;
    private String content;
    private List<String> imageUrls;
    private String category; // TRADITION, FOOD, FESTIVAL, LANGUAGE, etc.
    private boolean hasQuiz;
    private int quizPoints;

    public CulturalArticle(int id, int villageId, String title, String titleKorean,
                          String content, List<String> imageUrls, String category,
                          boolean hasQuiz, int quizPoints) {
        this.id = id;
        this.villageId = villageId;
        this.title = title;
        this.titleKorean = titleKorean;
        this.content = content;
        this.imageUrls = imageUrls;
        this.category = category;
        this.hasQuiz = hasQuiz;
        this.quizPoints = quizPoints;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getVillageId() { return villageId; }
    public void setVillageId(int villageId) { this.villageId = villageId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTitleKorean() { return titleKorean; }
    public void setTitleKorean(String titleKorean) { this.titleKorean = titleKorean; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isHasQuiz() { return hasQuiz; }
    public void setHasQuiz(boolean hasQuiz) { this.hasQuiz = hasQuiz; }

    public int getQuizPoints() { return quizPoints; }
    public void setQuizPoints(int quizPoints) { this.quizPoints = quizPoints; }
}
