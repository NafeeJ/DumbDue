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

fun Calendar.minsFromNow() = ceil((timeInMillis - System.currentTimeMillis()).div(60000.0)).toInt().absoluteValue

fun Calendar.timeFromNowString(): String {
    val minsFromNow = minsFromNow()
    return when {
        minsFromNow == 0 -> { "0 Minutes" } //less than 1 minute
        minsFromNow == 1 -> { "$minsFromNow Minute" } //equal to 1 minute
        minsFromNow < 60 -> { "$minsFromNow Minutes" } //less than 1 hour
        minsFromNow / 60 == 1 -> { "${minsFromNow / 60} Hour"}//equal to 1 hour
        minsFromNow / 60 < 24 -> { "${minsFromNow / 60} Hours" } //less than 1 day
        minsFromNow / 60 / 24 == 1 -> { "${minsFromNow / 60 / 24} Day" } //equal to 1 day
        minsFromNow / 60 / 24 < 7 -> { "${minsFromNow / 60 / 24} Days" } //less than 1 week
        minsFromNow / 60 / 24 / 7 == 1 -> { "${minsFromNow / 60 / 24 / 7} Week" } //equal to 1 week
        minsFromNow / 60 / 24 / 7 < 4 -> { "${minsFromNow / 60 / 24 / 7} Weeks"  } //less than 1 month
        minsFromNow / 60 / 24 / 7 / 4 == 1 -> { "${minsFromNow / 60 / 24 / 7 / 4} Month" } //equal to 1 month
        minsFromNow / 60 / 24 / 7 / 4 < 12 -> { "${minsFromNow / 60 / 24 / 7 / 4} Months" } //less than one year
        minsFromNow / 60 / 24 / 7 / 4 / 12 == 1 -> { "${minsFromNow / 60 / 24 / 7 / 4 / 12} Year" } //equal to 1 year
        else -> "${minsFromNow / 60 / 24 / 7 / 4 / 12} Years"
    }
}

fun Calendar.isOverdue() = timeInMillis - System.currentTimeMillis() < 0