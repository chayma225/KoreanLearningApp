package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.koreanlearning.R;
import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.models.Lesson;

public class LessonActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private Lesson currentLesson;
    private int userId;
    private TextView lessonContentTextView;
    private TextView lessonKoreanTextView;
    private TextView lessonTranslationTextView;
    private Button completeButton;
    private WebView culturalArticleWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(this);

        setContentView(R.layout.activity_lesson);
        lessonContentTextView = findViewById(R.id.lesson_content);
        lessonKoreanTextView = findViewById(R.id.lesson_korean);
        lessonTranslationTextView = findViewById(R.id.lesson_translation);
        completeButton = findViewById(R.id.complete_button);
        culturalArticleWebView = findViewById(R.id.cultural_article_webview);

        // Récupérer les données de l'intent
        Intent intent = getIntent();
        int lessonId = intent.getIntExtra("lesson_id", -1);
        userId = intent.getIntExtra("user_id", -1);

        if (lessonId == -1 || userId == -1) {
            Toast.makeText(this, "Erreur de chargement de la leçon.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentLesson = databaseHelper.getLessonById(lessonId);
        if (currentLesson == null) {
            Toast.makeText(this, "Leçon introuvable.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(currentLesson.getTitle());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setupLessonView(currentLesson);
    }

    private void setupLessonView(Lesson lesson) {
        if ("CULTURAL_ARTICLE".equals(lesson.getType())) {
            // Afficher l'article culturel
            lessonContentTextView.setVisibility(View.GONE);
            lessonKoreanTextView.setVisibility(View.GONE);
            lessonTranslationTextView.setVisibility(View.GONE);
            culturalArticleWebView.setVisibility(View.VISIBLE);

            // Le contenu de l'article est dans lesson.getContent()
            String htmlContent = "<html><body>" + lesson.getContent() + "</body></html>";
            culturalArticleWebView.loadData(htmlContent, "text/html", "UTF-8");

            // Le bouton sert à lancer le mini-quiz
            completeButton.setText("Commencer le Mini-Quiz");
            completeButton.setOnClickListener(v -> startQuiz(lesson));

        } else {
            // Afficher les leçons normales
            lessonContentTextView.setVisibility(View.VISIBLE);
            lessonKoreanTextView.setVisibility(View.VISIBLE);
            lessonTranslationTextView.setVisibility(View.VISIBLE);
            culturalArticleWebView.setVisibility(View.GONE);

            lessonContentTextView.setText(lesson.getContent());
            lessonKoreanTextView.setText(lesson.getKoreanText());
            lessonTranslationTextView.setText(lesson.getTranslation());

            if (lesson.isCompleted()) {
                completeButton.setText("Leçon Complétée");
                completeButton.setEnabled(false);
            } else {
                if ("VOCABULARY".equals(lesson.getType()) ||
                        "GRAMMAR".equals(lesson.getType()) ||
                        "PRONUNCIATION".equals(lesson.getType())) {

                    completeButton.setText("Commencer le Quiz");
                    completeButton.setOnClickListener(v -> startQuiz(lesson));
                } else {
                    completeButton.setText("Marquer comme Complété");
                    completeButton.setOnClickListener(v -> markLessonAsCompleted(lesson));
                }
            }
        }
    }

    private void startQuiz(Lesson lesson) {
        Intent quizIntent = new Intent(LessonActivity.this, QuizActivity.class);
        quizIntent.putExtra("lesson_id", lesson.getId());
        quizIntent.putExtra("user_id", userId);
        quizIntent.putExtra("quiz_type", lesson.getType());
        startActivity(quizIntent);
        finish();
    }

    private void markLessonAsCompleted(Lesson lesson) {
        databaseHelper.markLessonAsCompleted(userId, lesson.getId(), lesson.getPoints());
        Toast.makeText(this, "Leçon complétée ! +" + lesson.getPoints() + " points.", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}