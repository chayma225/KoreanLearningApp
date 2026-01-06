package com.example.myapplication.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.models.Badge;
import com.example.myapplication.models.CulturalArticle;
import com.example.myapplication.models.InteractiveLessonQuestion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Extensions pour DatabaseHelper - Nouvelles tables pour leçons interactives et articles culturels
 * À intégrer dans DatabaseHelper.java
 */
public class DatabaseExtensions {

    // ==================== TABLE NAMES ====================
    public static final String TABLE_INTERACTIVE_QUESTIONS = "interactive_questions";
    public static final String TABLE_CULTURAL_ARTICLES = "cultural_articles";
    public static final String TABLE_BADGES = "badges";
    public static final String TABLE_USER_BADGES = "user_badges";

    // ==================== COLUMN NAMES ====================
    // Interactive Questions
    public static final String COLUMN_IQ_ID = "id";
    public static final String COLUMN_IQ_LESSON_ID = "lesson_id";
    public static final String COLUMN_IQ_TYPE = "type";
    public static final String COLUMN_IQ_QUESTION = "question_text";
    public static final String COLUMN_IQ_KOREAN = "korean_text";
    public static final String COLUMN_IQ_CORRECT = "correct_answer";
    public static final String COLUMN_IQ_OPTIONS = "options"; // JSON array
    public static final String COLUMN_IQ_IMAGE = "image_resource";
    public static final String COLUMN_IQ_AUDIO = "audio_resource";
    public static final String COLUMN_IQ_POINTS = "points";
    public static final String COLUMN_IQ_ORDER = "question_order";

    // Cultural Articles
    public static final String COLUMN_CA_ID = "id";
    public static final String COLUMN_CA_VILLAGE_ID = "village_id";
    public static final String COLUMN_CA_TITLE = "title";
    public static final String COLUMN_CA_TITLE_KO = "title_korean";
    public static final String COLUMN_CA_CONTENT = "content";
    public static final String COLUMN_CA_IMAGES = "images"; // JSON array
    public static final String COLUMN_CA_CATEGORY = "category";
    public static final String COLUMN_CA_HAS_QUIZ = "has_quiz";
    public static final String COLUMN_CA_QUIZ_POINTS = "quiz_points";

    // Badges
    public static final String COLUMN_BADGE_ID = "id";
    public static final String COLUMN_BADGE_NAME = "name";
    public static final String COLUMN_BADGE_DESC = "description";
    public static final String COLUMN_BADGE_ICON = "icon";
    public static final String COLUMN_BADGE_TYPE = "type";
    public static final String COLUMN_BADGE_REQUIRED = "required_value";

    // User Badges
    public static final String COLUMN_UB_ID = "id";
    public static final String COLUMN_UB_USER_ID = "user_id";
    public static final String COLUMN_UB_BADGE_ID = "badge_id";
    public static final String COLUMN_UB_UNLOCKED_DATE = "unlocked_date";

    /**
     * Créer les nouvelles tables
     */
    public static void createNewTables(SQLiteDatabase db) {
        // Table Interactive Questions
        String CREATE_INTERACTIVE_QUESTIONS = "CREATE TABLE " + TABLE_INTERACTIVE_QUESTIONS + "(" +
                COLUMN_IQ_ID + " INTEGER PRIMARY KEY," +
                COLUMN_IQ_LESSON_ID + " INTEGER," +
                COLUMN_IQ_TYPE + " TEXT," +
                COLUMN_IQ_QUESTION + " TEXT," +
                COLUMN_IQ_KOREAN + " TEXT," +
                COLUMN_IQ_CORRECT + " TEXT," +
                COLUMN_IQ_OPTIONS + " TEXT," +
                COLUMN_IQ_IMAGE + " TEXT," +
                COLUMN_IQ_AUDIO + " TEXT," +
                COLUMN_IQ_POINTS + " INTEGER," +
                COLUMN_IQ_ORDER + " INTEGER," +
                "FOREIGN KEY(" + COLUMN_IQ_LESSON_ID + ") REFERENCES lessons(id)" +
                ")";
        db.execSQL(CREATE_INTERACTIVE_QUESTIONS);

        // Table Cultural Articles
        String CREATE_CULTURAL_ARTICLES = "CREATE TABLE " + TABLE_CULTURAL_ARTICLES + "(" +
                COLUMN_CA_ID + " INTEGER PRIMARY KEY," +
                COLUMN_CA_VILLAGE_ID + " INTEGER," +
                COLUMN_CA_TITLE + " TEXT," +
                COLUMN_CA_TITLE_KO + " TEXT," +
                COLUMN_CA_CONTENT + " TEXT," +
                COLUMN_CA_IMAGES + " TEXT," +
                COLUMN_CA_CATEGORY + " TEXT," +
                COLUMN_CA_HAS_QUIZ + " INTEGER," +
                COLUMN_CA_QUIZ_POINTS + " INTEGER," +
                "FOREIGN KEY(" + COLUMN_CA_VILLAGE_ID + ") REFERENCES villages(id)" +
                ")";
        db.execSQL(CREATE_CULTURAL_ARTICLES);

        // Table Badges
        String CREATE_BADGES = "CREATE TABLE " + TABLE_BADGES + "(" +
                COLUMN_BADGE_ID + " INTEGER PRIMARY KEY," +
                COLUMN_BADGE_NAME + " TEXT," +
                COLUMN_BADGE_DESC + " TEXT," +
                COLUMN_BADGE_ICON + " TEXT," +
                COLUMN_BADGE_TYPE + " TEXT," +
                COLUMN_BADGE_REQUIRED + " INTEGER" +
                ")";
        db.execSQL(CREATE_BADGES);

        // Table User Badges
        String CREATE_USER_BADGES = "CREATE TABLE " + TABLE_USER_BADGES + "(" +
                COLUMN_UB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_UB_USER_ID + " INTEGER," +
                COLUMN_UB_BADGE_ID + " INTEGER," +
                COLUMN_UB_UNLOCKED_DATE + " LONG," +
                "FOREIGN KEY(" + COLUMN_UB_USER_ID + ") REFERENCES users(id)," +
                "FOREIGN KEY(" + COLUMN_UB_BADGE_ID + ") REFERENCES badges(id)" +
                ")";
        db.execSQL(CREATE_USER_BADGES);

        // Insert initial badges
        insertInitialBadges(db);
    }

