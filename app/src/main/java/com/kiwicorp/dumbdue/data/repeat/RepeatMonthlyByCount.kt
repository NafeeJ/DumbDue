package com.kiwicorp.dumbdue.data.repeat

import java.text.SimpleDateFormat
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
data class RepeatMonthlyByCount(override var frequency: Int, override var firstOccurrence: Calendar, val days: List<Day>): RepeatInterval(frequency,firstOccurrence) {

    override fun getNextOccurrence(): Calendar {
        return if (prevOccurrence != null) {
            prevOccurrence = firstOccurrence
            firstOccurrence
        } else {
            // the next calendar
            var next: Calendar = Calendar.getInstance().apply {
                timeInMillis = Long.MAX_VALUE
            }
            // try to find the next day in the month from recurrence day
            for (day in days) {
                val currCalendar = Calendar.getInstance().apply {
                    timeInMillis = prevOccurrence!!.timeInMillis
                }
                currCalendar.set(Calendar.DAY_OF_WEEK,day.dayOfWeek!!)

                val dayOfWeekInMonth = if (day.dayOfWeekInMonth != 5) day.dayOfWeekInMonth else currCalendar.getActualMaximum(Calendar.DAY_OF_WEEK_IN_MONTH)
                currCalendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, dayOfWeekInMonth!!)
                // if currCalendar is after calendar and currCalendar is closer calendar than next is
                // assign next to currCalendar
                if (currCalendar.timeInMillis > prevOccurrence!!.timeInMillis && (currCalendar.timeInMillis - prevOccurrence!!.timeInMillis < next.timeInMillis - prevOccurrence!!.timeInMillis)) {
                    next = currCalendar
                }
            }
            // if could not find a calendar in this month that's after calendar, set next to be the
            // first day of recurrenceDays of the next month
            if (next.timeInMillis == Long.MAX_VALUE) {
                prevOccurrence!!.apply {
                    // must account for frequency
                    add(Calendar.MONTH, frequency)
                    // set week of month to 2 because if calendar is in the last/first week of the month,
                    // setting day of the week can move the day to a different month
                    set(Calendar.WEEK_OF_MONTH,2)
                }
                var first: Calendar = Calendar.getInstance().apply {
                    timeInMillis = prevOccurrence!!.timeInMillis
                    set(Calendar.DAY_OF_MONTH,getActualMaximum(Calendar.DAY_OF_MONTH))
                }
                // potentially must iterate through recurrenceDays again to find the first day of next
                // month because the last day in the month of a day of the week could come before the
                // fourth day in the month of another day of the week
                for (day in days) {
                    val currCalendar = Calendar.getInstance().apply {
                        timeInMillis = prevOccurrence!!.timeInMillis
                    }

                    currCalendar.set(Calendar.DAY_OF_WEEK,day.dayOfWeek!!)

                    val dayOfWeekInMonth = if (day.dayOfWeekInMonth != 5) day.dayOfWeekInMonth else currCalendar.getActualMaximum(Calendar.DAY_OF_WEEK_IN_MONTH)
                    currCalendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, dayOfWeekInMonth!!)

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
            prevOccurrence = next
            next
        }
    }

    override fun toString(): String {
        val time = SimpleDateFormat("h:mm a", Locale.US).format(firstOccurrence.time)
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
            " every $frequency month at $time"
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
    data class Day(var dayOfWeek: Int? = null, var dayOfWeekInMonth: Int? = null) {
        override fun toString(): String {
            val number = when(dayOfWeekInMonth) {
                1 -> "1st"
                2 -> "2nd"
                3 -> "3rd"
                4 -> "4th"
                else -> "last"
            }
            val day = when (dayOfWeek) {
                Calendar.MONDAY -> "Monday"
                Calendar.TUESDAY -> "Tuesday"
                Calendar.WEDNESDAY -> "Wednesday"
                Calendar.THURSDAY -> "Thursday"
                Calendar.FRIDAY -> "Friday"
                Calendar.SATURDAY -> "Saturday"
                else -> "Sunday"
            }
            return "$number $day"
        }
    }
}