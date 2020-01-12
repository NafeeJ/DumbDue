package com.kiwicorp.dumbdue

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationText: String = intent.getStringExtra("reminderText")

        val repeatingIntent= Intent(context, MainActivity::class.java)
        repeatingIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context,MainActivity.notificationID,repeatingIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val builder: Notification.Builder = Notification.Builder(context,NotificationChannel.CHANNEL_1_ID)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_android_black_24dp)
            .setContentTitle(notificationText)
            .setAutoCancel(true)//makes notification dismissible when the user swipes it away

        notificationManager.notify(++MainActivity.notificationID,builder.build())
    }
}