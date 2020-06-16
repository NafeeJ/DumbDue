package com.kiwicorp.dumbdue.data.repeat

import java.text.SimpleDateFormat
import java.util.*

/**
 * A class the represents a repeat interval that's scoped in years and will be based off of the
 * month and the count of a given day of the week in the month eg: (second friday of June each year)
 *
 * [frequency] will be the years users receive a reminder on
 *
 * [dayOfWeekInMonth] will be a number from 1 to 5. 1 indicates first, 2 indicates second, etc.
 * Since a given day of the week in a given month must occur at least 4 times but can go up to at
 * most 5 times, 5 represent the last occurrence of the day of the week in the month.
 */
class RepeatYearlyByCount(override val frequency: Int, val dayOfWeekInMonth: Int): RepeatInterval {
    override fun getNextDueDate(calendar: Calendar): Calendar? {
        val returnCalendar = Calendar.getInstance().apply { timeInMillis = calendar.timeInMillis }

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        returnCalendar.add(Calendar.YEAR,frequency)
        returnCalendar.set(Calendar.WEEK_OF_MONTH,2)//in case changing day of week moves month to next month
        returnCalendar.set(Calendar.DAY_OF_WEEK,dayOfWeek)
        returnCalendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, if(dayOfWeekInMonth == 5) returnCalendar.getActualMaximum(Calendar.DAY_OF_WEEK_IN_MONTH) else dayOfWeekInMonth)

        return returnCalendar
    }

    override fun getText(calendar: Calendar): String {
        val time = SimpleDateFormat("h:mm a", Locale.US).format(calendar.time)

        val week = when(dayOfWeekInMonth) {
            1 -> "1st"
            2 -> "2nd"
            3 -> "3rd"
            4 -> "4th"
            else -> "last"
        }

        val month = when(calendar.get(Calendar.MONTH)) {
            Calendar.JANUARY -> "January"
            Calendar.FEBRUARY -> "February"
            Calendar.MARCH -> "March"
            Calendar.APRIL -> "April"
            Calendar.MAY -> "May"
            Calendar.JUNE -> "June"
            Calendar.JULY -> "July"
            Calendar.AUGUST -> "August"
            Calendar.SEPTEMBER -> "September"
            Calendar.OCTOBER -> "October"
            Calendar.NOVEMBER -> "November"
            else -> "December"
        }

        val dayOfWeek = when(calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            else -> "Sunday"
        }

        return "On the $week $dayOfWeek of $month at $time ${if (frequency == 1) "every year" else "every $frequency years"}"
    }
}