package com.kiwicorp.dumbdue.ui.reminders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kiwicorp.dumbdue.NavEvent
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.data.source.ReminderRepository

class RemindersViewModel internal constructor(reminderRepository: ReminderRepository) : ViewModel() {


    val reminders: LiveData<List<Reminder>> = reminderRepository.reminders

    private val _eventAddReminder = MutableLiveData<NavEvent<Unit>>()
    val eventAddReminder: LiveData<NavEvent<Unit>> = _eventAddReminder

    private val _eventEditReminder = MutableLiveData<NavEvent<String>>()
    val eventEditReminder: LiveData<NavEvent<String>> = _eventEditReminder
    /**
     * Called via listener binding.
     */
    fun onAddReminder() {
        _eventAddReminder.value = NavEvent(Unit)
    }

    fun onEditReminder(id: String) {
        _eventEditReminder.value = NavEvent(id)
    }
}
