package com.kiwicorp.dumbdue

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import com.kiwicorp.dumbdue.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class DumbDueApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeNotificationChannel()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun makeNotificationChannel() {

        val notificationManager: NotificationManager = getSystemService()
            ?: throw Exception("Notification Manager Not Found")

        notificationManager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID_REMINDER,
                "Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { lockscreenVisibility = Notification.VISIBILITY_PRIVATE }
        )
    }

}

const val CHANNEL_ID_REMINDER = "reminder_channel_id"