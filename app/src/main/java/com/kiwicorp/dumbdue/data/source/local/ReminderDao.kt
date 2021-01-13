package com.kiwicorp.dumbdue.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kiwicorp.dumbdue.data.Reminder


@Dao
interface ReminderDao  {

    /**
     * Observes list of reminders ordered by their due date.
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
     * Observes list of unarchived reminders ordered by their due date.
     *
     * @return unarchived reminders
     */
    @Query("SELECT * FROM reminders WHERE is_archived = 0 ORDER BY dueDate")
    fun observeUnarchivedReminders(): LiveData<List<Reminder>>

    /**
     * get unarchived reminders ordered by their due date
     *
     * @return unarchived reminders
     */
    @Query("SELECT * FROM reminders WHERE is_archived = 0 ORDER BY dueDate")
    suspend fun getUnarchivedReminders(): List<Reminder>

    /**
     * Observes a list of archived reminders ordered by their due date descending.
     *
     * @return archived reminders
     */
    @Query("SELECT * FROM reminders WHERE is_archived = 1 ORDER BY dueDate DESC")
    fun observeArchivedReminders(): LiveData<List<Reminder>>

    /**
     * get archived reminders ordered by their due date descending.
     *
     * @return archived reminders
     */
    @Query("SELECT * FROM reminders WHERE is_archived = 0 ORDER BY dueDate DESC")
    suspend fun getArchivedReminders(): List<Reminder>


    /**
     * get unarchived reminders with titles that contain the query ordered by their date.
     *
     * @param query the search query.
     * @return unarchived reminders with titles that contain the query.
     */
    @Query("SELECT * FROM reminders WHERE is_archived = 0 AND title LIKE '%' || :query || '%' ORDER BY dueDate")
    suspend fun getSearchedUnarchivedReminders(query: String?): List<Reminder>

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
     * Delete all archived reminders.
     */
    @Query("DELETE FROM reminders WHERE is_archived = 1")
    suspend fun deleteArchivedReminders()

    /**
     * Delete all reminders.
     */
    @Query("DELETE FROM reminders")
    suspend fun deleteReminders()
}