package com.kiwicorp.dumbdue.data.repeat

import com.kiwicorp.dumbdue.util.isOnLastDayOfMonth
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
 * index 23 is true, it indicates the user wishes to receive a reminder on the 22rd day of the month.
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

}