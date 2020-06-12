package com.kiwicorp.dumbdue.timesetters

import java.util.*

class IncrementalTimeSetter {

    var number: Int

    /**
     * [unit] will be one of [Calendar]'s units eg: [Calendar.MINUTE]
     */
    var unit: Int

    val text: String
        get() {
            val unitString = when(unit) {
                Calendar.MINUTE -> "min"
                Calendar.HOUR -> "hr"
                Calendar.DAY_OF_YEAR -> "day"
                Calendar.WEEK_OF_YEAR -> "wk"
                Calendar.MONTH -> "mo"
                else -> "yr"
            }
            return "${if (number > 0) "+$number" else number} $unitString"
        }

    constructor(number: Int, unit: Int) {
        this.number = number
        this.unit = unit
    }

    constructor(text: String) {
        // number is the actual number of how much to increment/decrement, notDigits contains "+ unit"
        var (number,notDigits)= text.partition { it.isDigit() }
        val unit: Int = when (notDigits.substring(2)) {
            "min" -> Calendar.MINUTE
            "hr" -> Calendar.HOUR
            "day" -> Calendar.DAY_OF_YEAR
            "wk" -> Calendar.WEEK_OF_YEAR
            "mo" -> Calendar.MONTH
            else -> Calendar.YEAR
        }
        if (notDigits[0] == '-') number = "-$number"

        this.number = number.toInt()
        this.unit = unit
    }

    fun setTime(calendar: Calendar) {
        calendar.add(unit,number)
    }
}