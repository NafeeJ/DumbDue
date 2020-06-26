package com.kiwicorp.dumbdue.data.repeat

import java.text.SimpleDateFormat
import java.util.*

/**
 * A class that represents a repeat interval that's scoped in a week
 *
 * [frequency] will be which weeks users will receive reminders on
 *
 * [days] is a sorted list of [Calendar]'s constants eg: [Calendar.TUESDAY]. It represents
 * the days the user wishes to receive reminders on. The list should never have a size of more than 7.
 * Sunday is considered to be the first day.
 */
data class RepeatWeekly(override var frequency: Int, override var firstOccurrence: Calendar, val days: List<Int>): RepeatInterval(frequency, firstOccurrence) {

    override fun getNextOccurrence(): Calendar {
        return if (prevOccurrence == null) {
            prevOccurrence = firstOccurrence
            firstOccurrence
        } else {
            val next = Calendar.getInstance().apply { timeInMillis = prevOccurrence!!.timeInMillis }
            // get next day
            val currentDay = prevOccurrence!!.get(Calendar.DAY_OF_WEEK)
            val nextDay = days.firstOrNull { it > currentDay } ?: days.first()

            next.add(Calendar.DAY_OF_YEAR,1)
            // add days to returnCalendar until returnCalendar's day of week is nextDay
            while (next.get(Calendar.DAY_OF_WEEK) != nextDay) {
                next.add(Calendar.DAY_OF_YEAR,1)
            }
            // if returnCalendar goes into a new week, account for the frequency
            if (next.get(Calendar.DAY_OF_WEEK) == days[0]) {
                next.add(Calendar.WEEK_OF_YEAR,frequency - 1)
            }
            prevOccurrence = next
            next
        }
    }

    override fun toString(): String {
        val time = SimpleDateFormat("h:mm a", Locale.US).format(firstOccurrence.time)

        val weekdays = listOf(
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY
        )

        if (days == weekdays) {
            return if (frequency == 1) {
                "Weekdays $time"
            } else {
                "Weekdays $time every $frequency weeks"
            }
        } else {
            val dayConstantToString = mapOf(
                Calendar.MONDAY to "Monday",
                Calendar.TUESDAY to "Tuesday",
                Calendar.WEDNESDAY to "Wednesday",
                Calendar.THURSDAY to "Thursday",
                Calendar.FRIDAY to "Friday",
                Calendar.SATURDAY to "Saturday",
                Calendar.SUNDAY to "Sunday"
            )

            var string = ""
            when (days.size) {
                1 -> string = "${dayConstantToString[days[0]]}s"
                2 -> string = "${dayConstantToString[days[0]]}s and ${dayConstantToString[days[1]]}s"
                else -> {
                    for (i in 0..days.size - 2) {
                        string += "${dayConstantToString[days[i]]}s, "
                    }
                    string += "and ${dayConstantToString[days.last()]}s"
                }
            }
            string += " at $time"
            if (frequency != 1) {
                string += " every $frequency weeks"
            }
            return string
        }
    }
}