package com.kiwicorp.dumbdue.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    fun provideReminderRepository(reminderDatabase: ReminderDatabase): ReminderRepository {
        return ReminderRepository(reminderDatabase.reminderDao())
    }

    private val MIGRATION_1_2 = object : Migration(1,2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE reminders ADD COLUMN is_archived INTEGER NOT NULL DEFAULT 0")
        }
    }

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): ReminderDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ReminderDatabase::class.java,
            "Reminder.db")
            .addMigrations(MIGRATION_1_2)
            .build()
    }
}