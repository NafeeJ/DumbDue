package com.kiwicorp.dumbdue.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kiwicorp.dumbdue.data.Reminder

@Database(entities = [Reminder::class], version = 2)
@TypeConverters(Converters::class)
abstract class ReminderDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao
}