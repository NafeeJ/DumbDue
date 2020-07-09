package com.kiwicorp.dumbdue.data.repeat

import org.junit.Assert.*
import org.junit.Test
import org.threeten.bp.*

class RepeatYearlyByCountIntervalTest {

    val time = LocalTime.of(10,15)

    @Test
    fun getNextDueDate_frequency1JuneSecondSaturday2020_JuneSecondSaturday2021() {
        val juneSecondSaturday2020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE, 13), time, ZoneId.systemDefault())
        val juneSecondSaturday2021 = ZonedDateTime.of(LocalDate.of(2021, Month.JUNE, 12), time, ZoneId.systemDefault())

        val repeat = RepeatYearlyByCountInterval(1,Year.of(2020),Month.JUNE,DayOfWeek.SATURDAY,2,time)
        val result = repeat.getNextDueDate(juneSecondSaturday2020)

        assertEquals(juneSecondSaturday2021,result)
    }

    @Test
    fun getNextDueDate_frequency3JuneSecondSaturday2020_JuneSecondSaturday2023() {
        val juneSecondSaturday2020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE, 13), time, ZoneId.systemDefault())
        val juneSecondSaturday2023 = ZonedDateTime.of(LocalDate.of(2023, Month.JUNE, 10), time, ZoneId.systemDefault())

        val repeat = RepeatYearlyByCountInterval(3,Year.of(2020),Month.JUNE,DayOfWeek.SATURDAY,2,time)
        val result = repeat.getNextDueDate(juneSecondSaturday2020)

        assertEquals(juneSecondSaturday2023,result)
    }

    @Test
    fun getNextDueDate_frequency3JuneLastSaturday2020_JuneLastSaturday2023() {
        val juneLastSaturday2020 = ZonedDateTime.of(LocalDate.of(2020, Month.JUNE, 27), time, ZoneId.systemDefault())
        val juneLastSaturday2023 = ZonedDateTime.of(LocalDate.of(2023, Month.JUNE, 24), time, ZoneId.systemDefault())

        val repeat = RepeatYearlyByCountInterval(3,Year.of(2020),Month.JUNE,DayOfWeek.SATURDAY,5,time)
        val result = repeat.getNextDueDate(juneLastSaturday2020)

        assertEquals(juneLastSaturday2023,result)
    }
}