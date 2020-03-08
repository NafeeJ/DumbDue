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
        val autoSnoozeVal = reminderData.autoSnoozeVal

        notificationReceiverIntent.putExtra("ReminderDataBundle",intent.getBundleExtra("ReminderDataBundle"))

        //initializes the pending intent to be notification receiver
        notificationPendingIntent = PendingIntent.getBroadcast(context,
            requestCode,notificationReceiverIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        val interval: Long? = when(autoSnoozeVal) {
            Reminder.AUTO_SNOOZE_NONE -> null
            Reminder.AUTO_SNOOZE_MINUTE -> 60000
            Reminder.AUTO_SNOOZE_5_MINUTES -> 5 * 60000
            Reminder.AUTO_SNOOZE_10_MINUTES -> 10 * 60000
            Reminder.AUTO_SNOOZE_15_MINUTES -> 15 * 60000
            Reminder.AUTO_SNOOZE_30_MINUTES -> 30 * 60000
            else -> 60 * 60000
        }

        if (interval != null) {
            //repeating alarm that repeats every minute
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                Calendar.getInstance().timeInMillis,
                interval,
                notificationPendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,Calendar.getInstance().timeInMillis,notificationPendingIntent)
        }
    }
}