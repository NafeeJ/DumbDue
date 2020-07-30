package com.kiwicorp.dumbdue.notifications

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.data.source.ReminderRepository
import dagger.android.DaggerBroadcastReceiver
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
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