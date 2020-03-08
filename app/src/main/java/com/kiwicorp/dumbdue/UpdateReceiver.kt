package com.kiwicorp.dumbdue

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.*
//receiver that updates the reminder lists at midnight every night
//(so that the lists updates if the user happens to have the app at midnight)
class UpdateReceiver : BroadcastReceiver() {
    private lateinit var context: Context

    override fun onReceive(context: Context, intent: Intent) {
        this.context = context
        updateList(Reminder.tomorrowList)
        updateList(Reminder.next7daysList)
        updateList(Reminder.futureList)
    }

    private fun updateList(list: LinkedList<Reminder>) {
        for (reminder in list) {
            reminder.deleteReminder()
            Reminder(reminder.text,reminder.remindCalendar,reminder.repeatVal,reminder.autoSnoozeVal,context)
        }
    }
}