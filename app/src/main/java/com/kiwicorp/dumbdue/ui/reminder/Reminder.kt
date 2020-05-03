package com.kiwicorp.dumbdue.ui.reminder

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "reminder_table")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int= 0,
    var title: String = "",
    var timeInMillis: Long = 0,
    @ColumnInfo(name = "repeat_val")
    var repeatVal: Int = 0,
    @ColumnInfo(name = "auto_snooze_val")
    var autoSnoozeVal: Int = 0
){
    companion object {
        //ints used to determine the user's desired repeat frequency
        const val REPEAT_NONE: Int = 0
        const val REPEAT_DAILY: Int = 1
        const val REPEAT_WEEKDAYS: Int = 2
        const val REPEAT_WEEKLY: Int = 3
        const val REPEAT_MONTHLY: Int = 4
        const val REPEAT_YEARLY: Int = 5
        const val REPEAT_CUSTOM: Int = 6
        //ints used to determine user's desired auto-snooze
        const val AUTO_SNOOZE_NONE: Int = 0
        const val AUTO_SNOOZE_MINUTE: Int = 1
        const val AUTO_SNOOZE_5_MINUTES: Int = 2
        const val AUTO_SNOOZE_10_MINUTES: Int = 3
        const val AUTO_SNOOZE_15_MINUTES: Int = 4
        const val AUTO_SNOOZE_30_MINUTES: Int = 5
        const val AUTO_SNOOZE_HOUR : Int = 6
    }
}
