
package com.example.myapplication.activities;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.koreanlearning.R;
import com.example.myapplication.adapters.LessonAdapter;
import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.models.Lesson;

import java.util.List;

public class VillageActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private int villageId, userId;
    private String villageName;
    private ListView lessonsListView;
    private LessonAdapter lessonAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_village);

        Intent intent = getIntent();
        villageId = intent.getIntExtra("village_id", 0);
        userId = intent.getIntExtra("user_id", 0);
        villageName = intent.getStringExtra("village_name");

        databaseHelper = new DatabaseHelper(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(villageName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        lessonsListView = findViewById(R.id.lessons_list_view);
        lessonAdapter = new LessonAdapter(this, R.layout.item_lesson, new java.util.ArrayList<>());
        lessonsListView.setAdapter(lessonAdapter);

        lessonsListView.setOnItemClickListener((parent, view, position, id) -> {
            Lesson lesson = lessonAdapter.getItem(position);
            if (lesson != null) {
                Intent intent1 = new Intent(VillageActivity.this, LessonActivity.class);
                intent1.putExtra("lesson_id", lesson.getId());
                intent1.putExtra("user_id", userId);
                startActivity(intent1);
            }
        });

        loadLessons();
    }

    private void loadLessons() {
        List<Lesson> lessons = databaseHelper.getLessonsByVillage(villageId, userId);
        lessonAdapter.clear();
        lessonAdapter.addAll(lessons);
        lessonAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLessons();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}