package com.kiwicorp.dumbdue.ui.reminders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.kiwicorp.dumbdue.Event
import com.kiwicorp.dumbdue.SnackbarMessage
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.data.source.ReminderRepository
import kotlinx.coroutines.*

class RemindersViewModel internal constructor(private val repository: ReminderRepository) : ViewModel() {

    /**
     * viewModelJob allows us to cancel all coroutines started by this ViewModel
     */
    private var viewModelJob = Job()

    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val reminders: LiveData<List<Reminder>> = repository.reminders

    private val _eventAddReminder = MutableLiveData<Event<Unit>>()
    val eventAddReminder: LiveData<Event<Unit>> = _eventAddReminder

    private val _eventEditReminder = MutableLiveData<Event<String>>()
    val eventEditReminder: LiveData<Event<String>> = _eventEditReminder

    private val _snackbarData = MutableLiveData<Event<SnackbarMessage>>()
    val snackbarMessage: LiveData<Event<SnackbarMessage>> = _snackbarData
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
        uiScope.launch {
            delete(reminder)
            _snackbarData.value = Event(SnackbarMessage("Bye-Bye ${reminder.title}", Snackbar.LENGTH_LONG, "Undo") {
                undoDelete(reminder)
            })
        }
    }

    /**
     * Deletes the given reminder in the repository
     */
    private suspend fun delete(reminder: Reminder) {
        withContext(Dispatchers.IO) {
            repository.deleteReminder(reminder)
        }
    }

    /**
     * Reinserts the deleted reminder
     */
    private fun undoDelete(reminder: Reminder) {
        uiScope.launch {
            insert(reminder)
        }
    }

    fun onCompleteReminder(reminder: Reminder) {
        uiScope.launch {
            val newReminder = complete(reminder)
            _snackbarData.value = Event(SnackbarMessage("Completed ${reminder.title} :)", Snackbar.LENGTH_LONG,"Undo") {
                undoComplete(reminder, newReminder)
            })
        }
    }

    /**
     * Completes the reminder in the repository
     */
    private suspend fun complete(reminder: Reminder): Reminder? {
        return withContext(Dispatchers.IO) {
            repository.completeReminder(reminder)
        }
    }

    /**
     * [reminder] is the reminder that was just completed.
     * [newReminder] is the reminder that was just created by the repository if [reminder]'s repeat
     * val was not [Reminder.REPEAT_NONE]. If it was [Reminder.REPEAT_NONE], [newReminder] should be
     * null.
     */
    private fun undoComplete(reminder: Reminder, newReminder: Reminder?) {
        uiScope.launch {
            if (newReminder != null) {
                delete(newReminder)
            }
            insert(reminder)
        }
    }

    /**
     * Inserts the given reminder into the repository
     */
    private suspend fun insert(reminder: Reminder) {
        withContext(Dispatchers.IO) {
            repository.insertReminder(reminder)
        }
    }
}
