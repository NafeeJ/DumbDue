package com.kiwicorp.dumbdue

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.*

class IntermediateReceiver : BroadcastReceiver() {
    private lateinit var notificationReceiverIntent: Intent
    private lateinit var notificationPendingIntent : PendingIntent
    private lateinit var alarmManager: AlarmManager

    override fun onReceive(context: Context, intent: Intent) {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        notificationReceiverIntent = Intent(context,NotificationReceiver::class.java)
        val reminderDataBundle = intent.getBundleExtra("ReminderDataBundle")
        val reminderData = reminderDataBundle.getParcelable<Reminder.ReminderData>("ReminderData")
        val requestCode = reminderData!!.requestCode

        notificationReceiverIntent.putExtra("ReminderDataBundle",intent.getBundleExtra("ReminderDataBundle"))

        //initializes the pending intent to be notification receiver
        notificationPendingIntent = PendingIntent.getBroadcast(context,
            requestCode,notificationReceiverIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        //repeating alarm that repeats every minute
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
            Calendar.getInstance().timeInMillis,
            60000,
            notificationPendingIntent)
    }

}