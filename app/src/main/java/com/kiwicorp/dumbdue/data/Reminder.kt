package com.kiwicorp.dumbdue.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kiwicorp.dumbdue.data.repeat.RepeatInterval
import org.threeten.bp.ZonedDateTime
import java.util.*

@Entity(tableName = "reminders")
data class Reminder (
    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "dueDate")
    var dueDate: ZonedDateTime = ZonedDateTime.now(),

    @ColumnInfo(name = "repeat_interval")
    var repeatInterval: RepeatInterval? = null,

    @ColumnInfo(name = "auto_snooze_val")
    var autoSnoozeVal: Long = AUTO_SNOOZE_MINUTE,

    @PrimaryKey @ColumnInfo(name = "id")
    var id: String = UUID.randomUUID().toString()) {

    companion object {
        const val AUTO_SNOOZE_NONE: Long = 0
        const val AUTO_SNOOZE_MINUTE: Long = 60000
        const val AUTO_SNOOZE_5_MINUTES: Long = 5 * 60000
        const val AUTO_SNOOZE_10_MINUTES: Long = 10 * 60000
        const val AUTO_SNOOZE_15_MINUTES: Long = 15 * 60000
        const val AUTO_SNOOZE_30_MINUTES: Long = 30 * 60000
        const val AUTO_SNOOZE_HOUR: Long = 60 * 60000
    }
}