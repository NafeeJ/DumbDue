package com.kiwicorp.dumbdue.data.repeat

import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalTime
import org.threeten.bp.Year
import org.threeten.bp.format.DateTimeFormatter
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
 *
 */
data class RepeatYearlyByCount(override var frequency: Int, val startingYear: Int, val month: Int,var dayOfWeek: Int, var dayOfWeekInMonth: Int, val time: LocalTime): RepeatInterval(frequency) {

    override fun getNextOccurrence(): Calendar {
        return if (prevOccurrence == null) {
            prevOccurrence = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, this@RepeatYearlyByCount.time.hour)
                set(Calendar.MINUTE, this@RepeatYearlyByCount.time.minute)
                set(Calendar.MONTH, month)
                set(Calendar.YEAR, startingYear)
                set(Calendar.WEEK_OF_MONTH,2)
                set(Calendar.DAY_OF_WEEK, dayOfWeek)
                if (dayOfWeekInMonth == 5) {
                    set(Calendar.DAY_OF_WEEK_IN_MONTH, getActualMaximum(Calendar.DAY_OF_WEEK_IN_MONTH))
                } else {
                    dayOfWeekInMonth
                }
            }
            prevOccurrence!!
        } else {
            val next = Calendar.getInstance().apply { timeInMillis = prevOccurrence!!.timeInMillis }

            val dayOfWeek = prevOccurrence!!.get(Calendar.DAY_OF_WEEK)

            next.add(Calendar.YEAR,frequency)
            next.set(Calendar.WEEK_OF_MONTH,2)//in case changing day of week moves month to next month
            next.set(Calendar.DAY_OF_WEEK,dayOfWeek)
            next.set(Calendar.DAY_OF_WEEK_IN_MONTH, if(dayOfWeekInMonth == 5) next.getActualMaximum(Calendar.DAY_OF_WEEK_IN_MONTH) else dayOfWeekInMonth)

            prevOccurrence = next
            next
        }
    }

    override fun toString(): String {
        val time = this.time.format(DateTimeFormatter.ofPattern("h:mm a"))

        val week = when(dayOfWeekInMonth) {
            1 -> "1st"
            2 -> "2nd"
            3 -> "3rd"
            4 -> "4th"
            else -> "last"
        }

        val monthStr = when(month) {
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

        val dayOfWeekStr = when(dayOfWeek) {
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            else -> "Sunday"
        }

        return "On the $week $dayOfWeekStr of $monthStr at $time ${if (frequency == 1) "every year" else "every $frequency years"}"
    }
}