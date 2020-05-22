package com.kiwicorp.dumbdue.ui.reminders

import androidx.lifecycle.*
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.data.source.ReminderRepository
import kotlinx.coroutines.*

class RemindersViewModel internal constructor(reminderRepository: ReminderRepository) : ViewModel() {


    val reminders: LiveData<List<Reminder>> = reminderRepository.reminders

    val test: LiveData<String> = Transformations.map(reminders) { reminders ->
        if (reminders.isEmpty()) {
            return@map "nothing here yet"
        } else {
            reminders[0].title
        }
    }

    fun onFabClicked() {
        //navigate to add fragment when FAB is clicked
        _onNavigateToAddFragment.value = true
    }

    private val _onNavigateToAddFragment = MutableLiveData<Boolean>()
    val onNavigateToAddFragment: LiveData<Boolean>
        get() = _onNavigateToAddFragment

    fun finishedNavigatingToAddFragment() {
        _onNavigateToAddFragment.value = null
    }

}
