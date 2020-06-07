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
import com.kiwicorp.dumbdue.util.isOverdue
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

            if (reminder.autoSnoozeVal == Reminder.AUTO_SNOOZE_NONE) {
                systemAlarmManager?.let {
                    AlarmManagerCompat.setExactAndAllowWhileIdle(
                        systemAlarmManager,
                        RTC_WAKEUP,
                        reminder.calendar.timeInMillis,
                        notificationIntent) }
            } else {
                systemAlarmManager?.setRepeating(
                    RTC_WAKEUP,
                    reminder.calendar.timeInMillis,
                    reminder.autoSnoozeVal,
                    notificationIntent)
            }

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

        notificationManager.cancel(reminder.id.hashCode())
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
                .putExtra(NotificationBroadcastReceiver.REMINDER_ID, reminder.id),
            FLAG_UPDATE_CURRENT
        )
    }
}