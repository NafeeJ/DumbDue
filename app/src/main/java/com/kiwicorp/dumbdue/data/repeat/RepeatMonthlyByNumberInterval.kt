package com.kiwicorp.dumbdue.data.repeat

import com.kiwicorp.dumbdue.util.getDaySuffix
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoField
import org.threeten.bp.temporal.TemporalAdjusters
import java.time.LocalDateTime
import java.util.*

/**
 * A class that represents a repeat interval that's scoped in a month and will be based off of
 * the number of a given day in the month eg: (15th each month)
 *
 * [frequency] will be which months will be months users receive reminders on
 *
 * [days] will be a list of the days in a month that the reminder will repeat on. Range is 1-32. 32
 * indicates the last day of the month.
 */
class RepeatMonthlyByNumberInterval(frequency: Int, startingYearMonth: YearMonth, time: LocalTime, val days: List<Int>): RepeatMonthlyInterval(frequency, time, startingYearMonth) {

    override fun getNextOccurrence(): ZonedDateTime {
        return if (prevOccurrence == null) {
            val firstDay = days.first()

           val date = if (firstDay != 32) {
                LocalDate.of(startingYearMonth.year, startingYearMonth.month,firstDay)
            } else {
                LocalDate.of(startingYearMonth.year, startingYearMonth.month,1)
                    .with(TemporalAdjusters.lastDayOfMonth())
            }
            prevOccurrence = ZonedDateTime.of(date,time, ZoneId.systemDefault())
            prevOccurrence!!
        } else {
            val currDay = prevOccurrence!!.dayOfMonth
            val nextDay = days.firstOrNull { it > currDay } ?: days.first()
            // if going into next month account for frequency
            if (nextDay == days.first()) {
                prevOccurrence = prevOccurrence!!.plusMonths(frequency.toLong())
            }
            prevOccurrence = if (nextDay == 32) {
                prevOccurrence!!.with(TemporalAdjusters.lastDayOfMonth())
            } else {
                prevOccurrence!!.withDayOfMonth(nextDay)
            }
            prevOccurrence!!
        }
    }

    override fun toString(): String {
        val time = this.time.format(DateTimeFormatter.ofPattern("h:mm a"))

        var string = "On the "

        when (days.size) {
            1 -> {
                string += if (days.last() == 32) {
                    "last day "
                } else {
                    "${getDaySuffixString(days.first())} "
                }
            }
            2 -> {
                string += "${getDaySuffixString(days[0])} and "
                string += if (days.last() == 32) {
                    "last day "
                } else {
                    "${getDaySuffixString(days[1])} "
                }
            }
            else -> {
                for (i in 0..days.size - 2) {
                    string += "${getDaySuffixString(days[i])}, "
                }
                string += if (days.last() == 32) "and last day " else "and ${getDaySuffixString(days.last())} "
            }
        }

        if (frequency == 1) {
            string += "every month at "
        } else if (frequency == 2) {
            string += "every $frequency months at "
        }
        string += time

        return string
    }

    private fun getDaySuffixString(dayOfMonth: Int): String {
        return "${dayOfMonth}${getDaySuffix(dayOfMonth)}"
    }
}