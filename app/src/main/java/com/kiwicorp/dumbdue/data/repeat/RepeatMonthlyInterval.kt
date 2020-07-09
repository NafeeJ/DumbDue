package com.kiwicorp.dumbdue.data.repeat

import org.threeten.bp.LocalTime
import org.threeten.bp.YearMonth

abstract class RepeatMonthlyInterval(frequency: Int, time: LocalTime, val startingYearMonth: YearMonth) : RepeatInterval(frequency, time) {

}