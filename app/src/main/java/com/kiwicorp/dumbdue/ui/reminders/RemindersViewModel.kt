package com.kiwicorp.dumbdue.ui.reminders

import androidx.lifecycle.*
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

    private val _eventAddReminder = MutableLiveData<Boolean>()
    val eventAddReminder: LiveData<Boolean>
        get() = _eventAddReminder

    /**
     * Called from data binding.
     */
    fun onAddReminder() {
        _eventAddReminder.value = true
    }

    fun onAddReminderComplete() {
        _eventAddReminder.value = null
    }

}
