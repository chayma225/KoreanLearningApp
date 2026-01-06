package com.example.myapplication.models;

import java.util.List;

public class QuizSession {
    private List<QuizQuestion> questions;
    private int currentIndex = 0;
    private int score = 0;

    public QuizSession(List<QuizQuestion> questions) {
        this.questions = questions;
    }

    public QuizQuestion getCurrentQuestion() {
        if (currentIndex < questions.size()) {
            return questions.get(currentIndex);
        }
        return null;
    }

    public void answerCurrentQuestion(boolean correct) {
        if (correct) score++;
        currentIndex++;
    }

    public boolean isFinished() {
        return currentIndex >= questions.size();
    }

    public int getScore() {
        return score;
    }

    public int getTotalQuestions() {
        return questions.size();
    }
}
