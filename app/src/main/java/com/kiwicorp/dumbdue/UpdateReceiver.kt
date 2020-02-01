package com.kiwicorp.dumbdue

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.*

class UpdateReceiver : BroadcastReceiver() {
    private lateinit var context: Context

    override fun onReceive(context: Context, intent: Intent) {
        this.context = context
        updateList(Reminder.tomorrowList,"Tomorrow")
        updateList(Reminder.next7daysList,"Next 7 Days")
        updateList(Reminder.futureList,"Future")
    }

    private fun updateList(list: LinkedList<Reminder>,title: String) {
        for (reminder in list) {
            val index = list.indexOf(reminder)
            list.remove(reminder)
            reminder.deleteReminder()
            MainActivity.sectionAdapter.notifyItemRemovedFromSection(title,index)
            Reminder(reminder.getText(),reminder.getRemindCalendar(),reminder.getRepeatVal(),context)
        }
    }
}