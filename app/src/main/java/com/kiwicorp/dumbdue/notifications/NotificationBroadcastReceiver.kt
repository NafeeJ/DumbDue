package com.kiwicorp.dumbdue.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.content.getSystemService
import com.kiwicorp.dumbdue.CHANNEL_ID_REMINDER
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.data.source.ReminderRepository
import com.kiwicorp.dumbdue.ui.MainActivity
import com.kiwicorp.dumbdue.util.HiltBroadcastReceiver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@AndroidEntryPoint
class NotificationBroadcastReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var reminderAlarmManager: ReminderAlarmManager

    @Inject
    lateinit var reminderRepository: ReminderRepository

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val notificationManager: NotificationManager = context.getSystemService()
            ?: throw Exception("Notification Manager not found.")

        val reminderId = intent.getStringExtra(REMINDER_ID) ?: return

        GlobalScope.launch {
            val reminder = reminderRepository.getReminder(reminderId)

            if (reminder != null && !reminder.isArchived) {
                val title = intent.getStringExtra(REMINDER_TITLE)!!
                val autoSnooze = intent.getLongExtra(REMINDER_AUTO_SNOOZE, 0)

                // Intent that opens app
                val openAppIntent = PendingIntent.getActivity(
                    context,
                    0,
                    Intent(context, MainActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT)

                val notification = Notification.Builder(context, CHANNEL_ID_REMINDER)
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.ic_dumbdue_logo)
                    .setContentIntent(openAppIntent)
                    .setShowWhen(true)
                    .setAutoCancel(true).build()

                val notificationId = reminderId.hashCode()

                Timber.d("Sending notification id: $notificationId title: $title")

                notificationManager.notify(notificationId,notification)
                // sets a new alarm (we manually set alarms because AlarmManager's setRepeating() will not
                // fire when device is idle)
                if (autoSnooze != Reminder.AUTO_SNOOZE_NONE) {
                    reminderAlarmManager.setAlarm(
                        title,
                        reminderId,
                        Instant.now().truncatedTo(ChronoUnit.MINUTES).toEpochMilli() + autoSnooze,
                        autoSnooze
                    )
                }
            }
        }
    }

    companion object {
        const val REMINDER_TITLE: String = "reminder_title"
        const val REMINDER_ID: String = "reminder_id"
        const val REMINDER_AUTO_SNOOZE: String = "reminder_auto_snooze"
    }
}