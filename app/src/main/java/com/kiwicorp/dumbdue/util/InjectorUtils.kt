package com.kiwicorp.dumbdue.util

import android.content.Context
import com.kiwicorp.dumbdue.data.source.ReminderRepository
import com.kiwicorp.dumbdue.data.source.local.ReminderDatabase
import com.kiwicorp.dumbdue.ui.addeditreminder.AddEditViewModelFactory
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

    fun provideAddEditViewModelFactory(context: Context): AddEditViewModelFactory {
        val repository = getReminderRepository(context)
        return AddEditViewModelFactory(repository)
    }
}