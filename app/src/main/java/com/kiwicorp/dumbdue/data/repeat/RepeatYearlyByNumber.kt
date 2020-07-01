package com.kiwicorp.dumbdue.data.repeat

import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

/**
 * A class the represents a repeat interval that's scoped in years and will be based off of the
 * month and the number of the a given day in the month eg: (June 13th each year)
 *
 * [frequency] will be the years users receive a reminder on
 * [month] is the month to repeat on and will be on of [Calendar]'s constants eg: [Calendar.JUNE]
 * [day] is the day of the month to repeat on
 */
class RepeatYearlyByNumber(override var frequency: Int, val startingDateTime: LocalDateTime) : RepeatInterval(frequency) {

    override fun getNextOccurrence(): Calendar {
        return if (prevOccurrence == null) {
            prevOccurrence = Calendar.getInstance().apply {
                set(Calendar.YEAR, startingDateTime.year)
                set(Calendar.MONTH, startingDateTime.monthValue - 1)
                set(Calendar.DAY_OF_MONTH, startingDateTime.dayOfMonth)
                set(Calendar.HOUR_OF_DAY, startingDateTime.hour)
                set(Calendar.MINUTE, startingDateTime.minute)
            }
            prevOccurrence!!
        } else {
            val next = Calendar.getInstance().apply { timeInMillis = prevOccurrence!!.timeInMillis }

            if (prevOccurrence!!.get(Calendar.MONTH) == startingDateTime.dayOfMonth && prevOccurrence!!.get(Calendar.DAY_OF_MONTH) == startingDateTime.dayOfMonth) {
                next.add(Calendar.YEAR,frequency)
            } else {
                // calendar with the day the reminder is supposed to repeat on this year
                val thisYearRepeatCalendar = Calendar.getInstance().apply {
                    timeInMillis = prevOccurrence!!.timeInMillis
                    set(Calendar.MONTH,startingDateTime.monthValue - 1)
                    set(Calendar.DAY_OF_MONTH,startingDateTime.dayOfMonth)
                }
                if (thisYearRepeatCalendar.timeInMillis < next.timeInMillis) {
                    next.add(Calendar.YEAR,frequency)
                }
                next.apply {
                    set(Calendar.MONTH,startingDateTime.monthValue - 1)
                    set(Calendar.DAY_OF_MONTH,startingDateTime.dayOfMonth)
                }
            }
            prevOccurrence = next
            next
        }
    }

    override fun toString(): String {
        val dateAndTime = startingDateTime.format(DateTimeFormatter.ofPattern("MMMM d, h:mm a"))
        return if (frequency == 1) "Every $dateAndTime" else "$dateAndTime every $frequency years"
    }
}