package com.kiwicorp.dumbdue.data.repeat

import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter

/**
 * A class the represents a repeat interval that's scoped in days
 *
 * [frequency] will be the days users receive a reminder on
 */
class RepeatDailyInterval(frequency: Int, time: LocalTime, var startingDate: LocalDate) : RepeatInterval(frequency, time) {

    override fun getNextOccurrence(): ZonedDateTime {
        return if (prevOccurrence == null) {
            prevOccurrence = ZonedDateTime.of(LocalDateTime.of(startingDate, time), ZoneId.systemDefault())
            prevOccurrence!!
        } else {
            prevOccurrence = prevOccurrence!!.plusDays(frequency.toLong())
            prevOccurrence!!
        }
    }
    
    override fun toString(): String {
        val time = time.format(DateTimeFormatter.ofPattern("h:mm a"))

        return if (frequency == 1) {
            "Daily $time"
        } else {
            "Every $frequency days at $time"
        }
    }

}