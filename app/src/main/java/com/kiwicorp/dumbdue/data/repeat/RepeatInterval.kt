package com.kiwicorp.dumbdue.data.repeat

import org.threeten.bp.ZonedDateTime

abstract class RepeatInterval(open var frequency: Int) {
    fun getNextDueDate(currOccurrence: ZonedDateTime): ZonedDateTime{
        var nextOccurrence = getNextOccurrence()
        while(!nextOccurrence.isAfter(currOccurrence)) {
            nextOccurrence = getNextOccurrence()
        }
        return nextOccurrence
    }
    protected var prevOccurrence: ZonedDateTime? = null
    protected abstract fun getNextOccurrence(): ZonedDateTime
}