package com.kiwicorp.dumbdue.ui.reminders

import androidx.lifecycle.*
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.data.source.ReminderRepository
import kotlinx.coroutines.*

class RemindersViewModel internal constructor(
    private val reminderRepository: ReminderRepository) : ViewModel() {

    //viewModelJob allows us to cancel all coroutines started by this ViewModel
    private var viewModelJob = Job()

    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val reminders: LiveData<List<Reminder>> = reminderRepository.reminders

    val test: LiveData<String> = Transformations.map(reminders) { reminders ->
        if (reminders.isEmpty()) {
            return@map "nothing here yet"
        } else {
            reminders[0].title
        }
    }

    private suspend fun insert(reminder: Reminder) {
        withContext(Dispatchers.IO) {
            reminderRepository.insertReminder(reminder)
        }
    }

    private suspend fun update(reminder: Reminder) {
        withContext(Dispatchers.IO) {
            reminderRepository.updateReminder(reminder)
        }
    }

    private suspend fun delete(reminder: Reminder) {
        withContext(Dispatchers.IO) {
            reminderRepository.deleteReminder(reminder)
        }
    }

    private suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            reminderRepository.deleteReminders()
        }
    }

    fun onFabClicked() {
        viewModelScope.launch {
            reminderRepository.deleteReminders()
            val titles = listOf("BUTT","MUNCH","APPLE","CANADA","STEVE")
            reminderRepository.insertReminder(Reminder(title = titles.random()))
        }
    }
}
