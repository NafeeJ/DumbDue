package com.kiwicorp.dumbdue.ui.reminders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.kiwicorp.dumbdue.Event
import com.kiwicorp.dumbdue.REQUEST_COMPLETE
import com.kiwicorp.dumbdue.REQUEST_DELETE
import com.kiwicorp.dumbdue.SnackbarMessage
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.data.source.ReminderRepository
import com.kiwicorp.dumbdue.notifications.ReminderAlarmManager
import kotlinx.coroutines.*
import javax.inject.Inject

class RemindersViewModel @Inject constructor(
    private val repository: ReminderRepository,
    private val reminderAlarmManager: ReminderAlarmManager
) : ViewModel() {
    val reminders: LiveData<List<Reminder>> = repository.reminders

    private val _eventAddReminder = MutableLiveData<Event<Unit>>()
    val eventAddReminder: LiveData<Event<Unit>> = _eventAddReminder

    private val _eventEditReminder = MutableLiveData<Event<String>>()
    val eventEditReminder: LiveData<Event<String>> = _eventEditReminder

    private val _snackbarMessage = MutableLiveData<Event<SnackbarMessage>>()
    val snackbarMessage: LiveData<Event<SnackbarMessage>> = _snackbarMessage

    private var argsRequestHandled = false
    /**
     * Called via listener binding.
     */
    fun onAddReminder() {
        _eventAddReminder.value = Event(Unit)
    }

    /**
     * Called via listener binding.
     */

    fun onEditReminder(id: String) {
        _eventEditReminder.value = Event(id)
    }

    fun onDeleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            delete(reminder)
            reminderAlarmManager.cancelAlarm(reminder)
            _snackbarMessage.value = Event(SnackbarMessage("Bye-Bye ${reminder.title}", Snackbar.LENGTH_LONG, "Undo") {
                undoDelete(reminder)
            })
        }
    }

    /**
     * Only used to delete reminder in [handleRequest]
     */
    private fun onDeleteReminder(reminderId: String) {
        viewModelScope.launch {
            val reminder = getReminder(reminderId)
            if (reminder != null) {
                onDeleteReminder(reminder)
            }
        }
    }

    fun onCompleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            val newReminder = complete(reminder)
            _snackbarMessage.value = Event(SnackbarMessage("Completed ${reminder.title} :)", Snackbar.LENGTH_LONG,"Undo") {
                undoComplete(reminder, newReminder)
            })
        }
    }

    /**
     * Only used to complete reminder in [handleRequest]
     */
    private fun onCompleteReminder(reminderId: String) {
        viewModelScope.launch {
            val reminder = getReminder(reminderId)
            if (reminder != null) {
                onCompleteReminder(reminder)
            }
        }
    }

    /**
     * Reinserts the deleted reminder
     */
    private fun undoDelete(reminder: Reminder) {
        viewModelScope.launch {
            insert(reminder)
            reminderAlarmManager.setAlarm(reminder)
        }
    }

    /**
     * [reminder] is the reminder that was just completed.
     * [newReminder] is the reminder that was just created by the repository if [reminder]'s repeat
     * val was not [Reminder.REPEAT_NONE]. If it was [Reminder.REPEAT_NONE], [newReminder] should be
     * null.
     */
    private fun undoComplete(reminder: Reminder, newReminder: Reminder?) {
        viewModelScope.launch {
            if (newReminder != null) {
                delete(newReminder)
            }
            insert(reminder)
        }
    }

    /**
     * Deletes the given reminder in the repository
     */
    private suspend fun delete(reminder: Reminder) {
        reminderAlarmManager.cancelAlarm(reminder)
        withContext(Dispatchers.IO) {
            repository.deleteReminder(reminder)
        }
    }

    /**
     * Handles the request provided by [RemindersFragmentArgs]
     */
    fun handleRequest(request: Int, reminderId: String) {
        if (argsRequestHandled) return
        when (request) {
            REQUEST_COMPLETE -> onCompleteReminder(reminderId)
            REQUEST_DELETE -> onDeleteReminder(reminderId)
        }
        argsRequestHandled = true
    }

    /**
     * Completes the reminder in the repository
     */
    private suspend fun complete(reminder: Reminder): Reminder? {
        reminderAlarmManager.cancelAlarm(reminder)
        return withContext(Dispatchers.IO) {
            repository.completeReminder(reminder)
        }
    }

    private suspend fun getReminder(reminderId: String): Reminder? {
        return withContext(Dispatchers.IO) {
            repository.getReminder(reminderId)
        }
    }

    /**
     * Inserts the given reminder into the repository
     */
    private suspend fun insert(reminder: Reminder) {
        reminderAlarmManager.setAlarm(reminder)
        withContext(Dispatchers.IO) {
            repository.insertReminder(reminder)
        }
    }
}
