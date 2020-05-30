package com.kiwicorp.dumbdue.data.source

import android.view.View
import androidx.lifecycle.LiveData
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.data.source.local.ReminderDao
import java.util.*

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

    /**
     * Returns a the newly created reminder if the reminder is repeating. Returns null otherwise.
     * (This is so this action can be undone).
     */
    suspend fun completeReminder(reminder: Reminder): Reminder? {
        reminderDao.deleteReminder(reminder)
        if (reminder.repeatVal != Reminder.REPEAT_NONE) {
            val newReminder = Reminder(
                title = reminder.title,
                calendar = Calendar.getInstance().apply { timeInMillis = reminder.calendar.timeInMillis },
                repeatVal = reminder.repeatVal,
                autoSnoozeVal = reminder.autoSnoozeVal)

            when (reminder.repeatVal) {
                Reminder.REPEAT_DAILY -> newReminder.calendar.add(Calendar.DAY_OF_YEAR,1)
                Reminder.REPEAT_WEEKDAYS -> {
                    when (newReminder.calendar.get(Calendar.DAY_OF_WEEK)) {
                        Calendar.FRIDAY -> newReminder.calendar.add(Calendar.DAY_OF_YEAR,3)
                        Calendar.SATURDAY -> newReminder.calendar.add(Calendar.DAY_OF_YEAR,2)
                        else -> newReminder.calendar.add(Calendar.DAY_OF_YEAR,1)
                    }
                }
                Reminder.REPEAT_WEEKLY -> newReminder.calendar.add(Calendar.WEEK_OF_YEAR,1)
                Reminder.REPEAT_MONTHLY -> newReminder.calendar.add(Calendar.MONTH, 1)
                Reminder.REPEAT_YEARLY -> newReminder.calendar.add(Calendar.YEAR,1)
                Reminder.REPEAT_CUSTOM -> return null//todo
                else -> throw IllegalArgumentException("Unknown Repeat Val")
            }

            reminderDao.insertReminder(newReminder)
            return newReminder
        }
        return null
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