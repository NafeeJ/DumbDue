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
    private val reminderDao: ReminderDao,
    private val alarmManger: ReminderAlarmManager) {

    val reminders: LiveData<List<Reminder>> = reminderDao.observeReminders()

    suspend fun getReminder(reminderId: String): Reminder? {
        return withContext(Dispatchers.IO ) {
            reminderDao.getReminder(reminderId)
        }
    }

    suspend fun insertReminder(reminder: Reminder) {
        alarmManger.setAlarm(reminder)
        withContext(Dispatchers.IO) {
            reminderDao.insertReminder(reminder)
        }
    }

    suspend fun updateReminder(reminder: Reminder): Int {
        alarmManger.updateAlarm(reminder)
        return withContext(Dispatchers.IO) {
            reminderDao.updateReminder(reminder)
        }
    }

    suspend fun deleteReminder(reminder: Reminder): Int {
        alarmManger.cancelAlarm(reminder)
        return withContext(Dispatchers.IO) {
            reminderDao.deleteReminder(reminder)
        }
    }

}