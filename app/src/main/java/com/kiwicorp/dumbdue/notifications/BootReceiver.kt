package com.kiwicorp.dumbdue.notifications

import android.content.Context
import android.content.Intent
import com.kiwicorp.dumbdue.data.source.ReminderRepository
import com.kiwicorp.dumbdue.util.HiltBroadcastReceiver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Receiver that listens for the BOOT_COMPLETE broadcast so reminders can be rescheduled after the
 * device is turned off (powering off device cancels all alarms)
 */
@AndroidEntryPoint
class BootReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var alarmManager: ReminderAlarmManager

    @Inject
    lateinit var reminderRepository: ReminderRepository

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Timber.d("Received")
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
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