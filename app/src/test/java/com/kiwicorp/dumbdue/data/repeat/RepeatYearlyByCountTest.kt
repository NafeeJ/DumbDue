package com.kiwicorp.dumbdue.data.repeat

import org.junit.Assert.*
import org.junit.Test
import java.util.*

class RepeatYearlyByCountTest {
    @Test
    fun getNextDueDate_frequency1JuneSecondSaturday2020_JuneSecondSaturday2021() {
        val calendar = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,13,3,3,0)
            set(Calendar.MILLISECOND,0)
        }
        val nextYearSameMonthDayOfWeekDay = Calendar.getInstance().apply {
            set(2021, Calendar.JUNE,12,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val repeat = RepeatYearlyByCount(1,2)
        val result = repeat.getNextDueDate(calendar)

        assertEquals(result,nextYearSameMonthDayOfWeekDay)
    }

    @Test
    fun getNextDueDate_frequency3JuneSecondSaturday2020_JuneSecondSaturday2023() {
        val calendar = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,13,3,3,0)
            set(Calendar.MILLISECOND,0)
        }
        val nextYearSameMonthDayOfWeekDay = Calendar.getInstance().apply {
            set(2023, Calendar.JUNE,10,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val repeat = RepeatYearlyByCount(3,2)
        val result = repeat.getNextDueDate(calendar)

        assertEquals(result,nextYearSameMonthDayOfWeekDay)
    }

    @Test
    fun getNextDueDate_frequency3JuneLastSaturday2020_JuneLastSaturday2023() {
        val juneLastSaturday2020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,26,3,3,0)
            set(Calendar.MILLISECOND,0)
        }
        val juneLastSaturday2023 = Calendar.getInstance().apply {
            set(2023, Calendar.JUNE,30,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val repeat = RepeatYearlyByCount(3,5)
        val result = repeat.getNextDueDate(juneLastSaturday2020)

        assertEquals(result,juneLastSaturday2023)
    }
}