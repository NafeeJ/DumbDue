package com.kiwicorp.dumbdue.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kiwicorp.dumbdue.data.Reminder


@Dao
interface ReminderDao  {

    /**
     * Observers list of reminders ordered by their due date.
     *
     * @return all reminders
     */
    @Query("SELECT * FROM reminders ORDER BY dueDate")
    fun observeReminders(): LiveData<List<Reminder>>

    /**
     * get all reminders ordered by their due date
     *
     * @return all reminders
     */
    @Query("SELECT * FROM reminders ORDER BY dueDate")
    suspend fun getReminders(): List<Reminder>

    /**
     * Observe a single task.
     *
     * @param reminderId the reminder id.
     * @return the reminder corresponding to the reminder id.
     */
    @Query("SELECT * FROM reminders WHERE id = :reminderId")
    fun observeReminderById(reminderId: String): LiveData<Reminder>

    /**
     * get a single reminder
     *
     * @param reminderId the reminder id.
     * @return the reminder corresponding to the reminder id
     */
    @Query("SELECT * FROM reminders WHERE id = :reminderId")
    suspend fun getReminder(reminderId: String): Reminder?

    /**
     * Insert a reminder in the database. If the reminder already exists, replace it.
     *
     * @param reminder the reminder to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder)

    /**
     * Update a reminder.
     *
     * @param reminder reminder to be updated
     * @return the number of reminders updated (always should be 1).
     */
    @Update
    suspend fun updateReminder(reminder: Reminder): Int

    /**
     * Delete a reminder.
     *
     * @param reminder reminder to be deleted
     * @return the number of reminders deleted (always should be 1).
     */
    @Delete
    suspend fun deleteReminder(reminder: Reminder): Int

    /**
     * Delete a reminder by id.
     *
     * @param reminderId the reminder id.
     * @return the number of reminders deleted (should always be 1).
     */
    @Query("DELETE FROM reminders WHERE id = :reminderId")
    suspend fun deleteReminderById(reminderId: String): Int

    /**
     * Delete all reminders.
     */
    @Query("DELETE FROM reminders")
    suspend fun deleteReminders()
}