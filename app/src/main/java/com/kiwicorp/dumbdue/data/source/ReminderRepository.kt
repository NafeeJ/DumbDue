package com.kiwicorp.dumbdue.data.source

import androidx.lifecycle.LiveData
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.data.source.local.ReminderDao

class ReminderRepository private constructor(private val reminderDao: ReminderDao) {
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

    companion object {
        //For singleton purposes
        @Volatile private var instance: ReminderRepository? = null

        fun getInstance(reminderDao: ReminderDao) =
            instance ?: synchronized(this) {
                instance ?: ReminderRepository(reminderDao).also { instance = it }
            }
    }
}