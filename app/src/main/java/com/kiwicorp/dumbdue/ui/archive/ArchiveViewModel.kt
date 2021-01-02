package com.kiwicorp.dumbdue.ui.archive

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.data.source.ReminderRepository
import com.kiwicorp.dumbdue.notifications.ReminderAlarmManager
import kotlinx.coroutines.launch

class ArchiveViewModel @ViewModelInject constructor(
    private val reminderRepository: ReminderRepository,
    private val reminderAlarmManager: ReminderAlarmManager
): ViewModel() {
    val reminders = reminderRepository.archivedReminders

    fun unarchive(reminder: Reminder) {
        viewModelScope.launch {
            reminder.isArchived = false
            reminderRepository.updateReminder(reminder)
            reminderAlarmManager.setAlarm(reminder)
        }
    }

    fun delete(reminder: Reminder) {
        viewModelScope.launch {
            reminderRepository.deleteReminder(reminder)
            reminderAlarmManager.cancelAlarm(reminder)
        }
    }
}