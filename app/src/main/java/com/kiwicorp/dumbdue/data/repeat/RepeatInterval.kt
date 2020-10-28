package com.kiwicorp.dumbdue.data.repeat

import org.threeten.bp.LocalTime
import org.threeten.bp.ZonedDateTime
import java.util.*

abstract class RepeatInterval(
    @Transient
    open val frequency: Int,
    @Transient
    open var time: LocalTime,
    val id: String = UUID.randomUUID().toString()
): Cloneable {
    fun getNextDueDate(currOccurrence: ZonedDateTime): ZonedDateTime{
        var nextOccurrence = getNextOccurrence()
        while(!nextOccurrence.isAfter(currOccurrence)) {
            nextOccurrence = getNextOccurrence()
        }
        return nextOccurrence
    }

    protected var prevOccurrence: ZonedDateTime? = null

    protected abstract fun getNextOccurrence(): ZonedDateTime

    public override fun clone(): Any {
        return super.clone()
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = frequency
        result = 31 * result + time.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + (prevOccurrence?.hashCode() ?: 0)
        return result
    }
}