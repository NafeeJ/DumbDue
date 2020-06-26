package com.kiwicorp.dumbdue.data.repeat

import java.text.SimpleDateFormat
import java.util.*

/**
 * A class the represents a repeat interval that's scoped in years and will be based off of the
 * month and the number of the a given day in the month eg: (June 13th each year)
 *
 * [frequency] will be the years users receive a reminder on
 * [month] is the month to repeat on and will be on of [Calendar]'s constants eg: [Calendar.JUNE]
 * [day] is the day of the month to repeat on
 */
class RepeatYearlyByNumber(override var frequency: Int, override var firstOccurrence: Calendar) : RepeatInterval(frequency, firstOccurrence) {

    override fun getNextOccurrence(): Calendar {
        return if (prevOccurrence == null) {
            prevOccurrence = firstOccurrence
            firstOccurrence
        } else {
            val next = Calendar.getInstance().apply { timeInMillis = prevOccurrence!!.timeInMillis }

            val month = firstOccurrence.get(Calendar.MONTH)
            val day = firstOccurrence.get(Calendar.DAY_OF_MONTH)

            if (prevOccurrence!!.get(Calendar.MONTH) == month && prevOccurrence!!.get(Calendar.DAY_OF_MONTH) == day) {
                next.add(Calendar.YEAR,frequency)
            } else {
                // calendar with the day the reminder is supposed to repeat on this year
                val thisYearRepeatCalendar = Calendar.getInstance().apply {
                    timeInMillis = prevOccurrence!!.timeInMillis
                    set(Calendar.MONTH,month)
                    set(Calendar.DAY_OF_MONTH,day)
                }
                if (thisYearRepeatCalendar.timeInMillis < next.timeInMillis) {
                    next.add(Calendar.YEAR,frequency)
                }
                next.apply {
                    set(Calendar.MONTH,month)
                    set(Calendar.DAY_OF_MONTH,day)
                }
            }
            prevOccurrence = next
            next
        }
    }

    override fun toString(): String {
        val dateAndTime = SimpleDateFormat("MMMM d, h:mm a", Locale.US).format(firstOccurrence.time)
        return if (frequency == 1) "Every $dateAndTime" else "$dateAndTime every $frequency years"
    }
}