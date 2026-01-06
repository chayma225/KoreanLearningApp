package com.example.myapplication.models;

import java.util.List;

/**
 * Modèle pour les questions interactives des leçons
 * Supporte : QCM, Associations, Exercices à trous, Enregistrement audio
 */
public class InteractiveLessonQuestion {
    private int id;
    private int lessonId;
    private String type; // QCM, ASSOCIATION, FILL_BLANK, PRONUNCIATION, GRAMMAR
    private String questionText;
    private String koreanText;
    private String correctAnswer;
    private List<String> options; // Pour QCM et ASSOCIATION
    private String imageUrl; // Pour associations image-mot
    private String audioUrl; // Pour prononciation
    private int points;
    private int order; // Ordre dans la leçon

    public InteractiveLessonQuestion(int id, int lessonId, String type, String questionText,
                                     String koreanText, String correctAnswer, List<String> options,
                                     String imageUrl, String audioUrl, int points, int order) {
        this.id = id;
        this.lessonId = lessonId;
        this.type = type;
        this.questionText = questionText;
        this.koreanText = koreanText;
        this.correctAnswer = correctAnswer;
        this.options = options;
        this.imageUrl = imageUrl;
        this.audioUrl = audioUrl;
        this.points = points;
        this.order = order;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getLessonId() { return lessonId; }
    public void setLessonId(int lessonId) { this.lessonId = lessonId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getKoreanText() { return koreanText; }
    public void setKoreanText(String koreanText) { this.koreanText = koreanText; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }
}
