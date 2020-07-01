package com.kiwicorp.dumbdue.data.repeat

import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*

/**
 * A class that represents a repeat interval that's scoped in a week
 *
 * [frequency] will be which weeks users will receive reminders on
 *
 * [daysOfWeek] is a sorted list of [Calendar]'s constants eg: [Calendar.TUESDAY]. It represents
 * the days the user wishes to receive reminders on. The list should never have a size of more than 7.
 * Sunday is considered to be the first day.
 */
data class RepeatWeekly(override var frequency: Int, var dateTimeOfFirstDayOfStartingWeek: LocalDateTime, val daysOfWeek: List<Int>): RepeatInterval(frequency) {

    override fun getNextOccurrence(): Calendar {
        return if (prevOccurrence == null) {
            prevOccurrence = Calendar.getInstance().apply {
                set(dateTimeOfFirstDayOfStartingWeek.year,
                    dateTimeOfFirstDayOfStartingWeek.monthValue - 1, // -1 cause Calendar.JANUARY starts at 0
                    dateTimeOfFirstDayOfStartingWeek.dayOfMonth,
                    dateTimeOfFirstDayOfStartingWeek.hour,
                    dateTimeOfFirstDayOfStartingWeek.minute)
                set(Calendar.DAY_OF_WEEK,daysOfWeek[0])
            }
            prevOccurrence!!
        } else {
            val next = Calendar.getInstance().apply { timeInMillis = prevOccurrence!!.timeInMillis }
            // get next day
            val currentDay = prevOccurrence!!.get(Calendar.DAY_OF_WEEK)
            val nextDay = daysOfWeek.firstOrNull { it > currentDay } ?: daysOfWeek.first()

            next.add(Calendar.DAY_OF_YEAR,1)
            // add days to returnCalendar until returnCalendar's day of week is nextDay
            while (next.get(Calendar.DAY_OF_WEEK) != nextDay) {
                next.add(Calendar.DAY_OF_YEAR,1)
            }
            // if returnCalendar goes into a new week, account for the frequency
            if (next.get(Calendar.DAY_OF_WEEK) == daysOfWeek[0]) {
                next.add(Calendar.WEEK_OF_YEAR,frequency - 1)
            }
            prevOccurrence = next
            next
        }
    }

    override fun toString(): String {
        val time = dateTimeOfFirstDayOfStartingWeek.toLocalTime().format(DateTimeFormatter.ofPattern("h:mm a"))

        val weekdays = listOf(
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY
        )

        if (daysOfWeek == weekdays) {
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
            when (daysOfWeek.size) {
                1 -> string = "${dayConstantToString[daysOfWeek[0]]}s"
                2 -> string = "${dayConstantToString[daysOfWeek[0]]}s and ${dayConstantToString[daysOfWeek[1]]}s"
                else -> {
                    for (i in 0..daysOfWeek.size - 2) {
                        string += "${dayConstantToString[daysOfWeek[i]]}s, "
                    }
                    string += "and ${dayConstantToString[daysOfWeek.last()]}s"
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