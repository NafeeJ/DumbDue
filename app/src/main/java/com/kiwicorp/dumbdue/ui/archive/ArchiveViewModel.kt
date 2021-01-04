package com.kiwicorp.dumbdue.ui.archive

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.kiwicorp.dumbdue.Event
import com.kiwicorp.dumbdue.SnackbarMessage
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.data.source.ReminderRepository
import com.kiwicorp.dumbdue.notifications.ReminderAlarmManager
import com.kiwicorp.dumbdue.ui.REQUEST_ARCHIVE
import com.kiwicorp.dumbdue.ui.REQUEST_COMPLETE
import com.kiwicorp.dumbdue.ui.REQUEST_DELETE
import com.kiwicorp.dumbdue.ui.REQUEST_UNARCHIVE
import kotlinx.coroutines.launch

class ArchiveViewModel @ViewModelInject constructor(
    private val reminderRepository: ReminderRepository,
    private val reminderAlarmManager: ReminderAlarmManager
): ViewModel() {
    val reminders = reminderRepository.archivedReminders

    private val _navigateToEditReminderFragment = MutableLiveData<Event<String>>()
    val navigateToEditReminderFragment: LiveData<Event<String>> = _navigateToEditReminderFragment

    private val _snackbarMessage = MutableLiveData<Event<SnackbarMessage>>()
    val snackbarMessage: LiveData<Event<SnackbarMessage>> = _snackbarMessage

    private var argsRequestHandled = false

    fun unarchive(reminder: Reminder) {
        viewModelScope.launch {
            reminder.isArchived = false
            reminderRepository.updateReminder(reminder)
            reminderAlarmManager.setAlarm(reminder)

            _snackbarMessage.value = Event(SnackbarMessage("${reminder.title} is back", Snackbar.LENGTH_LONG,"Undo") {
                undoUnarchive(reminder)
            })
        }
    }

    fun unarchive(reminderId: String) {
        viewModelScope.launch {
            reminderRepository.getReminder(reminderId)?.let { unarchive(it) }
        }
    }

    private fun undoUnarchive(reminder: Reminder) {
        viewModelScope.launch {
            reminder.isArchived = true
            reminderRepository.updateReminder(reminder)
            reminderAlarmManager.cancelAlarm(reminder)
        }
    }

    fun delete(reminder: Reminder) {
        viewModelScope.launch {
            reminderRepository.deleteReminder(reminder)

            _snackbarMessage.value = Event(SnackbarMessage("Bye-Bye forever ${reminder.title}", Snackbar.LENGTH_LONG, "Undo") {
                undoDelete(reminder)
            })
        }
    }

    fun delete(reminderId: String) {
        viewModelScope.launch {
            reminderRepository.getReminder(reminderId)?.let { delete(it) }
        }
    }

    fun handleRequest(request: Int, reminderId: String) {
        if (argsRequestHandled) return
        when (request) {
            REQUEST_UNARCHIVE -> unarchive(reminderId)
            REQUEST_DELETE -> delete(reminderId)
        }
        argsRequestHandled = true
    }

    private fun undoDelete(reminder: Reminder) {
        viewModelScope.launch {
            reminderRepository.insertReminder(reminder)
        }
    }

    fun navigateToEditReminderFragment(reminder: Reminder) {
        _navigateToEditReminderFragment.value = Event(reminder.id)
    }
}