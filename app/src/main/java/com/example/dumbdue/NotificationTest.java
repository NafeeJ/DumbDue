package com.example.dumbdue;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class NotificationTest extends Application {
    public static final String CHANNEL_1_ID = "channel1";


    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_1_ID, "boop boop", NotificationManager.IMPORTANCE_HIGH);

            channel1.setDescription("Bitch get yo ass to work");

            NotificationManager manager = getSystemService(NotificationManager.class);

        }
    }
}
