package com.kiwicorp.dumbdue


import android.app.*
import android.os.Build
import android.app.NotificationChannel
import android.content.Context


class NotificationChannel : android.app.Application() {

    companion object {//equivalent as public static
        const val CHANNEL_1_ID = "Channel1"//constant string for channel ID
    }

    override fun onCreate() {//creates notification channel on creation of app
        super.onCreate()

        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//checks if android version is greater than or equal to Android Oreo
            val channel1 = NotificationChannel(CHANNEL_1_ID, "Notification Channel 1", NotificationManager.IMPORTANCE_HIGH )

            channel1.description= "Bitch get yo ass to work" //sets description of notification channel

            val manager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager //creates a notification manager
            manager.createNotificationChannel(channel1) //notification manager creates a channel1 notification channel
        }
    }

}