package com.kiwicorp.dumbdue.ui.reminder

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ReminderDao {
    @Insert
    fun insert(reminder: Reminder)

    @Update
    fun update(reminder: Reminder)

    @Delete
    fun delete(reminder: Reminder)

    @Query("DELETE FROM reminder_table")
    fun deleteAllReminders()

    @Query("SELECT * FROM reminder_table ORDER BY timeInMillis DESC")
    fun getAllReminders(): LiveData<List<Reminder>>
}