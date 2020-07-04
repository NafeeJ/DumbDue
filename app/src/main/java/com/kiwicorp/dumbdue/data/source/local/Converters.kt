package com.kiwicorp.dumbdue.data.source.local

import androidx.room.TypeConverter
import com.google.gson.GsonBuilder
import com.google.gson.InstanceCreator
import com.kiwicorp.dumbdue.data.repeat.*
import com.kiwicorp.dumbdue.util.RuntimeTypeAdapterFactory
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import java.lang.reflect.Type
import java.util.*

/**
 * Type converts to allow Room to reference complex data types
 */
class Converters {
    @TypeConverter
    fun zonedDateTimeToMillis(zonedDateTime: ZonedDateTime): String = zonedDateTime.toString()

    @TypeConverter
    fun stringToZonedDateTime(string: String): ZonedDateTime = ZonedDateTime.parse(string)

    private val repeatIntervalTypeAdapterFactory = RuntimeTypeAdapterFactory
        .of(RepeatInterval::class.java, "type")
        .registerSubtype(RepeatDaily::class.java, RepeatDaily::class.java.name)
        .registerSubtype(RepeatWeekly::class.java, RepeatWeekly::class.java.name)
        .registerSubtype(RepeatMonthlyByCount::class.java, RepeatMonthlyByCount::class.java.name)
        .registerSubtype(RepeatMonthlyByNumber::class.java, RepeatMonthlyByNumber::class.java.name)
        .registerSubtype(RepeatYearlyByCount::class.java, RepeatYearlyByCount::class.java.name)
        .registerSubtype(RepeatYearlyByNumber::class.java, RepeatYearlyByNumber::class.java.name)

    private val gson = GsonBuilder().registerTypeAdapterFactory(repeatIntervalTypeAdapterFactory).registerTypeAdapter(ZoneId::class.java,InstanceCreator<ZoneId> { ZoneId.systemDefault() }).create()

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