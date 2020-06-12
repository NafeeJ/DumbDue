package com.kiwicorp.dumbdue.timesetters

import java.util.*

class QuickAccessTimeSetter {
    var min: Int

    var hourOfDay: Int

    val text: String
        get() {
            val hour: Int
            val ampm: String
            if (hourOfDay >= 12) {
                hour = if (hourOfDay == 12) hourOfDay else hourOfDay - 12
                ampm = "PM"
            } else {
                hour = if (hourOfDay == 0) 12 else hourOfDay
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