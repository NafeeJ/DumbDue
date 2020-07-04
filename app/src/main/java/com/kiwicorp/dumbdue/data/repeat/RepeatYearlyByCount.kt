package com.kiwicorp.dumbdue.data.repeat

import com.kiwicorp.dumbdue.util.getFullName
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.TemporalAdjuster
import org.threeten.bp.temporal.TemporalAdjusters
import java.text.SimpleDateFormat
import java.util.*

/**
 * A class the represents a repeat interval that's scoped in years and will be based off of the
 * month and the count of a given day of the week in the month eg: (second friday of June each year)
 *
 * [frequency] will be the years users receive a reminder on
 *
 * [dayOfWeekInMonth] will be a number from 1 to 5. 1 indicates first, 2 indicates second, etc.
 * Since a given day of the week in a given month must occur at least 4 times but can go up to at
 * most 5 times, 5 represent the last occurrence of the day of the week in the month.
 *
 */
class RepeatYearlyByCount(frequency: Int, val startingYear: Year, val month: Month, var dayOfWeek: DayOfWeek, var dayOfWeekInMonth: Int, val time: LocalTime): RepeatInterval(frequency) {

    override fun getNextOccurrence(): ZonedDateTime {
        return if (prevOccurrence == null) {
            prevOccurrence = ZonedDateTime.of(LocalDateTime.of(startingYear.value,month,1,time.hour,time.minute),
                ZoneId.systemDefault())

            prevOccurrence = if (dayOfWeekInMonth == 5) {
                prevOccurrence!!.with(TemporalAdjusters.lastDayOfMonth()).with(TemporalAdjusters.previousOrSame(dayOfWeek))
            } else {
                prevOccurrence!!.with(TemporalAdjusters.dayOfWeekInMonth(dayOfWeekInMonth,dayOfWeek))
            }

            prevOccurrence!!
        } else {
            prevOccurrence = prevOccurrence!!.plusYears(frequency.toLong())

            prevOccurrence = if (dayOfWeekInMonth == 5) {
                prevOccurrence!!.with(TemporalAdjusters.lastDayOfMonth()).with(TemporalAdjusters.previousOrSame(dayOfWeek))
            } else {
                prevOccurrence!!.with(TemporalAdjusters.dayOfWeekInMonth(dayOfWeekInMonth,dayOfWeek))
            }

            prevOccurrence!!
        }
    }

    override fun toString(): String {
        val time = this.time.format(DateTimeFormatter.ofPattern("h:mm a"))

        val week = when(dayOfWeekInMonth) {
            1 -> "1st"
            2 -> "2nd"
            3 -> "3rd"
            4 -> "4th"
            else -> "last"
        }

        return "On the $week ${dayOfWeek.getFullName()} of ${month.getFullName()} at $time ${if (frequency == 1) "every year" else "every $frequency years"}"
    }
}