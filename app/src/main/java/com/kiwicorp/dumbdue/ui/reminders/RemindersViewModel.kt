package com.kiwicorp.dumbdue.ui.reminders

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import com.kiwicorp.dumbdue.Event
import com.kiwicorp.dumbdue.SnackbarMessage
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.data.repeat.RepeatInterval
import com.kiwicorp.dumbdue.data.source.ReminderRepository
import com.kiwicorp.dumbdue.notifications.ReminderAlarmManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RemindersViewModel @ViewModelInject constructor(
    private val repository: ReminderRepository,
    private val reminderAlarmManager: ReminderAlarmManager
) : ViewModel() {
    private var searchQuery = ""

    val reminders = MediatorLiveData<List<Reminder>>().apply {
        addSource(repository.unarchivedReminders) {
            viewModelScope.launch {
                value = repository.getSearchedUnarchivedReminders(searchQuery)
            }
        }
    }

    val isEmpty: LiveData<Boolean> = Transformations.map(reminders) {
        it.isEmpty()
    }

    private var searchJob: Job? = null

    private val _eventAddReminder = MutableLiveData<Event<Unit>>()
    val eventAddReminder: LiveData<Event<Unit>> = _eventAddReminder

    private val _eventEditReminder = MutableLiveData<Event<String>>()
    val eventEditReminder: LiveData<Event<String>> = _eventEditReminder

    private val _snackbarMessage = MutableLiveData<Event<SnackbarMessage>>()
    val snackbarMessage: LiveData<Event<SnackbarMessage>> = _snackbarMessage

    fun onSearchQueryChanged(query: String?) {
        val newQuery = query ?: ""
        if (newQuery != searchQuery) {
            searchQuery = newQuery
            executeSearch()
        }
    }

    private fun executeSearch() {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(100)
            reminders.value = repository.getSearchedUnarchivedReminders(searchQuery)
        }
    }

    /**
     * Called via listener binding.
     */
    fun addReminder() {
        _eventAddReminder.value = Event(Unit)
    }

    /**
     * Called via listener binding.
     */
    fun editReminder(reminderId: String) {
        _eventEditReminder.value = Event(reminderId)
    }

    fun archive(reminder: Reminder) {
        viewModelScope.launch {
            reminder.isArchived = true
            repository.updateReminder(reminder)
            reminderAlarmManager.cancelAlarm(reminder)

            _snackbarMessage.value = Event(SnackbarMessage("Bye-Bye ${reminder.title}", Snackbar.LENGTH_LONG, "Undo") {
                undoArchive(reminder)
            })
        }
    }

    /**
     * Only used to delete reminder in [handleRequest]
     */
    fun archive(reminderId: String) {
        viewModelScope.launch {
            val reminder = repository.getReminder(reminderId)
            if (reminder != null) {
                archive(reminder)
            }
        }
    }

    fun complete(reminder: Reminder) {
        viewModelScope.launch {
            val reminderFromRepeatInterval: Reminder?

            if (reminder.repeatInterval != null) {
                repository.deleteReminder(reminder)
                reminderAlarmManager.cancelAlarm(reminder)
                // must create a copy because if this complete is undone, the reminder restored
                // will have the wrong prevOccurrence
                val repeatIntervalClone = reminder.repeatInterval!!.clone() as RepeatInterval
                val nextDueDate = repeatIntervalClone.getNextDueDate(reminder.dueDate)

                reminderFromRepeatInterval = Reminder(
                    reminder.title,
                    nextDueDate,
                    repeatIntervalClone,
                    reminder.autoSnoozeVal,
                    false
                )

                repository.insertReminder(reminderFromRepeatInterval)
                reminderAlarmManager.setAlarm(reminderFromRepeatInterval)
            } else {
                reminder.isArchived = true
                repository.updateReminder(reminder)
                reminderAlarmManager.cancelAlarm(reminder)
                reminderFromRepeatInterval = null
            }

            _snackbarMessage.value = Event(SnackbarMessage("Completed ${reminder.title} :)", Snackbar.LENGTH_LONG,"Undo") {
                undoComplete(reminder, reminderFromRepeatInterval)
            })
        }
    }

    /**
     * Only used to complete reminder in [handleRequest]
     */
    fun complete(reminderId: String) {
        viewModelScope.launch {
            val reminder = repository.getReminder(reminderId)
            if (reminder != null) {
                complete(reminder)
            }
        }
    }

    private fun undoArchive(reminder: Reminder) {
        viewModelScope.launch {
            reminder.isArchived = false
            repository.updateReminder(reminder)
            reminderAlarmManager.setAlarm(reminder)
        }
    }

    /**
     * [reminder] is the reminder that was just completed.
     * [reminderFromRepeatInterval] is the reminder that was just created  if [reminder]'s repeat
     *  interval was not null. If the repeat interval was null, [reminderFromRepeatInterval] should
     *  be null.
     */
    private fun undoComplete(reminder: Reminder, reminderFromRepeatInterval: Reminder?) {
        viewModelScope.launch {
            if (reminderFromRepeatInterval != null) {
                repository.deleteReminder(reminderFromRepeatInterval)
                reminderAlarmManager.cancelAlarm(reminder)

                repository.insertReminder(reminder)
                reminderAlarmManager.setAlarm(reminder)
            } else {
                reminder.isArchived = false
                repository.updateReminder(reminder)
                reminderAlarmManager.setAlarm(reminder)
            }
        }
    }
    
}