    /**
     * Insérer les badges initiaux
     */
    private static void insertInitialBadges(SQLiteDatabase db) {
        Badge[] badges = {
                new Badge(1, "Premier Pas", "Complétez votre première leçon", "ic_badge_first", "FIRST_LESSON", 0, false, 0),
                new Badge(2, "Maître du Vocabulaire", "Complétez 10 leçons de vocabulaire", "ic_badge_vocab", "VOCABULARY_MASTER", 10, false, 0),
                new Badge(3, "Grammairien", "Complétez 10 leçons de grammaire", "ic_badge_grammar", "GRAMMAR_MASTER", 10, false, 0),
                new Badge(4, "Locuteur Fluide", "Complétez 10 leçons de prononciation", "ic_badge_pronunciation", "PRONUNCIATION_MASTER", 10, false, 0),
                new Badge(5, "Explorateur", "Débloquez tous les villages", "ic_badge_explorer", "ALL_VILLAGES", 5, false, 0),
                new Badge(6, "Millionnaire", "Accumulez 1000 points", "ic_badge_1000points", "POINTS_MILESTONE", 1000, false, 0),
                new Badge(7, "Érudit Coréen", "Complétez tous les articles culturels", "ic_badge_scholar", "CULTURE_MASTER", 5, false, 0),
        };

        for (Badge badge : badges) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_BADGE_ID, badge.getId());
            values.put(COLUMN_BADGE_NAME, badge.getName());
            values.put(COLUMN_BADGE_DESC, badge.getDescription());
            values.put(COLUMN_BADGE_ICON, badge.getIcon());
            values.put(COLUMN_BADGE_TYPE, badge.getType());
            values.put(COLUMN_BADGE_REQUIRED, badge.getRequiredValue());
            db.insert(TABLE_BADGES, null, values);
        }
    }

    /**
     * Ajouter une question interactive
     */
    public static long addInteractiveQuestion(SQLiteDatabase db, InteractiveLessonQuestion question) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_IQ_LESSON_ID, question.getLessonId());
        values.put(COLUMN_IQ_TYPE, question.getType());
        values.put(COLUMN_IQ_QUESTION, question.getQuestionText());
        values.put(COLUMN_IQ_KOREAN, question.getKoreanText());
        values.put(COLUMN_IQ_CORRECT, question.getCorrectAnswer());
        values.put(COLUMN_IQ_OPTIONS, optionsToJson(question.getOptions()));
        values.put(COLUMN_IQ_IMAGE, question.getImageUrl());
        values.put(COLUMN_IQ_AUDIO, question.getAudioUrl());
        values.put(COLUMN_IQ_POINTS, question.getPoints());
        values.put(COLUMN_IQ_ORDER, question.getOrder());
        return db.insert(TABLE_INTERACTIVE_QUESTIONS, null, values);
    }

    /**
     * Récupérer les questions interactives d'une leçon
     */
    public static List<InteractiveLessonQuestion> getInteractiveQuestions(SQLiteDatabase db, int lessonId) {
        List<InteractiveLessonQuestion> questions = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_INTERACTIVE_QUESTIONS +
                " WHERE " + COLUMN_IQ_LESSON_ID + " = ? ORDER BY " + COLUMN_IQ_ORDER;
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(lessonId)});

        if (cursor.moveToFirst()) {
            do {
                InteractiveLessonQuestion question = new InteractiveLessonQuestion(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IQ_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IQ_LESSON_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IQ_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IQ_QUESTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IQ_KOREAN)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IQ_CORRECT)),
                        jsonToOptions(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IQ_OPTIONS))),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IQ_IMAGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IQ_AUDIO)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IQ_POINTS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IQ_ORDER))
                );
                questions.add(question);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return questions;
    }

    /**
     * Ajouter un article culturel
     */
    public static long addCulturalArticle(SQLiteDatabase db, CulturalArticle article) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CA_VILLAGE_ID, article.getVillageId());
        values.put(COLUMN_CA_TITLE, article.getTitle());
        values.put(COLUMN_CA_TITLE_KO, article.getTitleKorean());
        values.put(COLUMN_CA_CONTENT, article.getContent());
        values.put(COLUMN_CA_IMAGES, imagesToJson(article.getImageUrls()));
        values.put(COLUMN_CA_CATEGORY, article.getCategory());
        values.put(COLUMN_CA_HAS_QUIZ, article.isHasQuiz() ? 1 : 0);
        values.put(COLUMN_CA_QUIZ_POINTS, article.getQuizPoints());
        return db.insert(TABLE_CULTURAL_ARTICLES, null, values);
    }

    /**
     * Récupérer l'article culturel d'un village
     */
    public static CulturalArticle getCulturalArticle(SQLiteDatabase db, int villageId) {
        String query = "SELECT * FROM " + TABLE_CULTURAL_ARTICLES +
                " WHERE " + COLUMN_CA_VILLAGE_ID + " = ? LIMIT 1";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(villageId)});

        CulturalArticle article = null;
        if (cursor.moveToFirst()) {
            article = new CulturalArticle(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CA_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CA_VILLAGE_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CA_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CA_TITLE_KO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CA_CONTENT)),
                    jsonToImages(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CA_IMAGES))),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CA_CATEGORY)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CA_HAS_QUIZ)) == 1,
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CA_QUIZ_POINTS))
            );
        }
        cursor.close();
        return article;
    }

    /**
     * Débloquer un badge pour un utilisateur
     */
    public static void unlockBadge(SQLiteDatabase db, int userId, int badgeId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_UB_USER_ID, userId);
        values.put(COLUMN_UB_BADGE_ID, badgeId);
        values.put(COLUMN_UB_UNLOCKED_DATE, System.currentTimeMillis());
        db.insert(TABLE_USER_BADGES, null, values);
    }

    /**
     * Vérifier si un badge est débloqué
     */
    public static boolean isBadgeUnlocked(SQLiteDatabase db, int userId, int badgeId) {
        String query = "SELECT * FROM " + TABLE_USER_BADGES +
                " WHERE " + COLUMN_UB_USER_ID + " = ? AND " + COLUMN_UB_BADGE_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(badgeId)});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    /**
     * Récupérer les badges débloqués d'un utilisateur
     */
    public static List<Badge> getUnlockedBadges(SQLiteDatabase db, int userId) {
        List<Badge> badges = new ArrayList<>();
        String query = "SELECT b.* FROM " + TABLE_BADGES + " b " +
                "INNER JOIN " + TABLE_USER_BADGES + " ub ON b.id = ub.badge_id " +
                "WHERE ub.user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                Badge badge = new Badge(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BADGE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BADGE_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BADGE_DESC)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BADGE_ICON)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BADGE_TYPE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BADGE_REQUIRED)),
                        true,
                        0
                );
                badges.add(badge);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return badges;
    }

    // ==================== HELPER METHODS ====================
    private static String optionsToJson(List<String> options) {
        if (options == null) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < options.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(options.get(i).replace("\"", "\\\"")).append("\"");
        }
        sb.append("]");
        return sb.toString();
    }

    private static List<String> jsonToOptions(String json) {
        if (json == null || json.isEmpty()) return new ArrayList<>();
        List<String> options = new ArrayList<>();
        String cleaned = json.replace("[", "").replace("]", "").replace("\"", "");
        if (!cleaned.isEmpty()) {
            options.addAll(Arrays.asList(cleaned.split(",")));
        }
        return options;
    }

    private static String imagesToJson(List<String> images) {
        if (images == null) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < images.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(images.get(i).replace("\"", "\\\"")).append("\"");
        }
        sb.append("]");
        return sb.toString();
    }

    private static List<String> jsonToImages(String json) {
        if (json == null || json.isEmpty()) return new ArrayList<>();
        List<String> images = new ArrayList<>();
        String cleaned = json.replace("[", "").replace("]", "").replace("\"", "");
        if (!cleaned.isEmpty()) {
            images.addAll(Arrays.asList(cleaned.split(",")));
        }
        return images;
    }
}
