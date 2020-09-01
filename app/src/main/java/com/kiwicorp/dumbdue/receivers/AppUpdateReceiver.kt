package com.kiwicorp.dumbdue.receivers

import android.content.Context
import android.content.Intent
import com.kiwicorp.dumbdue.data.source.ReminderRepository
import com.kiwicorp.dumbdue.notifications.ReminderAlarmManager
import com.kiwicorp.dumbdue.util.HiltBroadcastReceiver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Receiver that listens for the app updates so reminders can be rescheduled (updating app cancels
 * all alarms).
 */
@AndroidEntryPoint
class AppUpdateReceiver : HiltBroadcastReceiver() {
    @Inject
    lateinit var alarmManager: ReminderAlarmManager

    @Inject
    lateinit var reminderRepository: ReminderRepository

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Timber.d("Received")
        if (intent.action == "android.intent.action.MY_PACKAGE_REPLACED") {
            Timber.d("Correct action received")
            GlobalScope.launch {
                val reminders = reminderRepository.getReminders()
                if (reminders != null) {
                    Timber.d("Reminders are not null, settings alarms")
                    for (reminder in reminders) {
                        alarmManager.setAlarm(reminder)
                    }
                } else {
                    Timber.d("Reminders are null, fuck")
                }
            }
        }
    }
}