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
import com.kiwicorp.dumbdue.ui.archive.CheckableReminder
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

    private val _selectedReminders = MutableLiveData<Set<Reminder>>(setOf())
    val selectedReminders: LiveData<Set<Reminder>> = _selectedReminders

    val isInSelectionMode: LiveData<Boolean> = Transformations.map(selectedReminders) { it.isNotEmpty() }

    val checkableReminders: LiveData<List<CheckableReminder>> = MediatorLiveData<List<CheckableReminder>>().apply {
        addSource(selectedReminders) {
            value = getCheckableReminders(it,reminders.value ?: listOf())
        }
        addSource(reminders) {
            value = getCheckableReminders(selectedReminders.value ?: setOf(), it)
        }
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
        reminder.isArchived = true
        viewModelScope.launch {
            repository.updateReminder(reminder)
        }
        reminderAlarmManager.cancelAlarm(reminder)
    }

    fun archiveAndShowSnackbar(reminder: Reminder) {
        archive(reminder)

        _snackbarMessage.value = Event(SnackbarMessage("Bye-Bye ${reminder.title}", Snackbar.LENGTH_LONG, "Undo") {
            undoArchive(reminder)
        })
    }

    fun archiveAndShowSnackbar(reminderId: String) {
        viewModelScope.launch {
            val reminder = repository.getReminder(reminderId)
            if (reminder != null) {
                archiveAndShowSnackbar(reminder)
            }
        }
    }

    fun archiveSelectedRemindersAndShowSnackbar() {
        val selectedReminders = selectedReminders.value!!

        for (reminder in selectedReminders) {
            archive(reminder)
        }

        _snackbarMessage.value = Event(SnackbarMessage(
            "Archived ${selectedReminders.size} reminder${if (selectedReminders.size > 1) "s" else ""}",
            Snackbar.LENGTH_LONG,
            "Undo") {
            undoArchiveSelectedReminders(selectedReminders)
        })

        clearSelectedReminders()
    }

    private fun undoArchiveSelectedReminders(reminders: Set<Reminder>) {
        for(reminder in reminders) {
            undoArchive(reminder)
        }
    }

    private fun undoArchive(reminder: Reminder) {
        reminder.isArchived = false
        viewModelScope.launch {
            repository.updateReminder(reminder)
        }
        reminderAlarmManager.setAlarm(reminder)
    }

    fun completeAndShowSnackbar(reminder: Reminder) {
        val reminderFromRepeatInterval = completeReminderAndShowSnackbar(reminder)

        _snackbarMessage.value = Event(SnackbarMessage("Completed ${reminder.title} :)", Snackbar.LENGTH_LONG,"Undo") {
            undoComplete(reminder, reminderFromRepeatInterval)
        })
    }

    fun completeAndShowSnackbar(reminderId: String) {
        viewModelScope.launch {
            val reminder = repository.getReminder(reminderId)
            if (reminder != null) {
                completeReminderAndShowSnackbar(reminder)
            }
        }
    }

    /**
     * Completes the reminder
     *
     * Returns the reminder from the repeat interval, returns null if there is no repeat interval
     */
    private fun completeReminderAndShowSnackbar(reminder: Reminder): Reminder? {
        val reminderFromRepeatInterval: Reminder?

        if (reminder.repeatInterval != null) {
            viewModelScope.launch {
                repository.deleteReminder(reminder)
            }
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

            viewModelScope.launch {
                repository.insertReminder(reminderFromRepeatInterval)
            }
            reminderAlarmManager.setAlarm(reminderFromRepeatInterval)
        } else {
            reminder.isArchived = true
            viewModelScope.launch {
                repository.updateReminder(reminder)
            }
            reminderAlarmManager.cancelAlarm(reminder)
            reminderFromRepeatInterval = null
        }

        return reminderFromRepeatInterval
    }

    fun completeSelectedRemindersAndShowSnackBar() {
        // For undoing this. First is reminder, second is reminder from repeat interval.
        val remindersWithRemindersFromRepeatInterval = mutableListOf<Pair<Reminder, Reminder?>>()

        val selectedReminders = selectedReminders.value!!

        for (reminder in selectedReminders) {
            val reminderFromRepeatInterval = completeReminderAndShowSnackbar(reminder)
            remindersWithRemindersFromRepeatInterval.add(Pair(reminder, reminderFromRepeatInterval))
        }

        _snackbarMessage.value = Event(SnackbarMessage(
            "Completed ${selectedReminders.size} reminder${if (selectedReminders.size > 1) "s" else ""} :D",
            Snackbar.LENGTH_LONG,
            "Undo") {
            undoCompleteSelectedReminders(remindersWithRemindersFromRepeatInterval)
        })

        clearSelectedReminders()
    }

    private fun undoCompleteSelectedReminders(remindersWithRemindersFromRepeatInterval : List<Pair<Reminder, Reminder?>>) {
        for (pair in remindersWithRemindersFromRepeatInterval) {
            undoComplete(pair.first, pair.second)
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

    private fun getCheckableReminders(selectedReminders: Set<Reminder>, reminders: List<Reminder>): List<CheckableReminder> {
        return MutableList(reminders.size) {
            val reminder = reminders[it]
            CheckableReminder(reminder, selectedReminders.contains(reminder))
        }
    }

    fun select(reminder: Reminder) {
        _selectedReminders.value = selectedReminders.value?.plus(reminder) ?: setOf(reminder)
    }

    fun deselect(reminder: Reminder) {
        _selectedReminders.value = selectedReminders.value?.minus(reminder) ?: setOf()
    }

    fun clearSelectedReminders() {
        _selectedReminders.value = setOf()
    }
    
}