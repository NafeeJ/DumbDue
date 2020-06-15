package com.kiwicorp.dumbdue.data.repeat

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

}