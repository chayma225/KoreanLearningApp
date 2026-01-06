# Guide d'Implémentation Complet - Korean Learning App

## 📋 Table des Matières
1. [Structure du Projet](#structure-du-projet)
2. [Fichiers Créés](#fichiers-créés)
3. [Intégration de la Base de Données](#intégration-de-la-base-de-données)
4. [Modification du DatabaseHelper](#modification-du-databasehelper)
5. [Intégration des Activités](#intégration-des-activités)
6. [Insertion des Données](#insertion-des-données)
7. [Tests et Débogage](#tests-et-débogage)

---

## Structure du Projet

```
KoreanLearningApp/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/myapplication/
│   │   │   │   ├── activities/
│   │   │   │   │   ├── MainActivity.java (existant)
│   │   │   │   │   ├── VillageActivity.java (existant)
│   │   │   │   │   ├── LessonActivity.java (existant)
│   │   │   │   │   ├── QuizActivity.java (existant)
│   │   │   │   │   ├── ProfileActivity.java (existant)
│   │   │   │   │   ├── InteractiveLessonActivity.java ✨ NOUVEAU
│   │   │   │   │   └── CulturalArticleActivity.java ✨ NOUVEAU
│   │   │   │   ├── models/
│   │   │   │   │   ├── User.java (existant)
│   │   │   │   │   ├── Village.java (existant)
│   │   │   │   │   ├── Lesson.java (existant)
│   │   │   │   │   ├── QuizQuestion.java (existant)
│   │   │   │   │   ├── InteractiveLessonQuestion.java ✨ NOUVEAU
│   │   │   │   │   ├── CulturalArticle.java ✨ NOUVEAU
│   │   │   │   │   └── Badge.java ✨ NOUVEAU
│   │   │   │   ├── database/
│   │   │   │   │   ├── DatabaseHelper.java (À MODIFIER)
│   │   │   │   │   └── DatabaseExtensions.java ✨ NOUVEAU
│   │   │   │   ├── adapters/
│   │   │   │   │   ├── VillageAdapter.java (existant)
│   │   │   │   │   └── LessonAdapter.java (existant)
│   │   │   │   └── services/
│   │   │   │       └── ProgressNotificationService.java (existant)
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml (existant)
│   │   │   │   │   ├── activity_village.xml (existant)
│   │   │   │   │   ├── activity_lesson.xml (existant)
│   │   │   │   │   ├── activity_quiz.xml (existant)
│   │   │   │   │   ├── activity_profile.xml (existant)
│   │   │   │   │   ├── activity_interactive_lesson.xml ✨ NOUVEAU
│   │   │   │   │   ├── activity_cultural_article.xml ✨ NOUVEAU
│   │   │   │   │   ├── item_lesson.xml (existant)
│   │   │   │   │   └── item_village.xml (existant)
│   │   │   │   ├── drawable/
│   │   │   │   │   ├── village_*.png (images)
│   │   │   │   │   ├── ic_badge_*.png (badges)
│   │   │   │   │   └── article_*.png (articles culturels)
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml (existant)
│   │   │   │   │   ├── strings.xml (À MODIFIER)
│   │   │   │   │   └── styles.xml (existant)
│   │   │   │   └── menu/
│   │   │   │       └── menu_main.xml (existant)
│   │   │   └── AndroidManifest.xml (À MODIFIER)
│   │   └── test/
│   └── build.gradle
├── INTERACTIVE_LESSONS_DATA.md ✨ NOUVEAU
└── IMPLEMENTATION_GUIDE.md ✨ NOUVEAU
```

---

## Fichiers Créés

### 1. **Modèles (Models)**
- `InteractiveLessonQuestion.java` - Questions interactives
- `CulturalArticle.java` - Articles culturels
- `Badge.java` - Système de badges

### 2. **Base de Données**
- `DatabaseExtensions.java` - Extensions pour les nouvelles tables

### 3. **Activités (Activities)**
- `InteractiveLessonActivity.java` - Leçons interactives
- `CulturalArticleActivity.java` - Articles culturels

### 4. **Layouts XML**
- `activity_interactive_lesson.xml` - Interface des leçons interactives
- `activity_cultural_article.xml` - Interface des articles culturels

### 5. **Documentation**
- `INTERACTIVE_LESSONS_DATA.md` - Données pour les leçons
- `IMPLEMENTATION_GUIDE.md` - Ce guide

---

## Intégration de la Base de Données

### Étape 1 : Modifier DatabaseHelper.java

Ajouter les imports au début du fichier :
```java
import com.example.myapplication.models.Badge;
import com.example.myapplication.models.CulturalArticle;
import com.example.myapplication.models.InteractiveLessonQuestion;
```

Modifier la version de la base de données :
```java
private static final int DATABASE_VERSION = 3; // Augmenter de 2 à 3
```

Modifier la méthode `onCreate()` pour créer les nouvelles tables :
```java
@Override
public void onCreate(SQLiteDatabase db) {
    // ... code existant ...
    
    // Créer les nouvelles tables
    DatabaseExtensions.createNewTables(db);
    
    insertInitialData(db);
}
```

Modifier la méthode `onUpgrade()` pour gérer la migration :
```java
@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    if (oldVersion < 3) {
        // Créer les nouvelles tables sans supprimer les anciennes
        try {
            DatabaseExtensions.createNewTables(db);
        } catch (Exception e) {
            // Les tables existent peut-être déjà
        }
    } else {
        // Ancien comportement
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_PROGRESS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LESSONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VILLAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }
}
```

### Étape 2 : Ajouter les méthodes d'accès aux données

Ajouter dans `DatabaseHelper.java` :
```java
// Méthodes pour les questions interactives
public long addInteractiveQuestion(InteractiveLessonQuestion question) {
    return DatabaseExtensions.addInteractiveQuestion(this.getWritableDatabase(), question);
}

public List<InteractiveLessonQuestion> getInteractiveQuestions(int lessonId) {
    return DatabaseExtensions.getInteractiveQuestions(this.getReadableDatabase(), lessonId);
}

// Méthodes pour les articles culturels
public long addCulturalArticle(CulturalArticle article) {
    return DatabaseExtensions.addCulturalArticle(this.getWritableDatabase(), article);
}

public CulturalArticle getCulturalArticle(int villageId) {
    return DatabaseExtensions.getCulturalArticle(this.getReadableDatabase(), villageId);
}

// Méthodes pour les badges
public void unlockBadge(int userId, int badgeId) {
    DatabaseExtensions.unlockBadge(this.getWritableDatabase(), userId, badgeId);
}

public boolean isBadgeUnlocked(int userId, int badgeId) {
    return DatabaseExtensions.isBadgeUnlocked(this.getReadableDatabase(), userId, badgeId);
}

public List<Badge> getUnlockedBadges(int userId) {
    return DatabaseExtensions.getUnlockedBadges(this.getReadableDatabase(), userId);
}
```

---

## Modification du AndroidManifest.xml

Ajouter les nouvelles activités :
```xml
<activity
    android:name=".activities.InteractiveLessonActivity"
    android:label="@string/interactive_lesson" />

<activity
    android:name=".activities.CulturalArticleActivity"
    android:label="@string/cultural_article" />
```

Ajouter les permissions pour l'enregistrement audio :
```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

---

## Insertion des Données

### Créer une classe DataSeeder

```java
package com.example.myapplication.database;

import android.content.Context;
import com.example.myapplication.models.CulturalArticle;
import com.example.myapplication.models.InteractiveLessonQuestion;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataSeeder {
    public static void seedInteractiveLessons(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        
        // VILLAGE 1 - Leçon 1 - Vocabulaire
        List<InteractiveLessonQuestion> questions = new ArrayList<>();
        
        // Question 1 : QCM
        questions.add(new InteractiveLessonQuestion(
            1, 1, "QCM",
            "Quel est le mot coréen pour 'Bonjour'?",
            "안녕하세요",
            "안녕하세요",
            Arrays.asList("안녕하세요", "감사합니다", "미안합니다", "안녕히 가세요"),
            null, null, 10, 1
        ));
        
        // Question 2 : Association
        questions.add(new InteractiveLessonQuestion(
            2, 1, "ASSOCIATION",
            "Associez l'image à la maison",
            "집",
            "집",
            Arrays.asList("집", "학교", "병원", "은행"),
            "village_welcome", null, 10, 2
        ));
        
        // ... Ajouter toutes les questions selon INTERACTIVE_LESSONS_DATA.md
        
        for (InteractiveLessonQuestion q : questions) {
            dbHelper.addInteractiveQuestion(q);
        }
    }
    
    public static void seedCulturalArticles(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        
        // Article 1 : Hangeul
        CulturalArticle article = new CulturalArticle(
            1, 1,
            "Hangeul - L'alphabet coréen",
            "한글",
            "Le Hangeul est l'alphabet coréen créé par le roi Sejong...",
            Arrays.asList("hangeul_alphabet", "king_sejong", "hangeul_structure"),
            "LANGUAGE",
            true, 20
        );
        dbHelper.addCulturalArticle(article);
        
        // ... Ajouter les autres articles
    }
}
```

### Appeler le seeder dans MainActivity

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    // Seed data on first launch
    SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
    if (!prefs.getBoolean("data_seeded", false)) {
        DataSeeder.seedInteractiveLessons(this);
        DataSeeder.seedCulturalArticles(this);
        
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("data_seeded", true);
        editor.apply();
    }
    
    // ... reste du code
}
```

---

## Intégration des Activités

### Modifier VillageActivity pour lancer les leçons interactives

```java
private void loadLessons() {
    List<Lesson> lessons = databaseHelper.getLessonsByVillage(villageId, userId);
    lessonAdapter = new LessonAdapter(this, R.layout.item_lesson, lessons);
    lessonsListView.setAdapter(lessonAdapter);
    
    // Set click listener
    lessonsListView.setOnItemClickListener((parent, view, position, id) -> {
        Lesson lesson = lessonAdapter.getItem(position);
        if (lesson != null) {
            // Lancer InteractiveLessonActivity au lieu de LessonActivity
            Intent intent = new Intent(VillageActivity.this, InteractiveLessonActivity.class);
            intent.putExtra("lesson_id", lesson.getId());
            intent.putExtra("lesson_title", lesson.getTitle());
            intent.putExtra("user_id", userId);
            intent.putExtra("village_id", villageId);
            startActivity(intent);
        }
    });
}
```

### Ajouter la logique pour afficher l'article culturel

```java
@Override
protected void onResume() {
    super.onResume();
    loadLessons();
    
    // Vérifier si toutes les leçons sont complétées
    int completedLessons = databaseHelper.getCompletedLessonsCount(userId, villageId);
    if (completedLessons >= 3) {
        // Afficher le bouton pour l'article culturel
        Button culturalArticleButton = new Button(this);
        culturalArticleButton.setText("Lire l'article culturel");
        culturalArticleButton.setOnClickListener(v -> {
            Intent intent = new Intent(VillageActivity.this, CulturalArticleActivity.class);
            intent.putExtra("village_id", villageId);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });
        // Ajouter le bouton au layout
    }
}
```

---

## Système de Progression et Points

### Modifier la méthode markLessonAsCompleted

```java
public void markLessonAsCompleted(int userId, int lessonId, int points) {
    SQLiteDatabase db = this.getWritableDatabase();
    
    // Marquer la leçon comme complétée
    ContentValues values = new ContentValues();
    values.put(COLUMN_PROGRESS_COMPLETED, 1);
    values.put(COLUMN_PROGRESS_DATE, System.currentTimeMillis());
    db.update(TABLE_USER_PROGRESS, values,
            COLUMN_PROGRESS_USER_ID + "=? AND " + COLUMN_PROGRESS_LESSON_ID + "=?",
            new String[]{String.valueOf(userId), String.valueOf(lessonId)});
    
    // Ajouter les points
    User user = getUserById(userId);
    if (user != null) {
        int newPoints = user.getTotalPoints() + points;
        updateUserPoints(userId, newPoints);
        
        // Vérifier et débloquer les badges
        checkAndUnlockBadges(userId, newPoints);
    }
}

private void checkAndUnlockBadges(int userId, int totalPoints) {
    // Badge "Premier Pas"
    if (!isBadgeUnlocked(userId, 1)) {
        unlockBadge(userId, 1);
    }
    
    // Badge "Millionnaire"
    if (totalPoints >= 1000 && !isBadgeUnlocked(userId, 6)) {
        unlockBadge(userId, 6);
    }
    
    // ... Autres vérifications de badges
}
```

---

## Tests et Débogage

### Checklist de Test

- [ ] Les nouvelles tables sont créées correctement
- [ ] Les questions interactives s'affichent correctement
- [ ] Les réponses sont validées correctement
- [ ] Les points sont ajoutés correctement
- [ ] Les badges sont débloqués automatiquement
- [ ] Les articles culturels s'affichent après 3 leçons
- [ ] La progression est sauvegardée
- [ ] L'enregistrement audio fonctionne
- [ ] Les images s'affichent correctement

### Commandes de Débogage

```bash
# Vérifier la base de données
adb shell
sqlite3 /data/data/com.example.myapplication/databases/korean_learning.db

# Vérifier les tables
.tables

# Vérifier le contenu
SELECT * FROM interactive_questions;
SELECT * FROM cultural_articles;
SELECT * FROM badges;
```

---

## Ressources Requises

### Images à Ajouter (res/drawable/)

**Villages :**
- village_welcome.png
- village_market.png
- village_temple.png
- village_mountain.png
- village_coastal.png

**Articles Culturels :**
- hangeul_alphabet.png
- king_sejong.png
- korean_food.png
- buddhist_temple.png
- korean_mountains.png
- korean_beach.png

**Badges :**
- ic_badge_first.png
- ic_badge_vocab.png
- ic_badge_grammar.png
- ic_badge_pronunciation.png
- ic_badge_explorer.png
- ic_badge_1000points.png
- ic_badge_scholar.png

### Strings à Ajouter (res/values/strings.xml)

```xml
<string name="interactive_lesson">Leçon Interactive</string>
<string name="cultural_article">Article Culturel</string>
<string name="vocabulary">Vocabulaire</string>
<string name="grammar">Grammaire</string>
<string name="pronunciation">Prononciation</string>
<string name="record">Enregistrer</string>
<string name="play">Écouter</string>
<string name="submit">Valider</string>
<string name="next">Suivant</string>
<string name="correct">Correct !</string>
<string name="incorrect">Incorrect</string>
```

---

## Prochaines Étapes

1. ✅ Créer les modèles et extensions de base de données
2. ✅ Créer les activités interactives
3. ✅ Créer les layouts XML
4. ⏳ Ajouter les images
5. ⏳ Implémenter le seeder de données
6. ⏳ Tester complètement
7. ⏳ Optimiser les performances

---

## Support et Dépannage

### Erreurs Courantes

**Erreur : "Table not found"**
- Solution : Augmenter la version de la base de données et appeler `onUpgrade()`

**Erreur : "Column not found"**
- Solution : Vérifier les noms des colonnes dans `DatabaseExtensions`

**Erreur : "NullPointerException"**
- Solution : Vérifier que les données sont bien insérées avec le seeder

---

## Conclusion

Ce guide fournit une implémentation complète du système de leçons interactives et d'articles culturels. Suivez les étapes dans l'ordre pour une intégration sans problème.

**Total de points possibles : 375 points**
**Nombre de badges : 7**
**Nombre de villages : 5**
**Nombre de leçons : 15 (3 par village)**

Bonne chance ! 🎓
