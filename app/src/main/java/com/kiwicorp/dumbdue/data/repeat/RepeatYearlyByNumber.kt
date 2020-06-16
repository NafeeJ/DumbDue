package com.kiwicorp.dumbdue.data.repeat

import java.text.SimpleDateFormat
import java.util.*

/**
 * A class the represents a repeat interval that's scoped in years and will be based off of the
 * month and the number of the a given day in the month eg: (June 13th each year)
 *
 * [frequency] will be the years users receive a reminder on
 */
class RepeatYearlyByNumber(override val frequency: Int) : RepeatInterval {

    override fun getNextDueDate(calendar: Calendar): Calendar? {
        return Calendar.getInstance().apply {
            timeInMillis = calendar.timeInMillis
            add(Calendar.YEAR,frequency)
        }
    }

    override fun getText(calendar: Calendar): String {
        val dateAndTime = SimpleDateFormat("MMMM d, h:mm a", Locale.US).format(calendar.time)
        return if (frequency == 1) "Every $dateAndTime" else "$dateAndTime every $frequency years"
    }
}