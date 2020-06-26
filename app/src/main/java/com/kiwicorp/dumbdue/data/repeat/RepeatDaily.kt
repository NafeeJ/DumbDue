package com.kiwicorp.dumbdue.data.repeat

import java.text.SimpleDateFormat
import java.util.*

/**
 * A class the represents a repeat interval that's scoped in days
 *
 * [frequency] will be the days users receive a reminder on
 */
data class RepeatDaily(override var frequency: Int, override var firstOccurrence: Calendar) : RepeatInterval(frequency, firstOccurrence) {

    override fun getNextOccurrence(): Calendar {
        return if (prevOccurrence == null) {
            prevOccurrence = firstOccurrence
            firstOccurrence
        } else {
            prevOccurrence = Calendar.getInstance().apply {
                timeInMillis = prevOccurrence!!.timeInMillis
                add(Calendar.DAY_OF_YEAR,frequency)
            }
            prevOccurrence!!
        }
    }
    
    override fun toString(): String {
        val time = SimpleDateFormat("h:mm a", Locale.US).format(firstOccurrence.time)

        return if (frequency == 1) {
            "Daily $time"
        } else {
            "Every $frequency days at $time"
        }
    }
    
}