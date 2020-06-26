package com.kiwicorp.dumbdue.data.repeat

import com.kiwicorp.dumbdue.util.getDaySuffix
import java.text.SimpleDateFormat
import java.util.*

/**
 * A class that represents a repeat interval that's scoped in a month and will be based off of
 * the number of a given day in the month eg: (15th each month)
 *
 * [frequency] will be which months will be months users receive reminders on
 *
 * [days] will be a list of the days in the month that the reminder will repeat on
 */
data class RepeatMonthlyByNumber(override var frequency: Int, override var firstOccurrence: Calendar, val days: List<Int>): RepeatInterval(frequency, firstOccurrence) {

    override fun getNextOccurrence(): Calendar {
        return if (prevOccurrence == null) {
            prevOccurrence = firstOccurrence
            firstOccurrence
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
        val time = SimpleDateFormat("h:mm a", Locale.US).format(firstOccurrence.time)

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
            string += "every $frequency month at "
        }
        string += time

        return string
    }

    private fun getDaySuffixString(dayOfMonth: Int): String {
        return "${dayOfMonth}${getDaySuffix(dayOfMonth)}"
    }
}