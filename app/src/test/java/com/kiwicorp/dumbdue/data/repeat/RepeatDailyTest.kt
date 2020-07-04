package com.kiwicorp.dumbdue.data.repeat

import org.junit.Assert.*
import org.junit.Test
import org.threeten.bp.*

class RepeatDailyTest {
    @Test
    fun getNextDueDate_frequency1june152020_june162020() {
        val time = LocalTime.of(10,15)
        val date = LocalDate.of(2020,Month.JUNE,15)
        val june152020 = ZonedDateTime.of(date,time, ZoneId.systemDefault())
        val repeat = RepeatDaily(1, june152020.toLocalDateTime())

        val june162020 = ZonedDateTime.of(LocalDate.of(2020,Month.JUNE,16),time, ZoneId.systemDefault())

        val result = repeat.getNextDueDate(june152020)
        assertEquals(june162020,result)
    }

    @Test
    fun getNextDueDate_frequency3june152020_june182020() {
        val time = LocalTime.of(10,15)
        val date = LocalDate.of(2020,Month.JUNE,15)
        val june152020 = ZonedDateTime.of(date,time, ZoneId.systemDefault())
        val repeat = RepeatDaily(3, june152020.toLocalDateTime())

        val june182020 = ZonedDateTime.of(LocalDate.of(2020,Month.JUNE,18),time, ZoneId.systemDefault())

        val result = repeat.getNextDueDate(june152020)
        assertEquals(june182020,result)
    }

}