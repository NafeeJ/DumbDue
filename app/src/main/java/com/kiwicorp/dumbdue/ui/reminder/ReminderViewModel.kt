package com.kiwicorp.dumbdue.ui.reminder

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ReminderViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ReminderRepository = ReminderRepository(application)
    private val allReminders: LiveData<List<Reminder>> = repository.getAllReminders()

    fun insert(reminder: Reminder) {
        repository.insert(reminder)
    }

    fun update(reminder: Reminder) {
        repository.update(reminder)
    }

    fun delete(reminder: Reminder) {
        repository.delete(reminder)
    }

    fun deleteAllNotes() {
        repository.deleteAllReminders()
    }

    fun getReminders(timeGroup: TimeGroup): LiveData<List<Reminder>> {
        return Transformations.switchMap(allReminders) {
            filterReminders(it,timeGroup)
        }
    }

    private fun filterReminders(reminders: List<Reminder>, timeGroup: TimeGroup): LiveData<List<Reminder>> {
        val result = MutableLiveData<List<Reminder>>()
        viewModelScope.launch {
            filterItems(reminders,timeGroup)
        }
        return result
    }

    private fun filterItems(reminders: List<Reminder>, timeGroup: TimeGroup): List<Reminder> {
        val remindersToShow = ArrayList<Reminder>()

        val calendar = Calendar.getInstance()
        val nowInMillis = calendar.timeInMillis
        calendar.set(Calendar.MILLISECOND, 59)
        calendar.set(Calendar.SECOND,59)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.HOUR_OF_DAY,23)
        val endOfTodayInMillis = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR,1)
        val endOfTomorrowInMillis = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR,6)
        val endOfNext7DaysInMillis = calendar.timeInMillis

        for (reminder in reminders) {
            when (timeGroup) {
                TimeGroup.OVERDUE -> if (reminder.timeInMillis <= nowInMillis) remindersToShow.add(reminder)
                TimeGroup.TODAY -> if (reminder.timeInMillis in nowInMillis+1..endOfTodayInMillis) remindersToShow.add(reminder)
                TimeGroup.TOMORROW -> if (reminder.timeInMillis in endOfTodayInMillis+1..endOfTomorrowInMillis) remindersToShow.add(reminder)
                TimeGroup.NEXT_7_DAYS -> if (reminder.timeInMillis in endOfTomorrowInMillis+1..endOfNext7DaysInMillis) remindersToShow.add(reminder)
                TimeGroup.FUTURE -> if (reminder.timeInMillis >= endOfNext7DaysInMillis) remindersToShow.add(reminder)
            }
        }

        return remindersToShow
    }

    fun getAllReminders(): LiveData<List<Reminder>> {
        return allReminders
    }

}