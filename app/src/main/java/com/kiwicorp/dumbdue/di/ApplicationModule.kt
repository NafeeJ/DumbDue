package com.kiwicorp.dumbdue.di

import android.content.Context
import androidx.room.Room
import com.kiwicorp.dumbdue.data.source.ReminderRepository
import com.kiwicorp.dumbdue.data.source.local.ReminderDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object ApplicationModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideReminderRepository(reminderDatabase: ReminderDatabase): ReminderRepository {
        return ReminderRepository(reminderDatabase.reminderDao())
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