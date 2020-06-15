package com.kiwicorp.dumbdue.data.repeat

import java.util.*

/**
 * A class the represents a repeat interval that's scoped in years and will be based off of the
 * month and the count of a given day of the week in the month eg: (second friday of June each year)
 *
 * [frequency] will be the years users receive a reminder on
 */
class RepeatYearlyByCount(override val frequency: Int): RepeatInterval {
    override fun getNextDueDate(calendar: Calendar): Calendar? {
        val returnCalendar = Calendar.getInstance().apply { timeInMillis = calendar.timeInMillis }

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val dayOfWeekInMonthCount = calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH)

        returnCalendar.add(Calendar.YEAR,frequency)
        returnCalendar.set(Calendar.DAY_OF_WEEK,dayOfWeek)
        returnCalendar.set(Calendar.DAY_OF_WEEK_IN_MONTH,dayOfWeekInMonthCount)

        return returnCalendar
    }
}