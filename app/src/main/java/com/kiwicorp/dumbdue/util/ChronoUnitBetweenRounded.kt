package com.kiwicorp.dumbdue.util

import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit
import kotlin.math.roundToInt

/**
 * Returns the time between two ZonedDateTimes in the unit provided rounded to the nearest whole number
 * ([ChronoUnit.between] truncates/rounds down)
 */
fun ChronoUnit.betweenRounded(zonedDateTime1: ZonedDateTime, zonedDateTime2: ZonedDateTime): Int {
    val diff = zonedDateTime1.toInstant().epochSecond - zonedDateTime2.toInstant().epochSecond
    return (diff / this.duration.seconds.toDouble()).roundToInt()
}