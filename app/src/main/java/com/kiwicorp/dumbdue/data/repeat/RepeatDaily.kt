package com.kiwicorp.dumbdue.data.repeat

import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

/**
 * A class the represents a repeat interval that's scoped in days
 *
 * [frequency] will be the days users receive a reminder on
 */
class RepeatDaily(frequency: Int, val startingDateTime: LocalDateTime) : RepeatInterval(frequency) {

    override fun getNextOccurrence(): ZonedDateTime {
        return if (prevOccurrence == null) {
            prevOccurrence = ZonedDateTime.of(startingDateTime, ZoneId.systemDefault())
            prevOccurrence!!
        } else {
            prevOccurrence = prevOccurrence!!.plusDays(frequency.toLong())
            prevOccurrence!!
        }
    }
    
    override fun toString(): String {
        val time = startingDateTime.toLocalTime().format(DateTimeFormatter.ofPattern("h:mm a"))

        return if (frequency == 1) {
            "Daily $time"
        } else {
            "Every $frequency days at $time"
        }
    }
    
}