package com.example.myapplication.models;

import java.util.ArrayList;
import java.util.List;

public class QuizQuestion {

    private int id;
    private int lessonId;
    private String type;
    private String questionText;
    private String koreanText;

    private String correctAnswer;
    private List<String> incorrectAnswers;

    private int points;
    private String imageResource;
    private String audioResource;
    private int order;

    // --- Constructeur simplifié pour anciens quiz ---
    public QuizQuestion(String questionText, String correctAnswer, List<String> incorrectAnswers, int points, int imageResId) {
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswers = incorrectAnswers;
        this.points = points;
        this.imageResource = String.valueOf(imageResId);
    }

    // --- Constructeur complet pour interactive_questions ---
    public QuizQuestion(
            int id,
            int lessonId,
            String type,
            String questionText,
            String koreanText,
            String correctAnswer,
            List<String> options,
            String imageResource,
            String audioResource,
            int points,
            int order
    ) {
        this.id = id;
        this.lessonId = lessonId;
        this.type = type;
        this.questionText = questionText;
        this.koreanText = koreanText;

        this.correctAnswer = correctAnswer;
        this.points = points;
        this.imageResource = imageResource;
        this.audioResource = audioResource;
        this.order = order;

        // Toutes les options sauf la bonne sont incorrectes
        this.incorrectAnswers = new ArrayList<>();
        if (options != null) {
            for (String opt : options) {
                if (!opt.equals(correctAnswer)) {
                    incorrectAnswers.add(opt);
                }
            }
        }
    }

    // --- Getters ---
    public int getId() { return id; }

    public int getLessonId() { return lessonId; }

    public String getType() { return type; }

    public String getQuestionText() { return questionText; }

    public String getKoreanText() { return koreanText; }

    public String getCorrectAnswer() { return correctAnswer; }

    public List<String> getIncorrectAnswers() { return incorrectAnswers; }

    public int getPoints() { return points; }

    public String getImageResource() { return imageResource; }

    public String getAudioResource() { return audioResource; }

    public int getOrder() { return order; }
}
