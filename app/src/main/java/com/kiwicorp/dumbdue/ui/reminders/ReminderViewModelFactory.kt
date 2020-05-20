package com.kiwicorp.dumbdue.ui.reminders

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kiwicorp.dumbdue.data.source.ReminderRepository

class RemindersViewModelFactory(
    private val repository: ReminderRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RemindersViewModel(repository) as T
    }
}