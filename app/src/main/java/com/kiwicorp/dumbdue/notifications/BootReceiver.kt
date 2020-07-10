package com.kiwicorp.dumbdue.notifications

import android.content.Context
import android.content.Intent
import com.kiwicorp.dumbdue.data.source.ReminderRepository
import dagger.android.DaggerBroadcastReceiver
import javax.inject.Inject

/**
 * Receiver that listens for the BOOT_COMPLETE broadcast so reminders can be rescheduled after the
 * device is turned off (powering off device cancels all alarms)
 */
class BootReceiver : DaggerBroadcastReceiver() {

    @Inject
    lateinit var alarmManager: ReminderAlarmManager

    @Inject
    lateinit var reminderRepository: ReminderRepository

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            val reminders = reminderRepository.reminders.value
            if (reminders != null) {
                for (reminder in reminders) {
                    alarmManager.setAlarm(reminder)
                }
            }
        }
    }

}