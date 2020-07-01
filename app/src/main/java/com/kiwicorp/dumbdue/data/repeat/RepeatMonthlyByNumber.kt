package com.kiwicorp.dumbdue.data.repeat

import com.kiwicorp.dumbdue.util.getDaySuffix
import org.threeten.bp.LocalTime
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

/**
 * A class that represents a repeat interval that's scoped in a month and will be based off of
 * the number of a given day in the month eg: (15th each month)
 *
 * [frequency] will be which months will be months users receive reminders on
 *
 * [days] will be a list of the days in the month that the reminder will repeat on
 */
data class RepeatMonthlyByNumber(override var frequency: Int, var startingYearMonth: YearMonth, var time: LocalTime, val days: List<Int>): RepeatInterval(frequency) {

    override fun getNextOccurrence(): Calendar {
        return if (prevOccurrence == null) {
            prevOccurrence = Calendar.getInstance().apply {
                set(startingYearMonth.year, startingYearMonth.monthValue - 1) // -1 because Calendar.JANUARY starts at 0
                set(Calendar.HOUR_OF_DAY, this@RepeatMonthlyByNumber.time.hour)
                set(Calendar.MINUTE, this@RepeatMonthlyByNumber.time.minute)

                if (days[0] != 32) {
                    set(Calendar.DAY_OF_MONTH, days[0])
                } else {
                    set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                }
            }
            prevOccurrence!!
        } else {
            prevOccurrence = Calendar.getInstance().apply {
                timeInMillis = prevOccurrence!!.timeInMillis
                // get next day
                val currDay = prevOccurrence!!.get(Calendar.DAY_OF_MONTH)
                var nextDay: Int = days.firstOrNull { it >  currDay} ?: days.first()
                // if going to next month, account for frequency
                if (nextDay == days.first()) {
                    add(Calendar.MONTH, frequency)
                    set(Calendar.DAY_OF_MONTH, 1)
                }
                // if next day is last day, set next day to the correct day
                if (nextDay == 32) {
                    nextDay = getActualMaximum(Calendar.DAY_OF_MONTH)
                }
                // add days until day of month is nextDay
                while (get(Calendar.DAY_OF_MONTH) != nextDay) {
                    add(Calendar.DAY_OF_YEAR,1)
                }
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