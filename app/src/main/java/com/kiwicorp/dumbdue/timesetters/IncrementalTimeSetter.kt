package com.kiwicorp.dumbdue.timesetters

import org.threeten.bp.temporal.*

class IncrementalTimeSetter : TemporalAdjuster {

    var number: Long

    var unit : ChronoUnit

    constructor(number: Long, unit: ChronoUnit) {
        this.number = number
        this.unit = unit
    }

    constructor(text: String) {
        // number is the actual number of how much to increment/decrement, notDigits contains "+ unit"
        var (number,notDigits)= text.partition { it.isDigit() }
        val unit = when (notDigits.substring(2)) {
            "min" -> ChronoUnit.MINUTES
            "hr" -> ChronoUnit.HOURS
            "day" -> ChronoUnit.DAYS
            "wk" -> ChronoUnit.WEEKS
            "mo" -> ChronoUnit.MONTHS
            else -> ChronoUnit.YEARS
        }
        if (notDigits[0] == '-') number = "-$number"

        this.number = number.toLong()
        this.unit = unit
    }

    override fun toString(): String {
        val unitString = when(unit) {
            ChronoUnit.MINUTES -> "min"
            ChronoUnit.HOURS -> "hr"
            ChronoUnit.DAYS -> "day"
            ChronoUnit.WEEKS -> "wk"
            ChronoUnit.MONTHS-> "mo"
            else -> "yr"
        }
        return "${if (number > 0) "+$number" else number.toString()} $unitString"
    }

    override fun adjustInto(temporal: Temporal): Temporal {
        return temporal.plus(number,unit)
    }
}