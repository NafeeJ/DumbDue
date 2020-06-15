package com.kiwicorp.dumbdue.data.repeat

import com.kiwicorp.dumbdue.util.isOnLastDayOfWeekInTheMonth
import java.text.SimpleDateFormat
import java.util.*

/**
 * A class that represents a repeat interval that's scoped in a month and will be based off of
 * the count of a given day of the week in the month eg: (second friday each month)
 *
 * [frequency] will be which months will be months users receive reminders on
 *
 * [recurrenceDays] will be a sorted list of pairs of ints where each pair represents a day in the
 * month that the user wishes to repeat the reminder on. The first int represents a day in the week
 * and will be one of [Calendar]'s day of week constants, eg: [Calendar.TUESDAY]. The second int
 * represents the count of that day and will be a number from 1 to 5. 1 indicates first, 2 indicates
 * second, etc. For example, a pair of <Calendar.MONDAY,3> indicates the user wants to repeat the
 * reminder on the third monday of the month. Since a given day of the week in a given month must
 * occur at least 4 times but can go up to at most 5 times, 5 will indicate the user wishes to
 * receive a reminder on the last occurrence of the given day of the week in the month.
 */
class RepeatMonthlyByCount(override val frequency: Int, val recurrenceDays: List<Pair<Int,Int>>) : RepeatInterval {

    private val dateFormatter = SimpleDateFormat("MMM d YYYY, h:mm a")

    override fun getNextDueDate(calendar: Calendar): Calendar? {
        // the next calendar
        var next: Calendar = Calendar.getInstance().apply {
            timeInMillis = Long.MAX_VALUE
        }
        for (day in recurrenceDays) {
            val currCalendar = Calendar.getInstance().apply {
                timeInMillis = calendar.timeInMillis
            }
            currCalendar.set(Calendar.DAY_OF_WEEK,day.first)

            val dayOfWeekInMonth = if (day.second != 5) day.second else currCalendar.getActualMaximum(Calendar.DAY_OF_WEEK_IN_MONTH)
            currCalendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, dayOfWeekInMonth)
            // if currCalendar is after calendar and currCalendar is closer calendar than next is\
            // assign next to currCalendar
            if (currCalendar.timeInMillis > calendar.timeInMillis && (currCalendar.timeInMillis - calendar.timeInMillis < next.timeInMillis - calendar.timeInMillis)) {
                next = currCalendar
            }
        }
        // if could not find a calendar in this month that's after calendar, set next to be the
        // first day of recurrenceDays of the next month
        if (next.timeInMillis == Long.MAX_VALUE) {
            calendar.apply {
                // must account for frequency
                add(Calendar.MONTH, frequency)
                // set week of month to 2 because if calendar is in the last/first week of the month,
                // setting day of the week can move the day to a different month
                set(Calendar.WEEK_OF_MONTH,2)
            }
            var first: Calendar = Calendar.getInstance().apply {
                timeInMillis = calendar.timeInMillis
                set(Calendar.DAY_OF_MONTH,getActualMaximum(Calendar.DAY_OF_MONTH))
            }
            // potentially must iterate through recurrenceDays again to find the first day of next
            // month because the last day in the month of a day of the week could come before the
            // fourth day in the month of another day of the week
            for (day in recurrenceDays) {
                val currCalendar = Calendar.getInstance().apply {
                    timeInMillis = calendar.timeInMillis
                }

                currCalendar.set(Calendar.DAY_OF_WEEK,day.first)

                val dayOfWeekInMonth = if (day.second != 5) day.second else currCalendar.getActualMaximum(Calendar.DAY_OF_WEEK_IN_MONTH)
                currCalendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, dayOfWeekInMonth)

                if (currCalendar.timeInMillis < first.timeInMillis) {
                    first = currCalendar
                }
                // break if dayOfWeekInMonth is less than four because we don't have to worry about
                // the inconsistency of fourth and last days in the month brought up above
                if (first.get(Calendar.DAY_OF_WEEK_IN_MONTH) < 4) {
                    break
                }
            }
            next = first
        }
        return next
    }
}