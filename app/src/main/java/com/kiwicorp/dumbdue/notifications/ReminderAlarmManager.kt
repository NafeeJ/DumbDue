package com.kiwicorp.dumbdue.notifications

import android.app.AlarmManager
import android.app.AlarmManager.RTC_WAKEUP
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.getSystemService
import com.kiwicorp.dumbdue.data.Reminder
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
        setAlarm(reminder.title,reminder.id,reminder.dueDate.toInstant().toEpochMilli(), reminder.autoSnoozeVal)
    }
    /**
     * Schedules an alarm.
     *
     * We do not use set repeating because [AlarmManager]'s setRepeating() does will not fire when
     * device is idle
     */
    fun setAlarm(title: String, id: String, timeInMillis: Long, autoSnooze: Long) {
        val notificationIntent = makePendingIntent(title,id,timeInMillis, autoSnooze)

        notificationIntent?.let {
            Timber.d("Setting alarm for $id")

            systemAlarmManager?.let {
                AlarmManagerCompat.setExactAndAllowWhileIdle(
                    systemAlarmManager,
                    RTC_WAKEUP,
                    timeInMillis,
                    notificationIntent
                )
            }
        }
    }

    /**
     * Cancels an alarm for a reminder
     */
    fun cancelAlarm(reminder: Reminder) {
        val notificationManager: NotificationManager = context.getSystemService()
            ?: throw Exception("Notification Manager not found.")

        val notificationIntent = makePendingIntent(reminder.title,reminder.id,reminder.dueDate.toInstant().toEpochMilli(),reminder.autoSnoozeVal)

        notificationIntent?.let {
            systemAlarmManager?.cancel(notificationIntent)
        }

        notificationManager.cancel(reminder.id.hashCode())
    }

    /**
     * Updates the alarm for a reminder
     */
    fun updateAlarm(reminder: Reminder) {
        cancelAlarm(reminder)
        setAlarm(reminder)
    }

    private fun makePendingIntent(title: String, id: String, timeInMillis: Long, autoSnooze: Long): PendingIntent? {
        return PendingIntent.getBroadcast(
            context,
            id.hashCode(),
            Intent(context,NotificationBroadcastReceiver::class.java)
                .putExtra(NotificationBroadcastReceiver.REMINDER_TITLE,title)
                .putExtra(NotificationBroadcastReceiver.REMINDER_ID, id)
                .putExtra(NotificationBroadcastReceiver.REMINDER_TIME_IN_MILLIS, timeInMillis)
                .putExtra(NotificationBroadcastReceiver.REMINDER_AUTO_SNOOZE, autoSnooze),
            FLAG_UPDATE_CURRENT
        )
    }
}