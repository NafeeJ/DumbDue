package com.kiwicorp.dumbdue

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class DumbDueApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeNotificationChannel()
        }

        Timber.plant(Timber.DebugTree())

        AndroidThreeTen.init(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun makeNotificationChannel() {

        val notificationManager: NotificationManager = getSystemService()
            ?: throw Exception("Notification Manager Not Found")

        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        val soundUri = Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${applicationContext.packageName}/${R.raw.pururin}")

        notificationManager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID_REMINDER, "Reminders", NotificationManager.IMPORTANCE_HIGH).apply {
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
                setSound(soundUri,attributes)
            }
        )
    }

}

const val CHANNEL_ID_REMINDER = "reminder_channel_id"