package com.kiwicorp.dumbdue.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.getSystemService
import com.kiwicorp.dumbdue.CHANNEL_ID_REMINDER
import com.kiwicorp.dumbdue.MainActivity
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.util.minsFromNow
import timber.log.Timber
import java.util.*

class NotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager: NotificationManager = context.getSystemService()
            ?: throw Exception("Notification Manager not found.")

        val title = intent.getStringExtra(REMINDER_TITLE) ?: return
        val reminderId = intent.getStringExtra(REMINDER_ID) ?: return
        val timeInMillis = intent.getLongExtra(REMINDER_TIME_IN_MILLIS, 0)

        val openAppIntent = PendingIntent.getActivity(context,0,Intent(context,MainActivity::class.java),PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = Notification.Builder(context, CHANNEL_ID_REMINDER)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_android_black_24dp)
            .setContentIntent(openAppIntent)
            .setShowWhen(true)
            .setAutoCancel(true).build()

        val minutesFromNow = Calendar.getInstance().apply { this.timeInMillis = timeInMillis }.minsFromNow()

        val id = reminderId.hashCode() + minutesFromNow

        notificationManager.notify(id,notification)

        Timber.d("Sending notification $id")
    }

    companion object {
        const val REMINDER_TITLE: String = "reminder_title"
        const val REMINDER_ID: String = "reminder_id"
        const val REMINDER_TIME_IN_MILLIS: String = "reminder_time_in_millis"
    }
}