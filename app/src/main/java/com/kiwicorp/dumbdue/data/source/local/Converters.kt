package com.kiwicorp.dumbdue.data.source.local

import androidx.room.TypeConverter
import com.google.gson.GsonBuilder
import com.google.gson.InstanceCreator
import com.kiwicorp.dumbdue.data.repeat.*
import com.kiwicorp.dumbdue.util.RuntimeTypeAdapterFactory
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

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
        .registerSubtype(RepeatDailyInterval::class.java, RepeatDailyInterval::class.java.name)
        .registerSubtype(RepeatWeeklyInterval::class.java, RepeatWeeklyInterval::class.java.name)
        .registerSubtype(RepeatMonthlyByCountInterval::class.java, RepeatMonthlyByCountInterval::class.java.name)
        .registerSubtype(RepeatMonthlyByNumberInterval::class.java, RepeatMonthlyByNumberInterval::class.java.name)
        .registerSubtype(RepeatYearlyByCountInterval::class.java, RepeatYearlyByCountInterval::class.java.name)
        .registerSubtype(RepeatYearlyByNumberInterval::class.java, RepeatYearlyByNumberInterval::class.java.name)
    //todo migrate to moshi
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