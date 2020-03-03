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

        val reminderDataBundle = intent.getBundleExtra("ReminderDataBundle")
        val reminderData: Reminder.ReminderData = reminderDataBundle.getParcelable("ReminderData") as Reminder.ReminderData

        val notificationTitle: String = reminderData.text

//        val startActivityIntent = Intent(context,LockScreenReminderActivity::class.java)
//        startActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        startActivityIntent.putExtra("ReminderDataBundle",reminderDataBundle)
//
//        val startActivityPendingIntent = PendingIntent.getActivity(context,0,startActivityIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val broadcastIntent = Intent(context,LockScreenReminderBroadcastReceiver::class.java)
        broadcastIntent.action = "com.dumbdue.LockScreenReminderBroadcastReceiver"
        broadcastIntent.putExtra("ReminderDataBundle",reminderDataBundle)

        val notificationOnClickPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(context,0,
                broadcastIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val builder: Notification.Builder = Notification.Builder(context, NotificationChannel.CHANNEL_1_ID)
            .setContentIntent(notificationOnClickPendingIntent)
            .setSmallIcon(R.drawable.ic_android_black_24dp)
            .setContentTitle(notificationTitle)
            .setShowWhen(true)
            .setAutoCancel(true)

        notificationManager.notify(++MainActivity.notificationID,builder.build())
    }
}