package com.kiwicorp.dumbdue.ui.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kiwicorp.dumbdue.data.source.ReminderRepository
import java.lang.IllegalArgumentException

class RemindersViewModelFactory(
    private val repository: ReminderRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RemindersViewModel::class.java)) {
            return RemindersViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModelClass")
    }
}