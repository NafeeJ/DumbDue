package com.kiwicorp.dumbdue.data.repeat

import com.kiwicorp.dumbdue.util.getDaySuffix
import com.kiwicorp.dumbdue.util.isOnLastDayOfMonth
import java.text.SimpleDateFormat
import java.util.*

/**
 * A class that represents a repeat interval that's scoped in a month and will be based off of
 * the number of a given day in the month eg: (15th each month)
 *
 * [frequency] will be which months will be months users receive reminders on
 *
 * [recurrenceDays] will a list of 32 booleans with each boolean representing a day in a month.
 * There is an extra boolean to represent the last day of the month. That boolean will be at index 0.
 * If an index is true, it means the user wishes to receive a reminder on that day. For example, if
 * index 23 is true, it indicates the user wishes to receive a reminder on the 23rd day of the month.
 */
class RepeatMonthlyByNumber(override val frequency: Int, val recurrenceDays: List<Boolean>): RepeatInterval {

    override fun getNextDueDate(calendar: Calendar): Calendar? {
        val returnCalendar = Calendar.getInstance().apply { timeInMillis = calendar.timeInMillis }
        returnCalendar.add(Calendar.DAY_OF_YEAR,1)
        // add days until the returnCalendar's day of month corresponds to a day that is true
        while(!recurrenceDays[returnCalendar.get(Calendar.DAY_OF_MONTH)]) {
            // break if return calendar has reached the end of the month and if 0 index is true
            if (returnCalendar.isOnLastDayOfMonth() && recurrenceDays[0]) {
                break
            }
            returnCalendar.add(Calendar.DAY_OF_YEAR,1)
        }

        val firstDay: Int = getFirstDay(recurrenceDays)
        // if the returnCalendar entered a new month, account for the frequency
        if (returnCalendar.get(Calendar.DAY_OF_MONTH) == firstDay || (returnCalendar.isOnLastDayOfMonth() && firstDay == 0)) {
            returnCalendar.add(Calendar.MONTH, frequency - 1)
        }

        return returnCalendar
    }

    private fun getFirstDay(days: List<Boolean>): Int {
        for (i in days.indices) {
            if (days[i] && i != 0) {
                return i
            }
        }
        if (days[0]) return 0
        return -1
    }

    override fun getText(calendar: Calendar): String {
        val time = SimpleDateFormat("h:mm a", Locale.US).format(calendar.time)

        var string = "On the "

        val daysOfMonth = mutableListOf<Int>()

        for (i in recurrenceDays.indices) {
            if (recurrenceDays[i]) {
                daysOfMonth.add(i)
            }
        }

        if (daysOfMonth.size == 1) {
            string += if (daysOfMonth.first() == 0) {
                "last day "
            } else {
                "${getDaySuffixString(daysOfMonth.first())} "
            }
        } else if (daysOfMonth.size == 2) {
            string += "${getDaySuffixString(daysOfMonth[0])} and ${getDaySuffixString(daysOfMonth[1])} "
        } else {
            if (daysOfMonth[0] == 0) {
                for (i in 1 until daysOfMonth.size) {
                    string += "${getDaySuffixString(daysOfMonth[i])}, "
                }
                string += "and last day "
            } else {
                for (i in 0..daysOfMonth.size - 2) {
                    string += "${getDaySuffixString(daysOfMonth[i])}, "
                }
                string += "and ${getDaySuffixString(daysOfMonth.last())} "
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