package com.kiwicorp.dumbdue.ui.addreminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kiwicorp.dumbdue.data.source.ReminderRepository
import com.kiwicorp.dumbdue.ui.reminders.RemindersViewModel

class AddReminderViewModelFactory(
    private val repository: ReminderRepository): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddReminderViewModel::class.java)) {
            return AddReminderViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}