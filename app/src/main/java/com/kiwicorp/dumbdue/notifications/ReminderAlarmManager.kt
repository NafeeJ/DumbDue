package com.kiwicorp.dumbdue.notifications

import android.app.AlarmManager
import android.app.AlarmManager.RTC_WAKEUP
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import androidx.core.content.getSystemService
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.util.hasPassed
import com.kiwicorp.dumbdue.util.minsFromNow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages setting, cancelling, and updating alarms for reminders.
 */
@Singleton
class ReminderAlarmManager @Inject constructor(private val context: Context) {
    private val systemAlarmManager: AlarmManager? = context.getSystemService()

    /**
     * Schedules an alarm for a reminder
     */
    fun setAlarm(reminder: Reminder) {
        val notificationIntent = makePendingIntent(reminder)

        notificationIntent?.let {
            Timber.d("Setting alarm for ${reminder.id}")
            systemAlarmManager?.setRepeating(
                RTC_WAKEUP,
                reminder.calendar.timeInMillis,
                60000,
                notificationIntent
            )
        }
    }

    /**
     * Cancels an alarm for a reminder
     */
    fun cancelAlarm(reminder: Reminder) {
        val notificationManager: NotificationManager = context.getSystemService()
            ?: throw Exception("Notification Manager not found.")

        val notificationIntent = makePendingIntent(reminder)

        notificationIntent?.let {
            systemAlarmManager?.cancel(notificationIntent)
        }

        if (reminder.calendar.hasPassed()) {
            for (i in 0..reminder.calendar.minsFromNow()) {
                val id = reminder.id.hashCode() + i
                Timber.d("Cancelling notification $id")
                notificationManager.cancel(id)
            }
        }

    }

    /**
     * Updates the alarm for a reminder
     */
    fun updateAlarm(reminder: Reminder) {
        cancelAlarm(reminder)
        setAlarm(reminder)
    }

    private fun makePendingIntent(reminder: Reminder): PendingIntent? {
        return PendingIntent.getBroadcast(
            context,
            reminder.id.hashCode(),
            Intent(context,NotificationBroadcastReceiver::class.java)
                .putExtra(NotificationBroadcastReceiver.REMINDER_TITLE,reminder.title)
                .putExtra(NotificationBroadcastReceiver.REMINDER_ID, reminder.id)
                .putExtra(NotificationBroadcastReceiver.REMINDER_TIME_IN_MILLIS, reminder.calendar.timeInMillis),
            FLAG_UPDATE_CURRENT
        )
    }
}