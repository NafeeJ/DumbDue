package com.kiwicorp.dumbdue.timesetters

import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoField
import org.threeten.bp.temporal.ChronoUnit
import org.threeten.bp.temporal.Temporal
import org.threeten.bp.temporal.TemporalAdjuster

class QuickAccessTimeSetter : TemporalAdjuster {
    var minute: Int
        set(value) {
            if (value > 59) {
                throw IllegalArgumentException("minute should not be more than 59")
            }
            field = value
        }

    var hour: Int
        set(value) {
            if (value > 12) {
                throw IllegalArgumentException("hour should not be more than 12")
            }
            field = value
        }

    var amPm: AmPm

    override fun toString(): String {
        val minuteString: String = if (minute < 10) "0$minute" else "$minute"
        return "$hour:$minuteString $amPm"
    }

    constructor(hour: Int, minute: Int, amPm: AmPm ) {
        this.minute = minute
        this.hour = hour
        this.amPm = amPm
    }

    constructor(text: String) {
        minute = text.substringAfter(':').substringBefore(' ').toInt()
        hour = text.substringBefore(':').toInt()
        amPm = if (text.takeLast(2) == "AM") AmPm.AM else AmPm.PM
    }

    override fun adjustInto(temporal: Temporal): Temporal {
        return temporal.with(ChronoField.HOUR_OF_AMPM, if (hour == 12) 0 else hour.toLong())
            .with(ChronoField.MINUTE_OF_HOUR, minute.toLong()).with(ChronoField.AMPM_OF_DAY,amPm.ordinal.toLong())
    }

}

enum class AmPm {
    AM,
    PM;

    override fun toString(): String {
        return if (this == AM) "AM" else "PM"
    }
}