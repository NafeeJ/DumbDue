package com.kiwicorp.dumbdue.data.repeat

import java.util.*

abstract class RepeatInterval(open var frequency: Int) {
    fun getNextDueDate(calendar: Calendar): Calendar {
        var nextOccurrence = getNextOccurrence()
        while(calendar >= nextOccurrence) {
            nextOccurrence = getNextOccurrence()
        }
        return nextOccurrence
    }
    protected var prevOccurrence: Calendar? = null
    protected abstract fun getNextOccurrence(): Calendar
}