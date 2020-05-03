package com.kiwicorp.dumbdue.ui.reminder

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData

class ReminderRepository(application: Application) {
    private var reminderDao: ReminderDao
    private var reminders: LiveData<List<Reminder>>

    init {
        val database: ReminderDatabase = ReminderDatabase.getDatabase(application)
        reminderDao = database.reminderDao()
        reminders = reminderDao.getAllReminders()
    }

    fun insert(reminder: Reminder) {
        reminderDao.insert(reminder)
    }

    fun update(reminder: Reminder) {
        reminderDao.update(reminder)
    }

    fun delete(reminder: Reminder) {
        reminderDao.delete(reminder)
    }

    fun deleteAllReminders() {
        reminderDao.deleteAllReminders()
    }

    fun getAllReminders(): LiveData<List<Reminder>> {
        return reminders
    }

}