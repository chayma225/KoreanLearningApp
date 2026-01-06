package com.example.myapplication.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.koreanlearning.R;
import com.example.myapplication.adapters.VillageAdapter;
import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.models.User;
import com.example.myapplication.models.Village;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements VillageAdapter.OnVillageClickListener {

    private RecyclerView villagesRecyclerView;
    private VillageAdapter villageAdapter;
    private DatabaseHelper databaseHelper;
    private User currentUser;
    private TextView pointsTextView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("KoreanLearning", MODE_PRIVATE);

        // Récupérer ou créer utilisateur
        String username = sharedPreferences.getString("username", null);
        if (username == null) {
            username = "Player_" + System.currentTimeMillis();
            sharedPreferences.edit().putString("username", username).apply();
            databaseHelper.addUser(username);
        }

        currentUser = databaseHelper.getUserByUsername(username);
        if (currentUser != null) {
            databaseHelper.updateLastLogin(currentUser.getId());
        }

        initializeUI();
        loadVillages();
    }

    private void initializeUI() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.main_title);
        }

        villagesRecyclerView = findViewById(R.id.villages_recycler_view);
        villagesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        villageAdapter = new VillageAdapter(new ArrayList<>(), this, this);
        villagesRecyclerView.setAdapter(villageAdapter);

        pointsTextView = findViewById(R.id.points_text_view);
        updatePointsDisplay();
    }

    private void loadVillages() {
        if (currentUser != null) {
            List<Village> villages = databaseHelper.getAllVillages(currentUser.getId());
            villageAdapter.updateVillages(villages);
        }
    }

    private void updatePointsDisplay() {
        if (currentUser != null) {
            pointsTextView.setText(String.valueOf(currentUser.getTotalPoints()));
        }
    }

    @Override
    public void onVillageClick(Village village) {
        Intent intent = new Intent(this, VillageActivity.class);
        intent.putExtra("village_id", village.getId());
        intent.putExtra("village_name", village.getName());
        intent.putExtra("user_id", currentUser.getId());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("user_id", currentUser.getId());
            startActivity(intent);
            return true;
        } else if (id == R.id.action_about) {
            Toast.makeText(this, "L'île du coréen v1.0\nJeu sérieux pour apprendre le coréen",
                    Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentUser != null) {
            currentUser = databaseHelper.getUserByUsername(currentUser.getUsername());
            updatePointsDisplay();
            loadVillages(); // recharge villages après progression
        }
    }
}
