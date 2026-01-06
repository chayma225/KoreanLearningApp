package com.example.myapplication.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.koreanlearning.R;
import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.database.DatabaseExtensions;
import com.example.myapplication.models.InteractiveLessonQuestion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Activité pour les leçons interactives
 * Supporte : QCM, Associations, Exercices à trous, Prononciation
 */
public class InteractiveLessonActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private int lessonId;
    private int userId;
    private int villageId;
    private List<InteractiveLessonQuestion> questions;
    private int currentQuestionIndex = 0;
    private int totalPoints = 0;
    private int correctAnswers = 0;

    // UI Components
    private TextView questionTextView;
    private TextView koreanTextView;
    private TextView progressTextView;
    private ProgressBar progressBar;
    private LinearLayout questionContainer;
    private Button nextButton;
    private Button submitButton;
    private Button recordButton;
    private Button playButton;

    // Media
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String audioFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interactive_lesson);

        // Get data from intent
        Intent intent = getIntent();
        lessonId = intent.getIntExtra("lesson_id", 0);
        userId = intent.getIntExtra("user_id", 0);
        villageId = intent.getIntExtra("village_id", 0);

        // Initialize database
        databaseHelper = new DatabaseHelper(this);

        // Initialize UI
        initializeUI();
        loadQuestions();
        displayQuestion();
    }

    private void initializeUI() {
        questionTextView = findViewById(R.id.question_text);
        koreanTextView = findViewById(R.id.korean_text);
        progressTextView = findViewById(R.id.progress_text);
        progressBar = findViewById(R.id.progress_bar);
        questionContainer = findViewById(R.id.question_container);
        nextButton = findViewById(R.id.next_button);
        submitButton = findViewById(R.id.submit_button);
        recordButton = findViewById(R.id.record_button);
        playButton = findViewById(R.id.play_button);

        nextButton.setOnClickListener(v -> nextQuestion());
        submitButton.setOnClickListener(v -> submitAnswer());
        recordButton.setOnClickListener(v -> startRecording());
        playButton.setOnClickListener(v -> playRecording());

        audioFilePath = getCacheDir() + "/audio_record.3gp";
    }

    private void loadQuestions() {
        questions = DatabaseExtensions.getInteractiveQuestions(
                databaseHelper.getReadableDatabase(), lessonId);
        if (questions.isEmpty()) {
            Toast.makeText(this, "Aucune question trouvée", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            showCompletionDialog();
            return;
        }

        InteractiveLessonQuestion question = questions.get(currentQuestionIndex);
        questionTextView.setText(question.getQuestionText());
        koreanTextView.setText(question.getKoreanText());

        // Update progress
        progressTextView.setText((currentQuestionIndex + 1) + " / " + questions.size());
        progressBar.setProgress((int) ((currentQuestionIndex + 1.0 / questions.size()) * 100));

        // Clear previous UI
        questionContainer.removeAllViews();

        // Display question based on type
        switch (question.getType()) {
            case "QCM":
                displayQCM(question);
                break;
            case "FILL_BLANK":
                displayFillBlank(question);
                break;
            case "GRAMMAR":
                displayGrammar(question);
                break;
            case "PRONUNCIATION":
                displayPronunciation(question);
                break;
            case "ASSOCIATION":
                displayAssociation(question);
                break;
        }
    }

    private void displayQCM(InteractiveLessonQuestion question) {
        RadioGroup radioGroup = new RadioGroup(this);
        List<String> options = new ArrayList<>(question.getOptions());
        Collections.shuffle(options);

        for (String option : options) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(option);
            radioButton.setTag(option);
            radioGroup.addView(radioButton);
        }

        questionContainer.addView(radioGroup);
        submitButton.setVisibility(Button.VISIBLE);
        submitButton.setTag(radioGroup);
    }

    private void displayFillBlank(InteractiveLessonQuestion question) {
        EditText editText = new EditText(this);
        editText.setHint("Complétez la phrase...");
        editText.setTag("answer");
        questionContainer.addView(editText);
        submitButton.setVisibility(Button.VISIBLE);
        submitButton.setTag(editText);
    }

    private void displayGrammar(InteractiveLessonQuestion question) {
        RadioGroup radioGroup = new RadioGroup(this);
        List<String> options = new ArrayList<>(question.getOptions());
        Collections.shuffle(options);

        for (String option : options) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(option);
            radioButton.setTag(option);
            radioGroup.addView(radioButton);
        }

        questionContainer.addView(radioGroup);
        submitButton.setVisibility(Button.VISIBLE);
        submitButton.setTag(radioGroup);
    }

    private void displayPronunciation(InteractiveLessonQuestion question) {
        TextView instructionText = new TextView(this);
        instructionText.setText("Enregistrez votre prononciation du mot : " + question.getKoreanText());
        questionContainer.addView(instructionText);

        recordButton.setVisibility(Button.VISIBLE);
        playButton.setVisibility(Button.VISIBLE);
        submitButton.setVisibility(Button.VISIBLE);
    }

    private void displayAssociation(InteractiveLessonQuestion question) {
        // Afficher l'image et les options
        ImageView imageView = new ImageView(this);
        int imageResId = getResources().getIdentifier(
                question.getImageUrl(), "drawable", getPackageName());
        if (imageResId != 0) {
            imageView.setImageResource(imageResId);
        }
        questionContainer.addView(imageView);

        RadioGroup radioGroup = new RadioGroup(this);
        List<String> options = new ArrayList<>(question.getOptions());
        Collections.shuffle(options);

        for (String option : options) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(option);
            radioButton.setTag(option);
            radioGroup.addView(radioButton);
        }

        questionContainer.addView(radioGroup);
        submitButton.setVisibility(Button.VISIBLE);
        submitButton.setTag(radioGroup);
    }

    private void submitAnswer() {
        InteractiveLessonQuestion question = questions.get(currentQuestionIndex);
        boolean isCorrect = false;

        switch (question.getType()) {
            case "QCM":
            case "GRAMMAR":
            case "ASSOCIATION":
                RadioGroup radioGroup = (RadioGroup) submitButton.getTag();
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selectedButton = radioGroup.findViewById(selectedId);
                    String answer = selectedButton.getText().toString();
                    isCorrect = answer.equals(question.getCorrectAnswer());
                }
                break;

            case "FILL_BLANK":
                EditText editText = (EditText) submitButton.getTag();
                String answer = editText.getText().toString().trim();
                isCorrect = answer.equalsIgnoreCase(question.getCorrectAnswer());
                break;

            case "PRONUNCIATION":
                // Vérification simplifiée - en production, utiliser une API de reconnaissance vocale
                isCorrect = true;
                break;
        }

        if (isCorrect) {
            correctAnswers++;
            totalPoints += question.getPoints();
            Toast.makeText(this, "Correct ! +" + question.getPoints() + " points", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Incorrect. Réponse : " + question.getCorrectAnswer(), Toast.LENGTH_SHORT).show();
        }

        nextQuestion();
    }

    private void nextQuestion() {
        currentQuestionIndex++;
        displayQuestion();
    }

    private void startRecording() {
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.prepare();
            mediaRecorder.start();
            recordButton.setText("Arrêter");
            Toast.makeText(this, "Enregistrement en cours...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playRecording() {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(audioFilePath);
                mediaPlayer.prepare();
            }
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showCompletionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Leçon complétée !")
                .setMessage("Vous avez obtenu " + totalPoints + " points\n" +
                        "Réponses correctes : " + correctAnswers + " / " + questions.size())
                .setPositiveButton("Continuer", (dialog, which) -> {
                    // Sauvegarder la progression
                    databaseHelper.markLessonAsCompleted(userId, lessonId, totalPoints);
                    finish();
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaRecorder != null) {
            mediaRecorder.release();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
