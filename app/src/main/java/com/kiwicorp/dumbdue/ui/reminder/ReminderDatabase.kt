package com.kiwicorp.dumbdue.ui.reminder

import android.content.Context
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.*

@Database(entities = [Reminder::class], version = 1)
abstract class ReminderDatabase : RoomDatabase() {

    abstract fun reminderDao(): ReminderDao

    companion object {
        //Singleton prevents multiple instances of database opening at the same time
        @Volatile
        private var INSTANCE: ReminderDatabase? = null

        fun getDatabase(context: Context): ReminderDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance =  Room.databaseBuilder(
                    context.applicationContext,
                    ReminderDatabase::class.java,
                    "reminder_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallBack)
                    .build()
                INSTANCE = instance
                return instance
            }
        }
        //callback for prepopulate async task
        private val roomCallBack = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                PopulateDbAysncTask(INSTANCE as ReminderDatabase).execute()
            }
        }
        //async task to prepopulate database
        private class PopulateDbAysncTask(reminderDatabase: ReminderDatabase) : AsyncTask<Void, Void, Void>() {
            var reminderDao: ReminderDao = reminderDatabase.reminderDao()

            override fun doInBackground(vararg p0: Void?): Void? {
                val calendar = Calendar.getInstance()
                reminderDao.insert(Reminder(0,"Title 1",calendar.timeInMillis - 10,Reminder.REPEAT_MONTHLY,Reminder.AUTO_SNOOZE_10_MINUTES))
                calendar.add(Calendar.DAY_OF_YEAR,1)
                reminderDao.insert(Reminder(1,"Title 2",calendar.timeInMillis,Reminder.REPEAT_DAILY,Reminder.AUTO_SNOOZE_30_MINUTES))
                calendar.add(Calendar.WEEK_OF_YEAR,1)
                reminderDao.insert(Reminder(3,"Title 3",calendar.timeInMillis,Reminder.REPEAT_WEEKDAYS,Reminder.AUTO_SNOOZE_MINUTE))
                return null
            }
        }

    }
}