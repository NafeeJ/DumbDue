package com.kiwicorp.dumbdue.data.repeat

import java.text.SimpleDateFormat
import java.util.*

/**
 * A class the represents a repeat interval that is not repeating
 */
class RepeatNone(override val frequency: Int) : RepeatInterval {
    override fun getNextDueDate(calendar: Calendar): Calendar? {
        return null
    }

    override fun getText(calendar: Calendar): String {
        return SimpleDateFormat("MMM d, h:mm a", Locale.US).format(calendar.time)
    }
}