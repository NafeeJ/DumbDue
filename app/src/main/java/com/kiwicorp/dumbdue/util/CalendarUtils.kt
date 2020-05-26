package com.kiwicorp.dumbdue.util

import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.ceil

//returns the suffix of number of the day
fun Calendar.daySuffix(): String {
    val dayOfMonth: Int = get(Calendar.DAY_OF_MONTH)
    return when {
        dayOfMonth.rem(10) == 1 && dayOfMonth != 11 -> "st"
        dayOfMonth.rem(10) == 2 && dayOfMonth != 12 -> "nd"
        dayOfMonth.rem(10) == 3 && dayOfMonth != 13 -> "rd"
        else -> "th"
    }
}

fun Calendar.timeFromNowMins() = ceil((timeInMillis - System.currentTimeMillis()).div(60000.0)).toInt()


fun Calendar.timeFromNowString(): String {
    val absTime = timeFromNowMins().absoluteValue
    return when {
        absTime == 0 -> { "0 Minutes" } //less than 1 minute
        absTime == 1 -> { "$absTime Minute" } //equal to 1 minute
        absTime < 60 -> { "$absTime Minutes" } //less than 1 hour
        absTime / 60 == 1 -> { "${absTime / 60} Hour"}//equal to 1 hour
        absTime / 60 < 24 -> { "${absTime / 60} Hours" } //less than 1 day
        absTime / 60 / 24 == 1 -> { "${absTime / 60 / 24} Day" } //equal to 1 day
        absTime / 60 / 24 < 7 -> { "${absTime / 60 / 24} Days" } //less than 1 week
        absTime / 60 / 24 / 7 == 1 -> { "${absTime / 60 / 24 / 7} Week" } //equal to 1 week
        absTime / 60 / 24 / 7 < 4 -> { "${absTime / 60 / 24 / 7} Weeks"  } //less than 1 month
        absTime / 60 / 24 / 7 / 4 == 1 -> { "${absTime / 60 / 24 / 7 / 4} Month" } //equal to 1 month
        absTime / 60 / 24 / 7 / 4 < 12 -> { "${absTime / 60 / 24 / 7 / 4} Months" } //less than one year
        absTime / 60 / 24 / 7 / 4 / 12 == 1 -> { "${absTime / 60 / 24 / 7 / 4 / 12} Year" } //equal to 1 year
        else -> "${absTime / 60 / 24 / 7 / 4 / 12} Years"
    }

}
