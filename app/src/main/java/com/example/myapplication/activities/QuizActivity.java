package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.koreanlearning.R;
import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.models.QuizQuestion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {
    private SpeechRecognizer recognizer;
    private boolean isRecording = false;

    private DatabaseHelper databaseHelper;
    private int userId;
    private int lessonId;
    private String quizType;

    private List<QuizQuestion> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;

    private LinearLayout quizContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        databaseHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        lessonId = intent.getIntExtra("lesson_id", -1);
        userId = intent.getIntExtra("user_id", -1);
        quizType = intent.getStringExtra("quiz_type");

        if (lessonId == -1 || userId == -1 || quizType == null) {
            Toast.makeText(this, "Erreur de chargement du quiz.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Quiz - " + quizType);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        quizContainer = findViewById(R.id.quiz_container);

        loadQuestions();
        displayQuestion();
    }


    private void loadQuestions() {
        // Charger toutes les questions pour la leçon
        questions = databaseHelper.getQuizQuestionsByLesson(lessonId);

        if (questions.isEmpty()) {
            Toast.makeText(this, "Aucune question trouvée pour ce quiz.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            finishQuiz();
            return;
        }

        QuizQuestion currentQuestion = questions.get(currentQuestionIndex);

        quizContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        View quizView;

        switch (quizType) {
            case "GRAMMAR":
                quizView = inflater.inflate(R.layout.quiz_grammar, quizContainer, false);
                setupGrammarQuiz(quizView, currentQuestion);
                break;

            case "PRONUNCIATION":
                quizView = inflater.inflate(R.layout.quiz_pronunciation, quizContainer, false);
                setupPronunciationQuiz(quizView, currentQuestion);
                break;

            case "VOCABULARY":
            default:
                quizView = inflater.inflate(R.layout.quiz_vocabulary, quizContainer, false);
                setupVocabularyQuiz(quizView, currentQuestion);
                break;
        }

        quizContainer.addView(quizView);
    }

    private void setupVocabularyQuiz(View quizView, QuizQuestion question) {
        TextView questionText = quizView.findViewById(R.id.question_text);
        LinearLayout answerContainer = quizView.findViewById(R.id.answer_buttons_container);

        questionText.setText(question.getQuestionText());

        List<String> options = new ArrayList<>(question.getIncorrectAnswers());
        options.add(question.getCorrectAnswer());
        Collections.shuffle(options);

        for (int i = 0; i < answerContainer.getChildCount(); i++) {
            Button button = (Button) answerContainer.getChildAt(i);
            if (i < options.size()) {
                String option = options.get(i);
                button.setText(option);
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener(v -> checkAnswer(button, option, question.getCorrectAnswer(), question.getPoints()));
            } else {
                button.setVisibility(View.GONE);
            }
        }
    }

    private void setupGrammarQuiz(View quizView, QuizQuestion question) {

        // Ce TextView EXISTE dans ton layout → OK
        TextView questionText = quizView.findViewById(R.id.grammar_sentence);

        // Ton EditText existe → OK
        EditText answerEditText = quizView.findViewById(R.id.answer_buttons_container);

        Button submitButton = quizView.findViewById(R.id.grammar_submit_button);

        String fullSentence = question.getQuestionText();
        String missingWord = question.getCorrectAnswer();
        String sentenceWithBlank = fullSentence.replace(missingWord, "___");

        questionText.setText(sentenceWithBlank);

        submitButton.setOnClickListener(v -> {
            String userAnswer = answerEditText.getText().toString().trim();
            checkAnswer(submitButton, userAnswer, missingWord, question.getPoints());
        });
    }

    private void setupPronunciationQuiz(View quizView, QuizQuestion question) {

        TextView hangeulText = quizView.findViewById(R.id.pronunciation_hangeul);
        TextView romanizationText = quizView.findViewById(R.id.pronunciation_romanization);
        ImageButton recordButton = quizView.findViewById(R.id.record_button);
        TextView statusText = quizView.findViewById(R.id.record_status);

        hangeulText.setText(question.getQuestionText());
        romanizationText.setText(question.getCorrectAnswer()); // romanisation

        SpeechRecognizer recognizer = SpeechRecognizer.createSpeechRecognizer(this);

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR"); // Coréen

        final boolean[] isRecording = {false};

        recordButton.setOnClickListener(v -> {
            if (!isRecording[0]) {
                isRecording[0] = true;
                statusText.setText("🎤 Parlez maintenant...");
                recognizer.startListening(intent);
            } else {
                isRecording[0] = false;
                statusText.setText("⏳ Analyse...");
                recognizer.stopListening();
            }
        });

        recognizer.setRecognitionListener(new RecognitionListener() {

            @Override
            public void onResults(Bundle results) {

                ArrayList<String> matches =
                        results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (matches == null || matches.isEmpty()) {
                    statusText.setText("❌ Aucun son détecté");
                    Toast.makeText(QuizActivity.this, "Aucun son détecté", Toast.LENGTH_SHORT).show();
                    return;
                }

                String spoken = matches.get(0).trim();
                String expected = romanizationText.getText().toString().trim();

                statusText.setText("Vous avez dit : " + spoken);

                // Comparaison flexible
                if (spoken.equalsIgnoreCase(expected)
                        || spoken.toLowerCase().contains(expected.toLowerCase())) {

                    // CORRECT → affiche un DIALOG
                    new AlertDialog.Builder(QuizActivity.this)
                            .setTitle("✔ Bonne prononciation !")
                            .setMessage("Vous avez prononcé correctement : " + spoken)
                            .setPositiveButton("Suivant", (d, w) -> {
                                checkAnswer(recordButton, expected, expected, question.getPoints());
                            })
                            .show();

                } else {
                    // INCORRECT → affiche un TOAST
                    Toast.makeText(QuizActivity.this,
                            "Incorrect : " + spoken,
                            Toast.LENGTH_LONG).show();

                    checkAnswer(recordButton, spoken, expected, question.getPoints());
                }
            }

            @Override public void onError(int e) {
                statusText.setText("Erreur… Réessayez.");
            }

            // Méthodes obligatoires inutiles ici
            @Override public void onReadyForSpeech(Bundle params) {}
            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() {}
            @Override public void onPartialResults(Bundle partialResults) {}
            @Override public void onEvent(int eventType, Bundle params) {}
        });
    }

    private void checkAnswer(View clickedView, String selectedAnswer, String correctAnswer, int points) {
        // Désactiver les boutons si le parent est un LinearLayout
        if (clickedView.getParent() instanceof LinearLayout) {
            LinearLayout container = (LinearLayout) clickedView.getParent();
            for (int i = 0; i < container.getChildCount(); i++) {
                container.getChildAt(i).setEnabled(false);
            }
        }

        if (selectedAnswer.equals(correctAnswer)) {
            score += points;
            new AlertDialog.Builder(this)
                    .setTitle("Félicitations !")
                    .setMessage("Bonne réponse ! Vous gagnez " + points + " points.")
                    .setPositiveButton("Suivant", (dialog, which) -> {
                        currentQuestionIndex++;
                        displayQuestion();
                    })
                    .setCancelable(false)
                    .show();
        } else {
            Toast.makeText(this, "Incorrect ! La bonne réponse était : " + correctAnswer, Toast.LENGTH_SHORT).show();
            currentQuestionIndex++;
            displayQuestion();
        }
    }

    private void finishQuiz() {
        databaseHelper.markLessonAsCompleted(userId, lessonId, score);
        Toast.makeText(this, "Quiz terminé ! Score total : " + score + " points.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
