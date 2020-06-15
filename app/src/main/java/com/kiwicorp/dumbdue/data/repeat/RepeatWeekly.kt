package com.kiwicorp.dumbdue.data.repeat

import java.util.*

/**
 * A class that represents a repeat interval that's scoped in a week
 *
 * [frequency] will be which weeks users will receive reminders on
 *
 * [recurringDays] is a sorted list of [Calendar]'s constants eg: [Calendar.TUESDAY]. It represents
 * the days the user wishes to receive reminders on. The list should never have a size of more than 7.
 * Sunday is considered to be the first day.
 */
class RepeatWeekly(override val frequency: Int, val recurringDays: List<Int>): RepeatInterval {

    override fun getNextDueDate(calendar: Calendar): Calendar? {
        val returnCalendar = Calendar.getInstance().apply { timeInMillis = calendar.timeInMillis }
        // get next day
        val indexOfToday = recurringDays.indexOf(calendar.get(Calendar.DAY_OF_WEEK))
        val indexOfNextDay = if (indexOfToday + 1 > recurringDays.lastIndex) 0 else indexOfToday + 1
        val nextDay = recurringDays[indexOfNextDay]

        returnCalendar.add(Calendar.DAY_OF_YEAR,1)
        // add days to returnCalendar until returnCalendar's day of week is nextDay
        while (returnCalendar.get(Calendar.DAY_OF_WEEK) != nextDay) {
            returnCalendar.add(Calendar.DAY_OF_YEAR,1)
        }
        // if returnCalendar goes into a new week, account for the frequency
        if (returnCalendar.get(Calendar.DAY_OF_WEEK) == recurringDays[0]) {
            returnCalendar.add(Calendar.WEEK_OF_YEAR,frequency - 1)
        }

        return returnCalendar
    }
}