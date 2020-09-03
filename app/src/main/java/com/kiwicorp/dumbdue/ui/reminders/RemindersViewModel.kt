package com.kiwicorp.dumbdue.ui.reminders

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import com.kiwicorp.dumbdue.Event
import com.kiwicorp.dumbdue.SnackbarMessage
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.data.repeat.RepeatInterval
import com.kiwicorp.dumbdue.data.source.ReminderRepository
import com.kiwicorp.dumbdue.ui.REQUEST_COMPLETE
import com.kiwicorp.dumbdue.ui.REQUEST_DELETE
import kotlinx.coroutines.launch

class RemindersViewModel @ViewModelInject constructor(
    private val repository: ReminderRepository
) : ViewModel() {
    val reminders: LiveData<List<Reminder>> = repository.reminders

    val isEmpty: LiveData<Boolean> = Transformations.map(reminders) {
        it.isEmpty()
    }

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
    fun addReminder() {
        _eventAddReminder.value = Event(Unit)
    }

    /**
     * Called via listener binding.
     */
    fun editReminder(reminderId: String) {
        _eventEditReminder.value = Event(reminderId)
    }

    fun delete(reminder: Reminder) {
        viewModelScope.launch {
            repository.deleteReminder(reminder)
            _snackbarMessage.value = Event(SnackbarMessage("Bye-Bye ${reminder.title}", Snackbar.LENGTH_LONG, "Undo") {
                undoDelete(reminder)
            })
        }
    }

    /**
     * Only used to delete reminder in [handleRequest]
     */
    private fun delete(reminderId: String) {
        viewModelScope.launch {
            val reminder = repository.getReminder(reminderId)
            if (reminder != null) {
                delete(reminder)
            }
        }
    }

    fun complete(reminder: Reminder) {
        viewModelScope.launch {
            repository.deleteReminder(reminder)

            val reminderFromRepeatInterval = if (reminder.repeatInterval != null) {
                // creates a repeat interval clone because if the new reminder shares the same
                // instance of RepeatInterval as the old reminder, when complete is undone, the
                // old reminder will have the wrong prevOccurrence.
                val repeatIntervalClone = reminder.repeatInterval!!.clone() as RepeatInterval
                val nextDueDate = repeatIntervalClone.getNextDueDate(reminder.dueDate)
                Reminder(reminder.title, nextDueDate,repeatIntervalClone,reminder.autoSnoozeVal)
            } else {
                null
            }

            if (reminderFromRepeatInterval != null) {
                repository.insertReminder(reminderFromRepeatInterval)
            }

            _snackbarMessage.value = Event(SnackbarMessage("Completed ${reminder.title} :)", Snackbar.LENGTH_LONG,"Undo") {
                undoComplete(reminder, reminderFromRepeatInterval)
            })
        }
    }

    /**
     * Only used to complete reminder in [handleRequest]
     */
    private fun complete(reminderId: String) {
        viewModelScope.launch {
            val reminder = repository.getReminder(reminderId)
            if (reminder != null) {
                complete(reminder)
            }
        }
    }

    private fun undoDelete(reminder: Reminder) {
        viewModelScope.launch {
            repository.insertReminder(reminder)
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
            }
            repository.insertReminder(reminder)
        }
    }

    /**
     * Handles the request provided by [RemindersFragmentArgs]
     */
    fun handleRequest(request: Int, reminderId: String) {
        if (argsRequestHandled) return
        when (request) {
            REQUEST_COMPLETE -> complete(reminderId)
            REQUEST_DELETE -> delete(reminderId)
        }
        argsRequestHandled = true
    }
    
}