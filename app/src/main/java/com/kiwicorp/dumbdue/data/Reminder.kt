package com.kiwicorp.dumbdue.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kiwicorp.dumbdue.util.isOverdue
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.ceil

@Entity(tableName = "reminders")
data class Reminder (
    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "calendar")
    var calendar: Calendar = Calendar.getInstance(),

    @ColumnInfo(name = "repeat_val")
    var repeatVal: Int = REPEAT_NONE,

    @ColumnInfo(name = "auto_snooze_val")
    var autoSnoozeVal: Long = AUTO_SNOOZE_MINUTE,

    @PrimaryKey @ColumnInfo(name = "id")
    var id: String = UUID.randomUUID().toString()) {

    companion object {
        const val REPEAT_NONE: Int = 0
        const val REPEAT_DAILY: Int = 1
        const val REPEAT_WEEKDAYS: Int = 2
        const val REPEAT_WEEKLY: Int = 3
        const val REPEAT_MONTHLY: Int = 4
        const val REPEAT_YEARLY: Int = 5
        const val REPEAT_CUSTOM: Int = 6

        const val AUTO_SNOOZE_NONE: Long = 0
        const val AUTO_SNOOZE_MINUTE: Long = 60000
        const val AUTO_SNOOZE_5_MINUTES: Long = 5 * 60000
        const val AUTO_SNOOZE_10_MINUTES: Long = 10 * 60000
        const val AUTO_SNOOZE_15_MINUTES: Long = 15 * 60000
        const val AUTO_SNOOZE_30_MINUTES: Long = 30 * 60000
        const val AUTO_SNOOZE_HOUR: Long = 60 * 60000
    }
}