package com.kiwicorp.dumbdue.ui.addeditreminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kiwicorp.dumbdue.data.source.ReminderRepository

class AddEditReminderViewModelFactory(
    private val repository: ReminderRepository
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEditReminderViewModel::class.java)) {
            return AddEditReminderViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}