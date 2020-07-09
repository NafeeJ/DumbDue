package com.kiwicorp.dumbdue.data.repeat

import org.threeten.bp.LocalTime
import org.threeten.bp.Year

abstract class RepeatYearlyInterval(frequency: Int, time: LocalTime, val startingYear: Year) : RepeatInterval(frequency, time)