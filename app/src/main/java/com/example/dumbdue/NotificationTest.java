package com.example.dumbdue;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class NotificationTest extends Application {
    public static final String CHANNEL_1_ID = "channel1"; //string for channel ID


    @Override
    public void onCreate() {//creates notification channel on creation of app
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //checks if andriod version is greater than or equal to Oreo
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_1_ID, "I am telling you to get to work", NotificationManager.IMPORTANCE_HIGH); //creates instance of notification channel

            channel1.setDescription("Bitch get yo ass to work"); //sets description of notification channel

            NotificationManager manager = getSystemService(NotificationManager.class); //creates a notification manager
            manager.createNotificationChannel(channel1); //notification manager creates channel1 notification channel

        }
    }
}
