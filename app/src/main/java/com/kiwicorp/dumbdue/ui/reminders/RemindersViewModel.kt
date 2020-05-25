package com.kiwicorp.dumbdue.ui.reminders

import androidx.lifecycle.*
import com.kiwicorp.dumbdue.NavEvent
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.data.source.ReminderRepository

class RemindersViewModel internal constructor(reminderRepository: ReminderRepository) : ViewModel() {


    val reminders: LiveData<List<Reminder>> = reminderRepository.reminders

    val test: LiveData<String> = Transformations.map(reminders) { reminders ->
        if (reminders.isEmpty()) {
            return@map "nothing here yet"
        } else {
            reminders[0].title
        }
    }

    private val _eventAddReminder = MutableLiveData<NavEvent<Unit>>()
    val eventAddReminder: LiveData<NavEvent<Unit>> = _eventAddReminder
    /**
     * Called via listener binding.
     */
    fun onAddReminder() {
        _eventAddReminder.value = NavEvent(Unit)
    }
}
