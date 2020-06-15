package com.kiwicorp.dumbdue.data.repeat

import java.util.*

/**
 * A class the represents a repeat interval that is not repeating
 */
class RepeatNone(override val frequency: Int) : RepeatInterval {
    override fun getNextDueDate(calendar: Calendar): Calendar? {
        return null
    }
}