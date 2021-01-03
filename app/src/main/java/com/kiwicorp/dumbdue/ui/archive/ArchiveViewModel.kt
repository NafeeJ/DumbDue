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
import kotlinx.coroutines.launch

class ArchiveViewModel @ViewModelInject constructor(
    private val reminderRepository: ReminderRepository,
    private val reminderAlarmManager: ReminderAlarmManager
): ViewModel() {
    val reminders = reminderRepository.archivedReminders

    private val _snackbarMessage = MutableLiveData<Event<SnackbarMessage>>()
    val snackbarMessage: LiveData<Event<SnackbarMessage>> = _snackbarMessage

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

    private fun undoDelete(reminder: Reminder) {
        viewModelScope.launch {
            reminderRepository.insertReminder(reminder)
        }
    }
}