package com.kiwicorp.dumbdue.ui.settings

import android.content.Context
import android.content.SharedPreferences
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesStorage @Inject constructor(context: Context) {
    companion object{
        const val PREF_NAME = "dumb_due"

        const val PREFS_TIME_SETTER_1 = "prefs_time_setter_1"
        const val PREFS_TIME_SETTER_2 = "prefs_time_setter_2"
        const val PREFS_TIME_SETTER_3 = "prefs_time_setter_3"
        const val PREFS_TIME_SETTER_4 = "prefs_time_setter_4"
        const val PREFS_TIME_SETTER_5 = "prefs_time_setter_5"
        const val PREFS_TIME_SETTER_6 = "prefs_time_setter_6"
        const val PREFS_TIME_SETTER_7 = "prefs_time_setter_7"
        const val PREFS_TIME_SETTER_8 = "prefs_time_setter_8"

        const val PREFS_QUICK_ACCESS_1 = "prefs_quick_access_1"
        const val PREFS_QUICK_ACCESS_2 = "prefs_quick_access_2"
        const val PREFS_QUICK_ACCESS_3 = "prefs_quick_access_3"
        const val PREFS_QUICK_ACCESS_4 = "prefs_quick_access_4"
    }

    /**
     * Map containing all the default values of the TimeSetters
     */
    private val timeSetterKeyToDefaultValue: Map<String, String> = mapOf(
        PREFS_TIME_SETTER_1 to "+10 min",
        PREFS_TIME_SETTER_2 to "+1 hr",
        PREFS_TIME_SETTER_3 to "+3 hr",
        PREFS_TIME_SETTER_4 to "+1 day",
        PREFS_TIME_SETTER_5 to "-10 min",
        PREFS_TIME_SETTER_6 to "-1 hr",
        PREFS_TIME_SETTER_7 to "-3 hr",
        PREFS_TIME_SETTER_8 to "-1 day",
        PREFS_QUICK_ACCESS_1 to "9:30 AM",
        PREFS_QUICK_ACCESS_2 to "12:00 PM",
        PREFS_QUICK_ACCESS_3 to "6:30 PM",
        PREFS_QUICK_ACCESS_4 to "10:00 PM"
    )

    private val prefs = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE)

    private val prefsEditor: SharedPreferences.Editor = prefs.edit()

    fun updateIncrementalTimeSetter(key: String, incrementalTimeSetter: IncrementalTimeSetter) {
        prefsEditor.putString(key, incrementalTimeSetter.text)
        prefsEditor.apply()
    }

    /**
     * Returns a timer setter from the key
     */
    fun getIncrementalTimeSetter(key: String): IncrementalTimeSetter{
        return IncrementalTimeSetter(prefs.getString(key, timeSetterKeyToDefaultValue[key])!!)
    }

    fun updateQuickAccessTimeSetter(key: String, quickAccess: QuickAccessTimeSetter) {
        prefsEditor.putString(key, quickAccess.text)
        prefsEditor.apply()
    }

    /**
     * Returns a quick access from the key
     */
    fun getQuickAccessTimeSetter(key: String): QuickAccessTimeSetter {
        return QuickAccessTimeSetter(prefs.getString(key, timeSetterKeyToDefaultValue[key])!!)
    }
}

class IncrementalTimeSetter {

    var number: Int

    /**
     * [unit] will be one of [Calendar]'s units eg: [Calendar.MINUTE]
     */
    var unit: Int

    val text: String
        get() {
            val unitString = when(unit) {
                Calendar.MINUTE -> "min"
                Calendar.HOUR -> "hr"
                Calendar.DAY_OF_YEAR -> "day"
                Calendar.WEEK_OF_YEAR -> "wk"
                Calendar.MONTH -> "mo"
                else -> "yr"
            }
            return "${if (number > 0) "+$number" else number} $unitString"
        }


    constructor(number: Int, unit: Int) {
        this.number = number
        this.unit = unit
    }

    constructor(text: String) {
        // number is the actual number of how much to increment/decrement, notDigits contains "+ unit"
        var (number,notDigits)= text.partition { it.isDigit() }
        val unit: Int = when (notDigits.substring(2)) {
            "min" -> Calendar.MINUTE
            "hr" -> Calendar.HOUR
            "day" -> Calendar.DAY_OF_YEAR
            "wk" -> Calendar.WEEK_OF_YEAR
            "mo" -> Calendar.MONTH
            else -> Calendar.YEAR
        }
        if (notDigits[0] == '-') number = "-$number"

        this.number = number.toInt()
        this.unit = unit
    }

    fun incrementTime(calendar: Calendar) {
        calendar.add(unit,number)
    }
}

class QuickAccessTimeSetter {
    var min: Int

    var hourOfDay: Int

    val text: String
        get() {
            val hour: Int
            val ampm: String
            if (hourOfDay > 12) {
                hour = hourOfDay - 12
                ampm = "PM"
            } else {
                hour = hourOfDay
                ampm = "AM"
            }
            val min: String = if (min < 10) "0$min" else "$min"
            return "$hour:$min $ampm"
        }

    constructor(min: Int, hourOfDay: Int) {
        this.min = min
        this.hourOfDay = hourOfDay
    }

    constructor(text: String) {
        val minute: Int = text.substringAfter(':').substringBefore(' ').toInt()
        var hour: Int = text.substringBefore(':').toInt()
        if (text.takeLast(2) == "PM") {
            if (hour < 12) hour += 12
        } else {
            if (hour == 12) hour = 0
        }
        this.min = minute
        this.hourOfDay = hour
    }

    fun setTime(calendar: Calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, min)
    }
}