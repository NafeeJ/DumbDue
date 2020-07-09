package com.kiwicorp.dumbdue.data.repeat

import com.kiwicorp.dumbdue.util.getFullName
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.TemporalAdjusters

/**
 * A class that represents a repeat interval that's scoped in a week
 *
 * [frequency] will be which weeks users will receive reminders on
 *
 * [daysOfWeek] is a sorted list of the days the user wishes to receive reminders on. The list
 * should never have a size of more than 7. Sunday is considered to be the first day.
 */
class RepeatWeeklyInterval(frequency: Int, time: LocalTime, var dateOfFirstDayOfStartingWeek: LocalDate, val daysOfWeek: List<DayOfWeek>): RepeatInterval(frequency, time) {

    override fun getNextOccurrence(): ZonedDateTime {
        return if (prevOccurrence == null) {
            val firstOccurrence = dateOfFirstDayOfStartingWeek.with(TemporalAdjusters.nextOrSame(daysOfWeek.first()))
            prevOccurrence = ZonedDateTime.of(LocalDateTime.of(firstOccurrence, time), ZoneId.systemDefault())
            prevOccurrence!!
        } else {
            val currIndex = daysOfWeek.indexOf(prevOccurrence!!.dayOfWeek)

            val nextIndex = if (currIndex != daysOfWeek.lastIndex) currIndex + 1 else 0
            val nextDayOfWeek = daysOfWeek[nextIndex]

            prevOccurrence = prevOccurrence!!.with(TemporalAdjusters.next(nextDayOfWeek))

            if (nextIndex == 0) { // if moved to a new week, account for frequency
                prevOccurrence = prevOccurrence!!.plusWeeks(frequency - 1L)
            }

            prevOccurrence!!
        }
    }

    override fun toString(): String {
        val time = time.format(DateTimeFormatter.ofPattern("h:mm a"))

        val weekdays = listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY
        )

        if (daysOfWeek == weekdays) {
            return if (frequency == 1) {
                "Weekdays $time"
            } else {
                "Weekdays $time every $frequency weeks"
            }
        } else {

            var string = ""
            when (daysOfWeek.size) {
                1 -> string = "${daysOfWeek[0].getFullName()}s"
                2 -> string = "${daysOfWeek[0].getFullName()}s and ${daysOfWeek[1].getFullName()}s"
                else -> {
                    for (i in 0..daysOfWeek.size - 2) {
                        string += "${daysOfWeek[i].getFullName()}s, "
                    }
                    string += "and ${daysOfWeek.last().getFullName()}s"
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