package com.kiwicorp.dumbdue.data.repeat

import java.text.SimpleDateFormat
import java.util.*

/**
 * A class the represents a repeat interval that's scoped in days
 *
 * [frequency] will be the days users receive a reminder on
 */
class RepeatDaily(override val frequency: Int) : RepeatInterval {

    override fun getNextDueDate(calendar: Calendar): Calendar? {
        return Calendar.getInstance().apply {
            timeInMillis = calendar.timeInMillis
            add(Calendar.DAY_OF_YEAR,frequency)
        }
    }

    override fun getText(calendar: Calendar): String {
        val time = SimpleDateFormat("h:mm a", Locale.US).format(calendar.time)

        return if (frequency == 1) {
            "Daily $time"
        } else {
            "Every $frequency days at $time"
        }

    }

}