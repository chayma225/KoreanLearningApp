package com.example.myapplication.activities;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.koreanlearning.R;
import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.models.User;


/**
 * Activité pour afficher le profil de l'utilisateur
 */
public class ProfileActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private int userId;
    private User currentUser;
    private TextView usernameTextView;
    private TextView pointsTextView;
    private TextView badgesTextView;
    private TextView villagesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Get user ID from intent
        userId = getIntent().getIntExtra("user_id", 0);

        // Initialize database
        databaseHelper = new DatabaseHelper(this);

        // Set toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.my_profile);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize UI
        initializeUI();
        loadUserProfile();
    }

    private void initializeUI() {
        usernameTextView = findViewById(R.id.username);
        pointsTextView = findViewById(R.id.total_points);
        badgesTextView = findViewById(R.id.total_badges);
        villagesTextView = findViewById(R.id.villages_unlocked);
    }

    private void loadUserProfile() {
        // Get current user from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("KoreanLearning", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Unknown");
        currentUser = databaseHelper.getUserByUsername(username);

        if (currentUser != null) {
            usernameTextView.setText(currentUser.getUsername());
            pointsTextView.setText(String.valueOf(currentUser.getTotalPoints()));
            badgesTextView.setText(String.valueOf(currentUser.getTotalBadges()));
            villagesTextView.setText(String.valueOf(currentUser.getVillagesUnlocked()) + " / 5");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh profile data
        loadUserProfile();
    }
}