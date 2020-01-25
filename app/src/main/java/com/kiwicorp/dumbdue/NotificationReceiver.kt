package com.kiwicorp.dumbdue

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.text.SimpleDateFormat
import java.util.*

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationText: String? = intent.getStringExtra("reminderText")

        val notificationOnClickIntent= Intent(context, MainActivity::class.java)
        notificationOnClickIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        val notificationOnClickPendingIntent: PendingIntent = PendingIntent.getActivity(context,
            MainActivity.notificationID,
            notificationOnClickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        val builder: Notification.Builder = Notification.Builder(context,
            NotificationChannel.CHANNEL_1_ID)
            .setContentIntent(notificationOnClickPendingIntent)
            .setSmallIcon(R.drawable.ic_android_black_24dp)
            .setContentTitle(notificationText)
            .setShowWhen(true)
            .setAutoCancel(true)//makes notification dismissible when the user swipes it away

        notificationManager.notify(++MainActivity.notificationID,builder.build())
    }
}