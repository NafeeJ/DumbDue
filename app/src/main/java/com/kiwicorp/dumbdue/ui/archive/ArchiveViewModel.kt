package com.kiwicorp.dumbdue.ui.archive

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import com.kiwicorp.dumbdue.Event
import com.kiwicorp.dumbdue.SnackbarMessage
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.data.source.ReminderRepository
import com.kiwicorp.dumbdue.notifications.ReminderAlarmManager
import com.kiwicorp.dumbdue.ui.reminders.ReminderRequest
import com.kiwicorp.dumbdue.ui.reminders.ReminderRequest.Companion.REQUEST_DELETE
import com.kiwicorp.dumbdue.ui.reminders.ReminderRequest.Companion.REQUEST_UNARCHIVE
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

    private val _selectedReminders = MutableLiveData<Set<Reminder>>(setOf())
    val selectedReminders: LiveData<Set<Reminder>> = _selectedReminders

    val checkableReminders = MediatorLiveData<List<CheckableReminder>>().apply {
        addSource(selectedReminders) {
            value = getCheckableReminders(it,reminders.value ?: listOf())
        }
        addSource(reminders) {
            value = getCheckableReminders(selectedReminders.value ?: setOf(),it)
        }
    }

    val isInSelectionMode: LiveData<Boolean> = Transformations.map(selectedReminders) { it.isNotEmpty() }

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

    private fun undoDelete(reminder: Reminder) {
        viewModelScope.launch {
            reminderRepository.insertReminder(reminder)
        }
    }

    fun navigateToEditReminderFragment(reminder: Reminder) {
        _navigateToEditReminderFragment.value = Event(reminder.id)
    }

    private fun getCheckableReminders(selectedReminders: Set<Reminder>, reminders: List<Reminder>): List<CheckableReminder> {
        return MutableList(reminders.size) {
            val reminder = reminders[it]
            CheckableReminder(reminder, selectedReminders.contains(reminder))
        }
    }

    fun select(reminder: Reminder) {
        _selectedReminders.value = selectedReminders.value?.plus(reminder) ?: setOf(reminder)
    }

    fun unselect(reminder: Reminder) {
        _selectedReminders.value = selectedReminders.value?.minus(reminder) ?: setOf()
    }

    fun clearSelectedReminders() {
        _selectedReminders.value = setOf()
    }

    fun deleteSelectedReminders() {
        viewModelScope.launch {
            val currSelectedReminders = selectedReminders.value!!

            for (reminder in currSelectedReminders) {
                reminderRepository.deleteReminder(reminder)
            }

            clearSelectedReminders()

            _snackbarMessage.value = Event(SnackbarMessage("They're gone.", Snackbar.LENGTH_LONG,"Undo") {
                undoDeleteSelectedReminders(currSelectedReminders)
            })
        }
    }

    private fun undoDeleteSelectedReminders(selectedReminders: Set<Reminder>) {
        viewModelScope.launch {
            for (reminder in selectedReminders) {
                reminderRepository.insertReminder(reminder)
            }
        }
    }

    fun unarchiveSelectedReminders() {
        viewModelScope.launch {
            val currSelectedReminders = selectedReminders.value!!

            for (reminder in currSelectedReminders) {
                reminder.isArchived = false
                reminderRepository.updateReminder(reminder)
                reminderAlarmManager.setAlarm(reminder)
            }
            clearSelectedReminders()

            _snackbarMessage.value = Event(SnackbarMessage("They've returned.", Snackbar.LENGTH_LONG,"Undo") {
                undoUnarchiveSelectedReminders(currSelectedReminders)
            })
        }
    }

    private fun undoUnarchiveSelectedReminders(selectedReminders: Set<Reminder>) {
        viewModelScope.launch {
            for (reminder in selectedReminders) {
                reminder.isArchived = true
                reminderRepository.updateReminder(reminder)
                reminderAlarmManager.cancelAlarm(reminder)
            }
        }
    }
}