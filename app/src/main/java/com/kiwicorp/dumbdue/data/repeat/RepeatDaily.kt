package com.kiwicorp.dumbdue.data.repeat

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*

/**
 * A class the represents a repeat interval that's scoped in days
 *
 * [frequency] will be the days users receive a reminder on
 */
data class RepeatDaily(override var frequency: Int, val startingDate: LocalDateTime) : RepeatInterval(frequency) {

    override fun getNextOccurrence(): Calendar {
        return if (prevOccurrence == null) {
            prevOccurrence = Calendar.getInstance().apply {
                set(startingDate.year,
                    startingDate.monthValue - 1, // -1 because Calendar.JANUARY starts at 0
                    startingDate.dayOfMonth,
                    startingDate.hour,
                    startingDate.minute)
            }
            prevOccurrence!!
        } else {
            prevOccurrence = Calendar.getInstance().apply {
                timeInMillis = prevOccurrence!!.timeInMillis
                add(Calendar.DAY_OF_YEAR,frequency)
            }
            prevOccurrence!!
        }
    }
    
    override fun toString(): String {
        val time = startingDate.toLocalTime().format(DateTimeFormatter.ofPattern("h:mm a"))

        return if (frequency == 1) {
            "Daily $time"
        } else {
            "Every $frequency days at $time"
        }
    }
    
}