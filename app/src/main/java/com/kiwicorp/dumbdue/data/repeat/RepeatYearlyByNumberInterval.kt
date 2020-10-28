package com.kiwicorp.dumbdue.data.repeat

import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter

/**
 * A class the represents a repeat interval that's scoped in years and will be based off of the
 * month and the number of the a given day in the month eg: (June 13th each year)
 *
 * [frequency] will be the years users receive a reminder on
 */
data class RepeatYearlyByNumberInterval(
    override val frequency: Int,
    override var time: LocalTime,
    val startingDate: LocalDate
): RepeatYearlyInterval(frequency, time, Year.of(startingDate.year)) {

    override fun getNextOccurrence(): ZonedDateTime {
        return if (prevOccurrence == null) {
            prevOccurrence = ZonedDateTime.of(LocalDateTime.of(startingDate, time), ZoneId.systemDefault())
            prevOccurrence!!
        } else {
            prevOccurrence = prevOccurrence!!.plusYears(frequency.toLong())
            prevOccurrence!!
        }
    }

    override fun toString(): String {
        val dateAndTime = LocalDateTime.of(startingDate, time).format(DateTimeFormatter.ofPattern("MMMM d, h:mm a"))
        return if (frequency == 1) "Every $dateAndTime" else "$dateAndTime every $frequency years"
    }
}