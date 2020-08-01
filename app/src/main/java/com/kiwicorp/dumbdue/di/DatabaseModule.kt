package com.kiwicorp.dumbdue.di

import android.content.Context
import androidx.room.Room
import com.kiwicorp.dumbdue.data.source.ReminderRepository
import com.kiwicorp.dumbdue.data.source.local.ReminderDatabase
import com.kiwicorp.dumbdue.notifications.ReminderAlarmManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun provideReminderRepository(reminderDatabase: ReminderDatabase, alarmManager: ReminderAlarmManager): ReminderRepository {
        return ReminderRepository(reminderDatabase.reminderDao(), alarmManager)
    }

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): ReminderDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ReminderDatabase::class.java,
            "Reminder.db").build()
    }
}