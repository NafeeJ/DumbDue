package com.kiwicorp.dumbdue


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build


class NotificationChannel : android.app.Application() {

    companion object { const val CHANNEL_1_ID = "Channel1" }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val channel1 = NotificationChannel(CHANNEL_1_ID, "Reminders", NotificationManager.IMPORTANCE_HIGH )

        channel1.description= "Reminders Notification Channel"

        val manager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel1)
    }

}