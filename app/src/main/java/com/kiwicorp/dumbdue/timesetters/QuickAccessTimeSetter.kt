package com.kiwicorp.dumbdue.timesetters

import org.threeten.bp.temporal.ChronoField
import org.threeten.bp.temporal.Temporal
import org.threeten.bp.temporal.TemporalAdjuster

class QuickAccessTimeSetter : TemporalAdjuster {
    var minute: Int

    var hourOfDay: Int

    override fun toString(): String {
        val hour: Int
        val ampm: String
        if (hourOfDay >= 12) {
            hour = if (hourOfDay == 12) hourOfDay else hourOfDay - 12
            ampm = "PM"
        } else {
            hour = if (hourOfDay == 0) 12 else hourOfDay
            ampm = "AM"
        }
        val min: String = if (minute < 10) "0$minute" else "$minute"
        return "$hour:$min $ampm"
    }

    constructor(hourOfDay: Int, minute: Int ) {
        this.minute = minute
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
        this.minute = minute
        this.hourOfDay = hour
    }

    override fun adjustInto(temporal: Temporal): Temporal {
        return temporal.with(ChronoField.HOUR_OF_DAY, hourOfDay.toLong())
            .with(ChronoField.MINUTE_OF_HOUR, minute.toLong())
    }

}