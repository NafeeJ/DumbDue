package com.kiwicorp.dumbdue.data.repeat

import org.junit.Assert.*
import org.junit.Test
import java.util.*

class RepeatYearlyByCountTest {
    @Test
    fun getNextDueDate_frequency1JuneSecondSaturday2020_JuneSecondSaturday2021() {
        val juneSecondSaturday2020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,13,3,3,0)
            set(Calendar.MILLISECOND,0)
        }
        val juneSecondSaturday2021 = Calendar.getInstance().apply {
            set(2021, Calendar.JUNE,12,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val repeat = RepeatYearlyByCount(1,juneSecondSaturday2020,2)
        val result = repeat.getNextDueDate(juneSecondSaturday2020)

        assertEquals(result,juneSecondSaturday2021)
    }

    @Test
    fun getNextDueDate_frequency3JuneSecondSaturday2020_JuneSecondSaturday2023() {
        val juneSecondSaturday2020 = Calendar.getInstance().apply {
            set(2020, Calendar.JUNE,13,3,3,0)
            set(Calendar.MILLISECOND,0)
        }
        val juneSecondSaturday2023 = Calendar.getInstance().apply {
            set(2023, Calendar.JUNE,10,3,3,0)
            set(Calendar.MILLISECOND,0)
        }

        val repeat = RepeatYearlyByCount(3,juneSecondSaturday2020,2)
        val result = repeat.getNextDueDate(juneSecondSaturday2020)

        assertEquals(result,juneSecondSaturday2023)
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

        val repeat = RepeatYearlyByCount(3,juneLastSaturday2020,5)
        val result = repeat.getNextDueDate(juneLastSaturday2020)

        assertEquals(result,juneLastSaturday2023)
    }
}