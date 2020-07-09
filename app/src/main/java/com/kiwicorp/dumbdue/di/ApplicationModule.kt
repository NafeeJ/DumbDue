package com.kiwicorp.dumbdue.di

import android.content.Context
import androidx.room.Room
import com.kiwicorp.dumbdue.data.source.ReminderRepository
import com.kiwicorp.dumbdue.data.source.local.ReminderDatabase
import com.kiwicorp.dumbdue.notifications.ReminderAlarmManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object ApplicationModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideReminderRepository(reminderDatabase: ReminderDatabase, alarmManager: ReminderAlarmManager): ReminderRepository {
        return ReminderRepository(reminderDatabase.reminderDao(), alarmManager)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideDataBase(context: Context): ReminderDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ReminderDatabase::class.java,
            "Reminder.db").build()
    }
}