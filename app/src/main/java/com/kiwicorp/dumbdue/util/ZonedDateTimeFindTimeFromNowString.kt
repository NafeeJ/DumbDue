package com.kiwicorp.dumbdue.util

import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit
import kotlin.math.absoluteValue

fun ZonedDateTime.timeFromNowString(abbreviateUnitName: Boolean): String {
    val names = if (abbreviateUnitName) {
        listOf("m","h","d","w","mo","yr")
    } else {
        listOf(" minutes"," hours"," days"," weeks"," months"," years")
    }
    val chronoUnitsToName = mapOf(
        ChronoUnit.MINUTES to names[0],
        ChronoUnit.HOURS to names[1],
        ChronoUnit.DAYS to names[2],
        ChronoUnit.WEEKS to names[3],
        ChronoUnit.MONTHS to names[4],
        ChronoUnit.YEARS to names[5]
    )
    val chronoUnits = chronoUnitsToName.keys.toList()

    var chronoUnit: ChronoUnit? = null
    // iterates through ChronoUnits until the difference between the time and now in the
    // given unit returns zero. When it does, assign the previous unit to chronoUnit.
    for (i in chronoUnits.indices) {
        if (chronoUnits[i].betweenRounded(this,ZonedDateTime.now()).absoluteValue <= 0) {
            chronoUnit = if (i == 0) ChronoUnit.MINUTES else chronoUnits[i - 1]
            break
        }
    }
    // if none of the units returns a difference of zero, assign chronoUnit to years
    if (chronoUnit == null) {
        chronoUnit = ChronoUnit.YEARS
    }
    val diff = chronoUnit.betweenRounded(this,ZonedDateTime.now().withSecond(0).withNano(0)).absoluteValue
    var name = chronoUnitsToName[chronoUnit]
    if (!abbreviateUnitName && diff == 1) {
        name = name!!.dropLast(1)
    }
    return "$diff$name"
}