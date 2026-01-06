package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.koreanlearning.R;

import com.example.myapplication.models.InteractiveLessonQuestion;
import com.example.myapplication.models.Lesson;
import com.example.myapplication.models.QuizQuestion;
import com.example.myapplication.models.User;
import com.example.myapplication.models.Village;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * DatabaseHelper central - gère users, villages, lessons, user_progress
 * et délègue les tables interactives / badges / articles à DatabaseExtensions.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "korean_learning.db";
    private static final int DATABASE_VERSION = 2;

    // Tables principales
    public static final String TABLE_USERS = "users";
    public static final String TABLE_VILLAGES = "villages";
    public static final String TABLE_LESSONS = "lessons";
    public static final String TABLE_USER_PROGRESS = "user_progress";

    // Users columns
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_TOTAL_POINTS = "total_points";
    public static final String COLUMN_TOTAL_BADGES = "total_badges";
    public static final String COLUMN_VILLAGES_UNLOCKED = "villages_unlocked";
    public static final String COLUMN_LAST_LOGIN = "last_login";

    // Villages columns
    public static final String COLUMN_VILLAGE_ID = "id";
    public static final String COLUMN_VILLAGE_NAME = "name";
    public static final String COLUMN_VILLAGE_KOREAN = "korean_name";
    public static final String COLUMN_VILLAGE_DESC = "description";
    public static final String COLUMN_VILLAGE_IMAGE = "image_resource";
    public static final String COLUMN_TOTAL_LESSONS = "total_lessons";

    // Lessons columns
    public static final String COLUMN_LESSON_ID = "id";
    public static final String COLUMN_LESSON_VILLAGE_ID = "village_id";
    public static final String COLUMN_LESSON_TITLE = "title";
    public static final String COLUMN_LESSON_TYPE = "type";
    public static final String COLUMN_LESSON_CONTENT = "content";
    public static final String COLUMN_LESSON_KOREAN = "korean_text";
    public static final String COLUMN_LESSON_TRANSLATION = "translation";
    public static final String COLUMN_LESSON_POINTS = "points";
    public static final String COLUMN_LESSON_IMAGE = "image_resource";

    // Progress columns
    public static final String COLUMN_PROGRESS_ID = "id";
    public static final String COLUMN_PROGRESS_USER_ID = "user_id";
    public static final String COLUMN_PROGRESS_LESSON_ID = "lesson_id";
    public static final String COLUMN_PROGRESS_COMPLETED = "completed";
    public static final String COLUMN_PROGRESS_DATE = "completion_date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // USERS
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_USERNAME + " TEXT UNIQUE," +
                COLUMN_TOTAL_POINTS + " INTEGER DEFAULT 0," +
                COLUMN_TOTAL_BADGES + " INTEGER DEFAULT 0," +
                COLUMN_VILLAGES_UNLOCKED + " INTEGER DEFAULT 1," +
                COLUMN_LAST_LOGIN + " LONG" +
                ")";
        db.execSQL(CREATE_USERS_TABLE);

        // VILLAGES
        String CREATE_VILLAGES_TABLE = "CREATE TABLE " + TABLE_VILLAGES + " (" +
                COLUMN_VILLAGE_ID + " INTEGER PRIMARY KEY," +
                COLUMN_VILLAGE_NAME + " TEXT," +
                COLUMN_VILLAGE_KOREAN + " TEXT," +
                COLUMN_VILLAGE_DESC + " TEXT," +
                COLUMN_VILLAGE_IMAGE + " INTEGER," +
                COLUMN_TOTAL_LESSONS + " INTEGER" +
                ")";
        db.execSQL(CREATE_VILLAGES_TABLE);

        // LESSONS
        String CREATE_LESSONS_TABLE = "CREATE TABLE " + TABLE_LESSONS + " (" +
                COLUMN_LESSON_ID + " INTEGER PRIMARY KEY," +
                COLUMN_LESSON_VILLAGE_ID + " INTEGER," +
                COLUMN_LESSON_TITLE + " TEXT," +
                COLUMN_LESSON_TYPE + " TEXT," +
                COLUMN_LESSON_CONTENT + " TEXT," +
                COLUMN_LESSON_KOREAN + " TEXT," +
                COLUMN_LESSON_TRANSLATION + " TEXT," +
                COLUMN_LESSON_POINTS + " INTEGER," +
                COLUMN_LESSON_IMAGE + " INTEGER," +
                "FOREIGN KEY(" + COLUMN_LESSON_VILLAGE_ID + ") REFERENCES " + TABLE_VILLAGES + "(" + COLUMN_VILLAGE_ID + ")" +
                ")";
        db.execSQL(CREATE_LESSONS_TABLE);

        // USER PROGRESS
        String CREATE_PROGRESS_TABLE = "CREATE TABLE " + TABLE_USER_PROGRESS + " (" +
                COLUMN_PROGRESS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_PROGRESS_USER_ID + " INTEGER," +
                COLUMN_PROGRESS_LESSON_ID + " INTEGER," +
                COLUMN_PROGRESS_COMPLETED + " INTEGER DEFAULT 0," +
                COLUMN_PROGRESS_DATE + " LONG," +
                "FOREIGN KEY(" + COLUMN_PROGRESS_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")," +
                "FOREIGN KEY(" + COLUMN_PROGRESS_LESSON_ID + ") REFERENCES " + TABLE_LESSONS + "(" + COLUMN_LESSON_ID + ")" +
                ")";
        db.execSQL(CREATE_PROGRESS_TABLE);

        // Create extension tables (interactive questions, badges, articles...)
        DatabaseExtensions.createNewTables(db);

        // Insert initial data (villages + lessons + interactive questions)
        insertInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_PROGRESS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LESSONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VILLAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        // Also drop extension tables
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseExtensions.TABLE_INTERACTIVE_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseExtensions.TABLE_CULTURAL_ARTICLES);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseExtensions.TABLE_BADGES);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseExtensions.TABLE_USER_BADGES);

        onCreate(db);
    }

    // ---------------- USERS ----------------

    public long addUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_USERNAME, username);
        v.put(COLUMN_TOTAL_POINTS, 0);
        v.put(COLUMN_TOTAL_BADGES, 0);
        v.put(COLUMN_VILLAGES_UNLOCKED, 1);
        v.put(COLUMN_LAST_LOGIN, System.currentTimeMillis());
        long id = db.insert(TABLE_USERS, null, v);
        return id;
    }

    public User getUserByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_USERS, null, COLUMN_USERNAME + "=?",
                new String[]{username}, null, null, null);
        User user = null;
        if (c.moveToFirst()) {
            user = new User(
                    c.getInt(c.getColumnIndexOrThrow(COLUMN_USER_ID)),
                    c.getString(c.getColumnIndexOrThrow(COLUMN_USERNAME)),
                    c.getInt(c.getColumnIndexOrThrow(COLUMN_TOTAL_POINTS)),
                    c.getInt(c.getColumnIndexOrThrow(COLUMN_TOTAL_BADGES)),
                    c.getInt(c.getColumnIndexOrThrow(COLUMN_VILLAGES_UNLOCKED)),
                    c.getLong(c.getColumnIndexOrThrow(COLUMN_LAST_LOGIN))
            );
        }
        c.close();
        return user;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_USERS, null, COLUMN_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null);
        User user = null;
        if (c.moveToFirst()) {
            user = new User(
                    c.getInt(c.getColumnIndexOrThrow(COLUMN_USER_ID)),
                    c.getString(c.getColumnIndexOrThrow(COLUMN_USERNAME)),
                    c.getInt(c.getColumnIndexOrThrow(COLUMN_TOTAL_POINTS)),
                    c.getInt(c.getColumnIndexOrThrow(COLUMN_TOTAL_BADGES)),
                    c.getInt(c.getColumnIndexOrThrow(COLUMN_VILLAGES_UNLOCKED)),
                    c.getLong(c.getColumnIndexOrThrow(COLUMN_LAST_LOGIN))
            );
        }
        c.close();
        return user;
    }

    public void updateUserPoints(int userId, int newTotalPoints) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_TOTAL_POINTS, newTotalPoints);
        db.update(TABLE_USERS, v, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
    }

    public void updateLastLogin(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_LAST_LOGIN, System.currentTimeMillis());
        db.update(TABLE_USERS, v, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
    }

    // ---------------- VILLAGES ----------------

    public List<Village> getAllVillages(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Village> villages = new ArrayList<>();

        User user = getUserById(userId);
        int unlocked = (user != null) ? user.getVillagesUnlocked() : 1;

        String query = "SELECT v.*, COALESCE(COUNT(l.id),0) as totalLessons, " +
                "COALESCE(SUM(CASE WHEN up.completed=1 THEN 1 ELSE 0 END),0) as completed " +
                "FROM " + TABLE_VILLAGES + " v " +
                "LEFT JOIN " + TABLE_LESSONS + " l ON v.id = l.village_id " +
                "LEFT JOIN " + TABLE_USER_PROGRESS + " up ON l.id = up.lesson_id AND up.user_id = ? " +
                "GROUP BY v.id";

        Cursor c = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (c.moveToFirst()) {
            do {
                int vid = c.getInt(c.getColumnIndexOrThrow(COLUMN_VILLAGE_ID));
                Village village = new Village(
                        vid,
                        c.getString(c.getColumnIndexOrThrow(COLUMN_VILLAGE_NAME)),
                        c.getString(c.getColumnIndexOrThrow(COLUMN_VILLAGE_KOREAN)),
                        c.getString(c.getColumnIndexOrThrow(COLUMN_VILLAGE_DESC)),
                        c.getInt(c.getColumnIndexOrThrow(COLUMN_VILLAGE_IMAGE)),
                        c.getInt(c.getColumnIndexOrThrow(COLUMN_TOTAL_LESSONS)),
                        c.getInt(c.getColumnIndexOrThrow("completed")),
                        vid <= unlocked
                );
                villages.add(village);
            } while (c.moveToNext());
        }
        c.close();
        return villages;
    }

    // ---------------- LESSONS ----------------

    public Lesson getLessonById(int lessonId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_LESSONS, null, COLUMN_LESSON_ID + "=?",
                new String[]{String.valueOf(lessonId)}, null, null, null);
        Lesson l = null;
        if (c.moveToFirst()) {
            l = new Lesson(
                    c.getInt(c.getColumnIndexOrThrow(COLUMN_LESSON_ID)),
                    c.getInt(c.getColumnIndexOrThrow(COLUMN_LESSON_VILLAGE_ID)),
                    c.getString(c.getColumnIndexOrThrow(COLUMN_LESSON_TITLE)),
                    c.getString(c.getColumnIndexOrThrow(COLUMN_LESSON_TYPE)),
                    c.getString(c.getColumnIndexOrThrow(COLUMN_LESSON_CONTENT)),
                    c.getString(c.getColumnIndexOrThrow(COLUMN_LESSON_KOREAN)),
                    c.getString(c.getColumnIndexOrThrow(COLUMN_LESSON_TRANSLATION)),
                    c.getInt(c.getColumnIndexOrThrow(COLUMN_LESSON_POINTS)),
                    false,
                    c.getInt(c.getColumnIndexOrThrow(COLUMN_LESSON_IMAGE))
            );
        }
        c.close();
        return l;
    }

    public List<Lesson> getLessonsByVillage(int villageId, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Lesson> lessons = new ArrayList<>();

        String query = "SELECT l.*, COALESCE(up.completed,0) as completed " +
                "FROM " + TABLE_LESSONS + " l " +
                "LEFT JOIN " + TABLE_USER_PROGRESS + " up ON l.id = up.lesson_id AND up.user_id = ? " +
                "WHERE l.village_id = ?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(villageId)});
        if (c.moveToFirst()) {
            do {
                Lesson lesson = new Lesson(
                        c.getInt(c.getColumnIndexOrThrow(COLUMN_LESSON_ID)),
                        c.getInt(c.getColumnIndexOrThrow(COLUMN_LESSON_VILLAGE_ID)),
                        c.getString(c.getColumnIndexOrThrow(COLUMN_LESSON_TITLE)),
                        c.getString(c.getColumnIndexOrThrow(COLUMN_LESSON_TYPE)),
                        c.getString(c.getColumnIndexOrThrow(COLUMN_LESSON_CONTENT)),
                        c.getString(c.getColumnIndexOrThrow(COLUMN_LESSON_KOREAN)),
                        c.getString(c.getColumnIndexOrThrow(COLUMN_LESSON_TRANSLATION)),
                        c.getInt(c.getColumnIndexOrThrow(COLUMN_LESSON_POINTS)),
                        c.getInt(c.getColumnIndexOrThrow("completed")) == 1,
                        c.getInt(c.getColumnIndexOrThrow(COLUMN_LESSON_IMAGE))
                );
                lessons.add(lesson);
            } while (c.moveToNext());
        }
        c.close();
        return lessons;
    }

    public void markLessonAsCompleted(int userId, int lessonId, int points) {
        SQLiteDatabase db = this.getWritableDatabase();

        // si entrée existe -> ne pas dupliquer
        Cursor c = db.query(TABLE_USER_PROGRESS, null,
                COLUMN_PROGRESS_USER_ID + "=? AND " + COLUMN_PROGRESS_LESSON_ID + "=?",
                new String[]{String.valueOf(userId), String.valueOf(lessonId)}, null, null, null);

        if (!c.moveToFirst()) {
            ContentValues v = new ContentValues();
            v.put(COLUMN_PROGRESS_USER_ID, userId);
            v.put(COLUMN_PROGRESS_LESSON_ID, lessonId);
            v.put(COLUMN_PROGRESS_COMPLETED, 1);
            v.put(COLUMN_PROGRESS_DATE, System.currentTimeMillis());
            db.insert(TABLE_USER_PROGRESS, null, v);

            // add points to user (atomic update)
            db.execSQL("UPDATE " + TABLE_USERS + " SET " + COLUMN_TOTAL_POINTS + " = " + COLUMN_TOTAL_POINTS + " + ? WHERE " + COLUMN_USER_ID + " = ?",
                    new Object[]{points, userId});

            // Check village completion
            Lesson lesson = getLessonById(lessonId);
            if (lesson != null && areAllLessonsCompleted(userId, lesson.getVillageId())) {
                unlockNextVillage(userId);
            }
        }
        c.close();
    }

    public boolean areAllLessonsCompleted(int userId, int villageId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String q = "SELECT COUNT(*) FROM " + TABLE_LESSONS + " l " +
                "LEFT JOIN " + TABLE_USER_PROGRESS + " up ON l.id = up.lesson_id AND up.user_id=? " +
                "WHERE l.village_id=? AND (up.completed IS NULL OR up.completed=0)";
        Cursor c = db.rawQuery(q, new String[]{String.valueOf(userId), String.valueOf(villageId)});
        boolean all = true;
        if (c.moveToFirst()) {
            all = c.getInt(0) == 0;
        }
        c.close();
        return all;
    }

    public void unlockNextVillage(int userId) {
        User u = getUserById(userId);
        if (u == null) return;
        int next = u.getVillagesUnlocked() + 1;
        ContentValues v = new ContentValues();
        v.put(COLUMN_VILLAGES_UNLOCKED, next);
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_USERS, v, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
    }

    // ---------------- QUIZ (interactive_questions via DatabaseExtensions) ----------------

    /**
     * Retourne la liste de QuizQuestion (UI model) pour une leçon, en mappant
     * depuis interactive_questions (DatabaseExtensions).
     */
    public List<QuizQuestion> getQuizQuestionsByLesson(int lessonId) {
        List<QuizQuestion> quizList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Récupère InteractiveLessonQuestion depuis DatabaseExtensions
        List<InteractiveLessonQuestion> iqList = DatabaseExtensions.getInteractiveQuestions(db, lessonId);
        for (InteractiveLessonQuestion iq : iqList) {
            // options stored as List<String>, create incorrect answers (exclude correct)
            List<String> allOptions = iq.getOptions();
            List<String> incorrect = new ArrayList<>();
            for (String opt : allOptions) {
                if (!opt.equals(iq.getCorrectAnswer())) incorrect.add(opt);
            }
            // Ensure we have up to 3 incorrect (pad if necessary)
            while (incorrect.size() < 3) {
                incorrect.add("—"); // placeholder
            }
            int imageRes = 0;
            try {
                // image_resource stored as string path or name; if numeric resource id stored, attempt parse
                imageRes = Integer.parseInt(iq.getImageUrl());
            } catch (Exception ignored) {
            }
            QuizQuestion q = new QuizQuestion(
                    iq.getQuestionText(),
                    iq.getCorrectAnswer(),
                    incorrect,
                    iq.getPoints(),
                    imageRes
            );
            quizList.add(q);
        }
        return quizList;
    }

    // ---------------- INITIAL DATA ----------------

    /**
     * Insert initial villages, lessons and interactive questions (thematic).
     * Les thèmes demandés :
     * 1: salutations, 2: nourriture, 3: famille, 4: objets, 5: culture (exemple)
     */
    private void insertInitialData(SQLiteDatabase db) {
        // villages (id, name, korean, description, imageResId, totalLessons)
        insertVillage(db, 1, "Salutations Village", "인사 마을", "Salutations de base : Bonjour, Au revoir, Enchanté", R.drawable.village_welcome, 3);
        insertVillage(db, 2, "Nourriture Village", "음식 마을", "Vocabulaire du marché et plats", R.drawable.village_market, 3);
        insertVillage(db, 3, "Famille Village", "가족 마을", "Membres de la famille", R.drawable.village_mountain, 3);
        insertVillage(db, 4, "Objets Village", "물건 마을", "Objets du quotidien : ballon, stylo, boite...", R.drawable.village_temple, 3);
        insertVillage(db, 5, "Culture Village", "문화 마을", "Articles et coutumes coréennes", R.drawable.village_coastal, 3);

        // Insert lessons per village: vocab, grammar, pronunciation (IDs: v*10+1..3)
        // Village 1: salutations (use explicit korean words for vocab lesson)
        insertLesson(db, 11, 1, "Bonjour / Au revoir / Enchanté", "VOCABULARY",
                "Mots de salutations courantes", "안녕하세요, 안녕히 가세요, 반갑습니다", "Hello, Goodbye, Nice to meet you", 10, R.drawable.bonjour);
        insertLesson(db, 12, 1, "Formules formelles", "GRAMMAR", "Utilisation des formules formelles", "안녕하십니까", "Formal greeting", 15, R.drawable.bye);
        insertLesson(db, 13, 1, "Se présenter", "PRONUNCIATION", "Comment se présenter", "저는 [Nom] 입니다", "I am [Name]", 10, R.drawable.bonjour);
        // insert interactive questions for salutations
        insertInteractiveQuestionsForTheme(db, 11, "salutations");
        insertInteractiveQuestionsForTheme(db, 12, "salutations");
        insertInteractiveQuestionsForTheme(db, 13, "salutations");

        // Village 2: nourriture
        insertLesson(db, 21, 2, "Plats coréens essentiels", "VOCABULARY", "Kimchi, Bulgogi, Bibimbap", "김치, 불고기, 비빔밥", "Kimchi, Bulgogi, Bibimbap", 10, R.drawable.kimichi);
        insertLesson(db, 22, 2, "Commander au restaurant", "GRAMMAR", "Phrases pour commander", "저는 [음식] 주세요", "Please give me [dish]", 15, R.drawable.kimichi);
        insertLesson(db, 23, 2, "Ustensiles & boissons", "PRONUNCIATION", "Cuillère, baguettes, eau", "숟가락, 젓가락, 물", "Spoon, chopsticks, water", 10, R.drawable.kimichi);
        insertInteractiveQuestionsForTheme(db, 21, "nourriture");
        insertInteractiveQuestionsForTheme(db, 22, "nourriture");
        insertInteractiveQuestionsForTheme(db, 23, "nourriture");

        // Village 3: famille
        insertLesson(db, 31, 3, "Membres de la famille", "VOCABULARY", "Père, Mère, Frère, Sœur", "아버지, 어머니, 형, 누나", "Father, Mother, Brother, Sister", 10, R.drawable.father);
        insertLesson(db, 32, 3, "Titres et respect", "GRAMMAR", "Utilisation honorifique", "님", "Honorifics", 15, R.drawable.mother);
        insertLesson(db, 33, 3, "Parler de sa famille", "PRONUNCIATION", "Présenter sa famille", "이것은 우리 가족입니다", "This is my family", 10, R.drawable.family);
        insertInteractiveQuestionsForTheme(db, 31, "famille");
        insertInteractiveQuestionsForTheme(db, 32, "famille");
        insertInteractiveQuestionsForTheme(db, 33, "famille");

        // Village 4: objets
        insertLesson(db, 41, 4, "Objets quotidiens", "VOCABULARY", "Ballon, stylo, livre...", "공, 펜, 책", "Ball, Pen, Book", 10, R.drawable.family);
        insertLesson(db, 42, 4, "Phrases avec objets", "GRAMMAR", "Poser et donner des objets", "이것은 무엇입니까?", "What is this?", 15, R.drawable.family);
        insertLesson(db, 43, 4, "Prononciation des objets", "PRONUNCIATION", "Prononciation des noms", "공, 펜, 책", "Ball, Pen, Book", 10, R.drawable.family);
        insertInteractiveQuestionsForTheme(db, 41, "objets");
        insertInteractiveQuestionsForTheme(db, 42, "objets");
        insertInteractiveQuestionsForTheme(db, 43, "objets");

        // Village 5: culture
        insertLesson(db, 51, 5, "Fêtes coréennes", "VOCABULARY", "Seollal, Chuseok", "설날, 추석", "Lunar New Year, Thanksgiving", 10, R.drawable.mother);
        insertLesson(db, 52, 5, "Coutumes", "GRAMMAR", "S'incliner, salutations culturelles", "인사하다", "To bow/greet", 15, R.drawable.family);
        insertLesson(db, 53, 5, "Hangeul Basics", "PRONUNCIATION", "Alphabet coréen", "한글", "Hangeul", 10, R.drawable.mother);
        insertInteractiveQuestionsForTheme(db, 51, "culture");
        insertInteractiveQuestionsForTheme(db, 52, "culture");
        insertInteractiveQuestionsForTheme(db, 53, "culture");
    }

    private void insertVillage(SQLiteDatabase db, int id, String name, String korean, String desc, int imageResId, int totalLessons) {
        ContentValues v = new ContentValues();
        v.put(COLUMN_VILLAGE_ID, id);
        v.put(COLUMN_VILLAGE_NAME, name);
        v.put(COLUMN_VILLAGE_KOREAN, korean);
        v.put(COLUMN_VILLAGE_DESC, desc);
        v.put(COLUMN_VILLAGE_IMAGE, imageResId);
        v.put(COLUMN_TOTAL_LESSONS, totalLessons);
        db.insert(TABLE_VILLAGES, null, v);
    }

    private void insertLesson(SQLiteDatabase db, int id, int villageId, String title, String type, String content,
                              String korean, String translation, int points, int imageResId) {
        ContentValues v = new ContentValues();
        v.put(COLUMN_LESSON_ID, id);
        v.put(COLUMN_LESSON_VILLAGE_ID, villageId);
        v.put(COLUMN_LESSON_TITLE, title);
        v.put(COLUMN_LESSON_TYPE, type);
        v.put(COLUMN_LESSON_CONTENT, content);
        v.put(COLUMN_LESSON_KOREAN, korean);
        v.put(COLUMN_LESSON_TRANSLATION, translation);
        v.put(COLUMN_LESSON_POINTS, points);
        v.put(COLUMN_LESSON_IMAGE, imageResId);
        db.insert(TABLE_LESSONS, null, v);
    }

    /**
     * Insère 3 questions thématiques dans la table interactive_questions via DatabaseExtensions.
     */
    private void insertInteractiveQuestionsForTheme(SQLiteDatabase db, int lessonId, String theme) {
        List<InteractiveLessonQuestion> questions = new ArrayList<>();

        String q1, c1, q2, c2, q3, c3;
        List<String> opts1, opts2, opts3;

        switch (theme) {
            case "salutations":
                q1 = "Comment dit-on 'Bonjour' (formel) ?";
                c1 = "안녕하세요";
                opts1 = Arrays.asList("감사합니다", "안녕히 계세요", "죄송합니다");

                q2 = "Quelle est la réponse à 'Merci' ?";
                c2 = "천만에요";
                opts2 = Arrays.asList("안녕하세요", "네", "아니요");

                q3 = "Traduisez 'Au revoir' (à celui qui part)";
                c3 = "안녕히 가세요";
                opts3 = Arrays.asList("안녕히 계세요", "미안합니다", "괜찮아요");
                break;

            case "nourriture":
                q1 = "Quel plat coréen est fermenté ?";
                c1 = "김치";
                opts1 = Arrays.asList("불고기", "비빔밥", "잡채");

                q2 = "Comment dit-on 'eau' ?";
                c2 = "물";
                opts2 = Arrays.asList("밥", "국", "차");

                q3 = "Traduisez : 'Je voudrais du Bulgogi'";
                c3 = "불고기 주세요";
                opts3 = Arrays.asList("김치 주세요", "물 주세요", "밥 주세요");
                break;

            case "famille":
                q1 = "Comment appelle-t-on sa 'mère' ?";
                c1 = "어머니";
                opts1 = Arrays.asList("아버지", "누나", "형");

                q2 = "Quel mot pour 'frère aîné' (par un homme) ?";
                c2 = "형";
                opts2 = Arrays.asList("오빠", "누나", "언니");

                q3 = "Traduisez 'famille'";
                c3 = "가족";
                opts3 = Arrays.asList("친구", "선생님", "학교");
                break;

            case "objets":
                q1 = "Quel mot signifie 'ballon' ?";
                c1 = "공";
                opts1 = Arrays.asList("펜", "책", "상자");

                q2 = "Quel est 'stylo' en coréen ?";
                c2 = "펜";
                opts2 = Arrays.asList("공", "의자", "컴퓨터");

                q3 = "Traduisez 'livre'";
                c3 = "책";
                opts3 = Arrays.asList("창문", "문", "의자");
                break;

            case "culture":
            default:
                q1 = "Quel est le nom du nouvel an lunaire coréen ?";
                c1 = "설날";
                opts1 = Arrays.asList("추석", "한글날", "광복절");

                q2 = "Quel est l'alphabet coréen ?";
                c2 = "한글";
                opts2 = Arrays.asList("한자", "가나", "로마자");

                q3 = "Traduisez 's'incliner'";
                c3 = "인사하다";
                opts3 = Arrays.asList("먹다", "자다", "가다");
                break;
        }

        // construire 3 InteractiveLessonQuestion et les insérer via DatabaseExtensions
        questions.add(new InteractiveLessonQuestion(0, lessonId, "MULTIPLE_CHOICE", q1, "", c1, opts1, "", "", 5, 1));
        questions.add(new InteractiveLessonQuestion(0, lessonId, "MULTIPLE_CHOICE", q2, "", c2, opts2, "", "", 5, 2));
        questions.add(new InteractiveLessonQuestion(0, lessonId, "MULTIPLE_CHOICE", q3, "", c3, opts3, "", "", 5, 3));

        for (InteractiveLessonQuestion iq : questions) {
            DatabaseExtensions.addInteractiveQuestion(db, iq);
        }
    }

    // ---------------- utilitaires (exemples de helpers de génération) ----------------

    private List<String> getRandomVocabIncorrect(String correctWord, int count) {
        String[] pool = {"사람", "집", "학교", "음식", "물", "책", "컴퓨터", "강아지", "고양이", "나무", "자동차", "의자", "연필"};
        List<String> res = new ArrayList<>();
        Random r = new Random();
        while (res.size() < count) {
            String cand = pool[r.nextInt(pool.length)];
            if (!cand.equals(correctWord) && !res.contains(cand)) res.add(cand);
        }
        return res;
    }
}
