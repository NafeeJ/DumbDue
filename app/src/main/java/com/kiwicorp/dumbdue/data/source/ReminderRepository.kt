package com.kiwicorp.dumbdue.data.source

import androidx.lifecycle.LiveData
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.data.source.local.ReminderDao
import com.kiwicorp.dumbdue.data.source.local.ReminderDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReminderRepository private constructor(private val reminderDao: ReminderDao) {
    val reminders: LiveData<List<Reminder>> = reminderDao.observeReminders()

    fun getReminder(reminderId: String) = reminderDao.observeTaskById(reminderId)

    suspend fun insertReminder(reminder: Reminder) {
        reminderDao.insertReminder(reminder)
    }

    suspend fun updateReminder(reminder: Reminder) {
        reminderDao.updateReminder(reminder)
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