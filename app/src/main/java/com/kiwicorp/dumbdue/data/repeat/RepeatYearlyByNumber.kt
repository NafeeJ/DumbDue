package com.kiwicorp.dumbdue.data.repeat

import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

/**
 * A class the represents a repeat interval that's scoped in years and will be based off of the
 * month and the number of the a given day in the month eg: (June 13th each year)
 *
 * [frequency] will be the years users receive a reminder on
 */
class RepeatYearlyByNumber(frequency: Int, val startingDateTime: LocalDateTime) : RepeatInterval(frequency) {

    override fun getNextOccurrence(): ZonedDateTime {
        return if (prevOccurrence == null) {
            prevOccurrence = ZonedDateTime.of(startingDateTime, ZoneId.systemDefault())
            prevOccurrence!!
        } else {
            prevOccurrence = prevOccurrence!!.plusYears(frequency.toLong())
            prevOccurrence!!
        }
    }

    override fun toString(): String {
        val dateAndTime = startingDateTime.format(DateTimeFormatter.ofPattern("MMMM d, h:mm a"))
        return if (frequency == 1) "Every $dateAndTime" else "$dateAndTime every $frequency years"
    }
}