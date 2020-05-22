package com.kiwicorp.dumbdue.util

import android.content.Context
import com.kiwicorp.dumbdue.data.source.ReminderRepository
import com.kiwicorp.dumbdue.data.source.local.ReminderDatabase
import com.kiwicorp.dumbdue.ui.addreminder.AddReminderViewModelFactory
import com.kiwicorp.dumbdue.ui.reminders.RemindersViewModelFactory


/**
 * Static methods used to inject classes needed for various activities and fragments
 */
object InjectorUtils {
    private fun getReminderRepository(context: Context): ReminderRepository {
        return ReminderRepository.getInstance(
            ReminderDatabase.getInstance(context.applicationContext).reminderDao())
    }

    fun provideRemindersViewModelFactory(context: Context): RemindersViewModelFactory {
        val repository = getReminderRepository(context)
        return RemindersViewModelFactory(repository)
    }

    fun provideAddRemindersViewModelFactory(context: Context): AddReminderViewModelFactory {
        val repository = getReminderRepository(context)
        return AddReminderViewModelFactory(repository)
    }
}