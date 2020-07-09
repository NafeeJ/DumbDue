package com.kiwicorp.dumbdue.data.repeat

import com.kiwicorp.dumbdue.util.getFullName
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.*
import java.util.*

/**
 * A class that represents a repeat interval for reminders that's scoped in a month and will be
 * based off of the count of a given day of the week in the month eg: (second friday each month)
 *
 * [frequency] will be which months will be months users receive reminders on
 *
 * [days] will be a sorted list of [Day]s where each day represents a day in the month
 * that the user wishes to repeat the reminder on.
 */

class RepeatMonthlyByCountInterval(frequency: Int, startingYearMonth: YearMonth, time: LocalTime, val days: List<Day>): RepeatMonthlyInterval(frequency, time, startingYearMonth) {

    override fun getNextOccurrence(): ZonedDateTime {
        return if (prevOccurrence == null) {
            prevOccurrence = ZonedDateTime.of(findFirstDayOfMonth(startingYearMonth),time, ZoneId.systemDefault())
            prevOccurrence!!
        } else {
            //todo make this more efficient
            var next = LocalDate.MAX
            //try to find the next day in this month
            for (day in days) {
                val curr = LocalDate.from(prevOccurrence!!).with(day)
                if (curr.isAfter(prevOccurrence!!.toLocalDate()) && ChronoUnit.DAYS.between(prevOccurrence!!.toLocalDate(),curr) < ChronoUnit.DAYS.between(prevOccurrence!!.toLocalDate(),next)) {
                    next = curr
                    // break if day of week in month is less than four because we don't have to worry
                    // about the inconsistency of the fourth day of a day in the week coming before the
                    // last day of another day of the week
                    if (day.dayOfWeekInMonth!! < 4) break
                }
            }
            // if could not find a day in this month
            if (next == LocalDate.MAX) {
                next = findFirstDayOfMonth(YearMonth.from(prevOccurrence!!).plusMonths(frequency.toLong()))
            }
            prevOccurrence = prevOccurrence!!.with(next)
            prevOccurrence!!
        }
    }
    /**
     * returns the first day of [days] in the next month (this may change from month to month
     * because the last day in the month of a day of the week could come before the fourth day in
     * the month of another day of the week)
     */
    private fun findFirstDayOfMonth(yearMonth: YearMonth): LocalDate {
        var first = LocalDate.of(yearMonth.year, yearMonth.month,1).with(TemporalAdjusters.lastDayOfMonth())
        for (day in days) {
            val curr = LocalDate.of(yearMonth.year,yearMonth.month,1).with(day)

            if (curr.isBefore(first)) {
                first = curr
                // break if day of week in month is less than four because we don't have to worry
                // about the inconsistency of the fourth day of a day in the week coming before the
                // last day of another day of the week
                if (day.dayOfWeekInMonth!! < 4) {
                    break
                }
            }
        }
        return first
    }

    override fun toString(): String {
        val time = time.format(DateTimeFormatter.ofPattern("h:mm a"))
        var string = "On the "

        when (days.size) {
            1 -> string += days.first()
            2 -> string += "${days[0]} and ${days[1]}"
            else -> {
                for (i in 0..days.size - 2) {
                    string += "${days[i]}, "
                }
                string += "and ${days.last()}"
            }
        }
        string += if (frequency == 1) {
            " every month at $time"
        } else {
            " every $frequency months at $time"
        }
        return string

    }
    /**
     * [dayOfWeek] will be one of [Calendar]'s day of week constants, eg: [Calendar.TUESDAY].
     *
     * [dayOfWeekInMonth] will be a number from 1 to 5. 1 indicates first, 2 indicates second, etc.
     * For example, a day with [Calendar.MONDAY,3] represents the third monday of the month. Since a
     * given day of the week in a given month must occur at least 4 times but can go up to at most 5
     * times, 5 represent the last occurrence of [dayOfWeek] in the month.
     */
    data class Day(var dayOfWeekInMonth: Int = 1, var dayOfWeek: DayOfWeek = DayOfWeek.SUNDAY) : TemporalAdjuster, Comparable<Day> {
        override fun toString(): String {
            val number = when (dayOfWeekInMonth) {
                1 -> "1st"
                2 -> "2nd"
                3 -> "3rd"
                4 -> "4th"
                else -> "last"
            }
            return "$number ${dayOfWeek.getFullName()}"
        }

        override operator fun compareTo(other: Day): Int {
            if (this.dayOfWeekInMonth > other.dayOfWeekInMonth) return 1
            if (this.dayOfWeekInMonth < other.dayOfWeekInMonth) return -1
            if (this.dayOfWeek > other.dayOfWeek) return 1
            if (this.dayOfWeek < other.dayOfWeek) return -1
            return 0
        }

        override fun adjustInto(temporal: Temporal): Temporal {
            return if (dayOfWeekInMonth == 5) {
                temporal.with(TemporalAdjusters.lastDayOfMonth())
                    .with(TemporalAdjusters.previousOrSame(dayOfWeek))
            } else {
                val moo = temporal.with(TemporalAdjusters.dayOfWeekInMonth(dayOfWeekInMonth!!, dayOfWeek!!))
                moo
            }
        }
    }
}