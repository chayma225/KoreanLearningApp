package com.example.myapplication.services;


import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.koreanlearning.R;
import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.models.User;


/**
 * Service pour envoyer des notifications de progression en arrière-plan
 */
public class ProgressNotificationService extends IntentService {
    private static final String CHANNEL_ID = "korean_learning_channel";
    private static final int NOTIFICATION_ID = 1;
    private DatabaseHelper databaseHelper;

    public ProgressNotificationService() {
        super("ProgressNotificationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        databaseHelper = new DatabaseHelper(this);
        createNotificationChannel();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Get current user
        SharedPreferences sharedPreferences = getSharedPreferences("KoreanLearning", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        if (username != null) {
            User user = databaseHelper.getUserByUsername(username);
            if (user != null) {
                // Check if user has earned new badges
                if (user.getTotalPoints() > 0 && user.getTotalPoints() % 100 == 0) {
                    sendNotification("Nouveau badge débloqué!",
                            "Vous avez gagné " + user.getTotalPoints() + " points!");
                }

                // Send daily reminder
                long lastLogin = user.getLastLoginTime();
                long currentTime = System.currentTimeMillis();
                long dayInMillis = 24 * 60 * 60 * 1000;

                if (currentTime - lastLogin > dayInMillis) {
                    sendNotification("Bienvenue de retour!",
                            "Continuez votre apprentissage du coréen!");
                }
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Korean Learning Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications for Korean Learning App");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void sendNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}