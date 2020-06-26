package com.kiwicorp.dumbdue.data.source

import androidx.lifecycle.LiveData
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.data.source.local.ReminderDao
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepository @Inject constructor(private val reminderDao: ReminderDao) {
    val reminders: LiveData<List<Reminder>> = reminderDao.observeReminders()

    fun observeReminder(reminderId: String) = reminderDao.observeReminderById(reminderId)

    fun getReminder(reminderId: String) = reminderDao.getReminder(reminderId)

    suspend fun insertReminder(reminder: Reminder) {
        reminderDao.insertReminder(reminder)
    }

    suspend fun updateReminder(reminder: Reminder): Int {
        return reminderDao.updateReminder(reminder)
    }

    suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.deleteReminder(reminder)
    }

    suspend fun deleteReminders() {
        reminderDao.deleteReminders()
    }

    /**
     * Returns a the newly created reminder if the reminder is repeating. Returns null otherwise.
     * (This is so this action can be undone).
     */
    suspend fun completeReminder(reminder: Reminder): Reminder? {
        with(reminder) {
            reminderDao.deleteReminder(this)
            repeatInterval?.let {
                return Reminder(title,it.getNextDueDate(calendar),it,autoSnoozeVal,id)
            }
        }
        return null
    }

}