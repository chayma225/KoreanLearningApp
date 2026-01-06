package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.koreanlearning.R;
import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.database.DatabaseExtensions;
import com.example.myapplication.models.CulturalArticle;

/**
 * Activité pour afficher les articles culturels
 * Affichée après la complétion des 3 leçons d'un village
 */
public class CulturalArticleActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private int villageId;
    private int userId;
    private CulturalArticle article;

    // UI Components
    private TextView titleTextView;
    private TextView titleKoreanTextView;
    private LinearLayout imagesContainer;
    private TextView contentTextView;
    private Button quizButton;
    private Button completeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cultural_article);

        // Get data from intent
        Intent intent = getIntent();
        villageId = intent.getIntExtra("village_id", 0);
        userId = intent.getIntExtra("user_id", 0);

        // Initialize database
        databaseHelper = new DatabaseHelper(this);

        // Set toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Article Culturel");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize UI
        initializeUI();
        loadArticle();
    }

    private void initializeUI() {
        titleTextView = findViewById(R.id.article_title);
        titleKoreanTextView = findViewById(R.id.article_title_korean);
        imagesContainer = findViewById(R.id.images_container);
        contentTextView = findViewById(R.id.article_content);
        quizButton = findViewById(R.id.quiz_button);
        completeButton = findViewById(R.id.complete_button);

        quizButton.setOnClickListener(v -> startQuiz());
        completeButton.setOnClickListener(v -> completeArticle());
    }

    private void loadArticle() {
        article = DatabaseExtensions.getCulturalArticle(
                databaseHelper.getReadableDatabase(), villageId);

        if (article != null) {
            titleTextView.setText(article.getTitle());
            titleKoreanTextView.setText(article.getTitleKorean());
            contentTextView.setText(article.getContent());

            // Load images
            for (String imageName : article.getImageUrls()) {
                int imageResId = getResources().getIdentifier(
                        imageName, "drawable", getPackageName());
                if (imageResId != 0) {
                    ImageView imageView = new ImageView(this);
                    imageView.setImageResource(imageResId);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 300);
                    params.setMargins(0, 16, 0, 16);
                    imageView.setLayoutParams(params);
                    imagesContainer.addView(imageView);
                }
            }

            // Show quiz button only if article has quiz
            if (article.isHasQuiz()) {
                quizButton.setVisibility(Button.VISIBLE);
            } else {
                quizButton.setVisibility(Button.GONE);
            }
        } else {
            Toast.makeText(this, "Article non trouvé", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void startQuiz() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quiz Culturel")
                .setMessage("Testez vos connaissances sur la culture coréenne!")
                .setPositiveButton("Commencer", (dialog, which) -> {
                    // TODO: Implémenter le quiz culturel
                    Toast.makeText(this, "Quiz en développement", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void completeArticle() {
        // Débloquer les badges si applicable
        checkAndUnlockBadges();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Félicitations !")
                .setMessage("Vous avez complété l'article culturel!\n" +
                        (article.isHasQuiz() ? "+" + article.getQuizPoints() + " points bonus" : ""))
                .setPositiveButton("Retour", (dialog, which) -> finish())
                .show();
    }

    private void checkAndUnlockBadges() {
        // Vérifier et débloquer les badges
        // Badge "Explorateur" si tous les villages sont complétés
        // Badge "Érudit Coréen" si tous les articles culturels sont lus
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
