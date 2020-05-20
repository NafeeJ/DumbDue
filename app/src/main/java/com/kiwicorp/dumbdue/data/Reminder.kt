package com.kiwicorp.dumbdue.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "reminders")
data class Reminder (
    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "due_date_milli" )
    var dueDateMilli: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "repeat_val")
    var repeatVal: Int = REPEAT_NONE,

    @ColumnInfo(name = "auto_snooze_val")
    var autoSnoozeVal: Int = AUTO_SNOOZE_MINUTE,

    @PrimaryKey @ColumnInfo(name = "id")
    var reminderId: String = UUID.randomUUID().toString()) {

    companion object {
        const val REPEAT_NONE: Int = 0
        const val REPEAT_DAILY: Int = 1
        const val REPEAT_WEEKDAYS: Int = 2
        const val REPEAT_WEEKLY: Int = 3
        const val REPEAT_MONTHLY: Int = 4
        const val REPEAT_YEARLY: Int = 5
        const val REPEAT_CUSTOM: Int = 6

        const val AUTO_SNOOZE_NONE: Int = 0
        const val AUTO_SNOOZE_MINUTE: Int = 1
        const val AUTO_SNOOZE_5_MINUTES: Int = 2
        const val AUTO_SNOOZE_10_MINUTES: Int = 3
        const val AUTO_SNOOZE_15_MINUTES: Int = 4
        const val AUTO_SNOOZE_30_MINUTES: Int = 5
        const val AUTO_SNOOZE_HOUR : Int = 6
    }

    fun isOverDue() = dueDateMilli < System.currentTimeMillis()
}