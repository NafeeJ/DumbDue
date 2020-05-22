package com.kiwicorp.dumbdue.data.source.local

import androidx.room.TypeConverter
import java.util.*

/**
 * Type converts to allow Room to reference complex data types
 */
class Converters {
    @TypeConverter
    fun calendarToMillis(calendar: Calendar): Long = calendar.timeInMillis

    @TypeConverter
    fun millisToCalendar(millis: Long): Calendar = Calendar.getInstance().apply { timeInMillis = millis }
}