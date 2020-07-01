package com.kiwicorp.dumbdue.data.source.local

import androidx.room.TypeConverter
import com.google.gson.GsonBuilder
import com.kiwicorp.dumbdue.data.repeat.*
import com.kiwicorp.dumbdue.util.RuntimeTypeAdapterFactory
import java.util.*

/**
 * Type converts to allow Room to reference complex data types
 */
class Converters {
    @TypeConverter
    fun calendarToMillis(calendar: Calendar): Long = calendar.timeInMillis

    @TypeConverter
    fun millisToCalendar(millis: Long): Calendar = Calendar.getInstance().apply { timeInMillis = millis }

    private val repeatIntervalTypeAdapterFactory = RuntimeTypeAdapterFactory
        .of(RepeatInterval::class.java, "type")
        .registerSubtype(RepeatDaily::class.java, RepeatDaily::class.java.name)
        .registerSubtype(RepeatWeekly::class.java, RepeatWeekly::class.java.name)
        .registerSubtype(RepeatMonthlyByCount::class.java, RepeatMonthlyByCount::class.java.name)
        .registerSubtype(RepeatMonthlyByNumber::class.java, RepeatMonthlyByNumber::class.java.name)
        .registerSubtype(RepeatYearlyByCount::class.java, RepeatYearlyByCount::class.java.name)
        .registerSubtype(RepeatYearlyByNumber::class.java, RepeatYearlyByNumber::class.java.name)

    private val gson = GsonBuilder().registerTypeAdapterFactory(repeatIntervalTypeAdapterFactory).create()

    @TypeConverter
    fun repeatIntervalToJsonString(repeatInterval: RepeatInterval?): String? {
        return if (repeatInterval != null) {
            gson.toJson(repeatInterval)
        } else {
            null
        }

    }

    @TypeConverter
    fun jsonStringToRepeatInterval(jsonString: String?): RepeatInterval? {
        return if (jsonString != null) {
            gson.fromJson(jsonString, RepeatInterval::class.java)
        } else {
            null
        }
    }
}