package com.kiwicorp.dumbdue.ui.reminders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kiwicorp.dumbdue.NavEvent
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.data.source.ReminderRepository
import kotlinx.coroutines.*

class RemindersViewModel internal constructor(private val reminderRepository: ReminderRepository) : ViewModel() {

    /**
     * viewModelJob allows us to cancel all coroutines started by this ViewModel
     */
    private var viewModelJob = Job()

    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

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

    fun onDeleteReminder(reminder: Reminder) {
        uiScope.launch {
            delete(reminder)
        }
    }

    /**
     * Deletes the given reminder in the repository
     */
    private suspend fun delete(reminder: Reminder) {
        withContext(Dispatchers.IO) {
            reminderRepository.deleteReminder(reminder)
        }
    }

    fun onCompleteReminder(reminder: Reminder) {
        uiScope.launch {
            complete(reminder)
        }
    }

    private suspend fun complete(reminder: Reminder) {
        withContext(Dispatchers.IO) {
            reminderRepository.completeReminder(reminder)
        }
    }
}
