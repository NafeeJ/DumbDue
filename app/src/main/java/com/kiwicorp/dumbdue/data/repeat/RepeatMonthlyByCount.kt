package com.kiwicorp.dumbdue.data.repeat

import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import org.threeten.bp.YearMonth
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

data class RepeatMonthlyByCount(override var frequency: Int, var startingMonth: YearMonth, var time: LocalTime, val days: List<Day>): RepeatInterval(frequency) {
    override fun getNextOccurrence(): Calendar {
        // if getting first occurrence, set prevOccurrence to first day of this starting month and
        // function will take care of the rest
        if (prevOccurrence == null) {
            prevOccurrence = findFirstDayOfMonth(startingMonth)
        }
        var next: Calendar = Calendar.getInstance().apply {
            timeInMillis = Long.MAX_VALUE
        }
        // try to find the next day in the month from recurrence day
        for (day in days) {
            val currCalendar = Calendar.getInstance().apply {
                timeInMillis = prevOccurrence!!.timeInMillis
            }
            day.setCalendar(currCalendar)
            // if currCalendar is after calendar and currCalendar is closer calendar than next is,
            // assign next to currCalendar
            if (currCalendar.timeInMillis > prevOccurrence!!.timeInMillis && (currCalendar.timeInMillis - prevOccurrence!!.timeInMillis < next.timeInMillis - prevOccurrence!!.timeInMillis)) {
                next = currCalendar
            }
        }
        // if could not find a calendar in this month that's after calendar, move next month
        if (next.timeInMillis == Long.MAX_VALUE) {
            next = findFirstDayOfMonth(
                YearMonth.of(prevOccurrence!!.get(Calendar.YEAR),
                    prevOccurrence!!.get(Calendar.MONTH) + frequency) //account for frequency
            )
        }
        prevOccurrence = next
        return next
    }

    /**
     * returns the first day of [days] in the next month (this may change from month to month
     * because the last day in the month of a day of the week could come before the fourth day in
     * the month of another day of the week)
     */
    private fun findFirstDayOfMonth(yearMonth: YearMonth): Calendar {
        var first = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, this@RepeatMonthlyByCount.time.hour)
            set(Calendar.MINUTE, this@RepeatMonthlyByCount.time.minute)
            set(Calendar.YEAR, yearMonth.year)
            set(Calendar.MONTH, yearMonth.monthValue - 1) // -1 because Calendar.JANUARY starts at 0
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        }
        for (day in days) {
            val currCalendar = Calendar.getInstance().apply {
                timeInMillis = first.timeInMillis
                day.setCalendar(this)
            }
            if (currCalendar.timeInMillis < first.timeInMillis) {
                first = currCalendar
            }
            // break if dayOfWeekInMonth is less than four because we don't have to worry about
            // the inconsistency of fourth and last days in the month brought up above
            if (first.get(Calendar.DAY_OF_WEEK_IN_MONTH) < 4) {
                break
            }
        }
        return first
    }

    override fun toString(): String {
        val time = this.time.format(DateTimeFormatter.ofPattern("h:mm a"))
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

        fun setCalendar(calendar: Calendar) {
            // to make sure changing day of week doesn't move to another
            calendar.set(Calendar.WEEK_OF_MONTH,2)

            dayOfWeek?.let { calendar.set(Calendar.DAY_OF_WEEK,it) }
            dayOfWeekInMonth?.let {
                if (it < 5) {
                    calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, it)
                } else {
                    val lastDayOfWeekInMonth = calendar.getActualMaximum(Calendar.DAY_OF_WEEK_IN_MONTH)
                    calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, lastDayOfWeekInMonth)
                }
            }
        }
    }
}