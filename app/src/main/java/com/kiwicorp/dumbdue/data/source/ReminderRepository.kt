package com.kiwicorp.dumbdue.data.source

import androidx.lifecycle.LiveData
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.data.source.local.ReminderDao
import com.kiwicorp.dumbdue.notifications.ReminderAlarmManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepository @Inject constructor(
    private val reminderDao: ReminderDao) {

    val reminders: LiveData<List<Reminder>> = reminderDao.observeReminders()

    suspend fun getReminders(): List<Reminder> {
        return withContext(Dispatchers.IO) {
            reminderDao.getReminders()
        }
    }

    val unarchivedReminders: LiveData<List<Reminder>> = reminderDao.observeUnarchivedReminders()

    suspend fun getUnarchivedReminders(): List<Reminder> {
        return withContext(Dispatchers.IO) {
            reminderDao.getUnarchivedReminders()
        }
    }

    val archivedReminders: LiveData<List<Reminder>> = reminderDao.observeArchivedReminders()

    suspend fun getArchivedReminders(): List<Reminder> {
        return withContext(Dispatchers.IO) {
            reminderDao.getArchivedReminders()
        }
    }

    suspend fun getReminder(reminderId: String): Reminder? {
        return withContext(Dispatchers.IO ) {
            reminderDao.getReminder(reminderId)
        }
    }

    suspend fun insertReminder(reminder: Reminder) {
        withContext(Dispatchers.IO) {
            reminderDao.insertReminder(reminder)
        }
    }

    suspend fun updateReminder(reminder: Reminder): Int {
        return withContext(Dispatchers.IO) {
            reminderDao.updateReminder(reminder)
        }
    }

    suspend fun deleteReminder(reminder: Reminder): Int {
        return withContext(Dispatchers.IO) {
            reminderDao.deleteReminder(reminder)
        }
    }

}