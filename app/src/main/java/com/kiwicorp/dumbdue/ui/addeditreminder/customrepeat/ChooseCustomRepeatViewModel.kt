package com.kiwicorp.dumbdue.ui.addeditreminder.customrepeat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.kiwicorp.dumbdue.Event
import com.kiwicorp.dumbdue.data.repeat.*
import com.kiwicorp.dumbdue.util.sortedSundayFirst
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

class ChooseCustomRepeatViewModel @Inject constructor(): ViewModel() {


    val time = MutableLiveData<LocalTime>()

    val timeStr = Transformations.map(time) { time ->
        time.format(DateTimeFormatter.ofPattern("h:mm a"))
    }
    // public mutable for 2 way data binding
    var frequency = MutableLiveData<String>()
    // public mutable for 2 way data binding
    var type = MutableLiveData<String>()

    fun getRepeatInterval(): RepeatInterval {
        return with(type.value!!) {
            when (this) {
                "days" -> {
                    chooseDailyViewModel.getRepeatInterval(frequency.value!!.toInt(),time.value!!)
                }
                "weeks" -> {
                    chooseWeeklyViewModel.getRepeatInterval(frequency.value!!.toInt(), time.value!!)
                }
                "months" -> {
                    chooseMonthlyViewModel.getRepeatInterval(frequency.value!!.toInt(), time.value!!)
                }
                "years" -> {
                    chooseYearlyViewModel.getRepeatInterval(frequency.value!!.toInt(), time.value!!)
                }
                else -> {
                    throw Exception("The done button was pressed when it should've have been able to")
                }
            }
        }
    }

    val chooseDailyViewModel = ChooseDailyViewModel()
    val chooseWeeklyViewModel = ChooseWeeklyViewModel()
    val chooseMonthlyViewModel = ChooseMonthlyViewModel()
    val chooseYearlyViewModel = ChooseYearlyViewModel()
}

class ChooseDailyViewModel {
    private val _startingDate = MutableLiveData<LocalDate>()
    val startingDate: LiveData<LocalDate> = _startingDate

    val startingDateStr = Transformations.map(startingDate) { date ->
        date.format(DateTimeFormatter.ofPattern("MMMM d, yyy"))
    }

    private val _eventOnStartingDateChosen = MutableLiveData<Event<Unit>>()
    val eventOnStartingDateChosen: LiveData<Event<Unit>> = _eventOnStartingDateChosen

    fun getRepeatInterval(frequency: Int, time: LocalTime): RepeatDaily {
        return RepeatDaily(frequency, LocalDateTime.of(startingDate.value!!,time))
    }

    fun chooseStartingDate(date: LocalDate) {
        _startingDate.value = date
        _eventOnStartingDateChosen.value = Event(Unit)
    }
}

class ChooseWeeklyViewModel {
    private val _firstDateOfStartingWeek = MutableLiveData<LocalDate>()
    val firstDateOfStartingWeek: LiveData<LocalDate> = _firstDateOfStartingWeek

    val firstDateOfStartingWeekStr = Transformations.map(firstDateOfStartingWeek) { date ->
        "week starting on ${date.format(DateTimeFormatter.ofPattern("MMMM d"))}"
    }

    private val _eventOnStartingWeekChosen = MutableLiveData<Event<Unit>>()
    val eventOnFirstDateOfStartingWeekChosen: LiveData<Event<Unit>> = _eventOnStartingWeekChosen

    val daysOfWeek: MutableList<DayOfWeek> = mutableListOf()

    fun getRepeatInterval(frequency: Int, time: LocalTime): RepeatWeekly {
        return RepeatWeekly(frequency, LocalDateTime.of(firstDateOfStartingWeek.value!!,time), daysOfWeek.sortedSundayFirst())
    }

    fun chooseStartingWeek(firstDateOfWeek: LocalDate) {
        _firstDateOfStartingWeek.value = firstDateOfWeek
        _eventOnStartingWeekChosen.value = Event(Unit)
    }
}

class ChooseMonthlyViewModel {
    var startingYearMonth: YearMonth = YearMonth.now()

    val selectedMonthlyOption = MutableLiveData<String>()

    val daysByNumber = mutableListOf<Int>()

    val daysByCount = mutableListOf(RepeatMonthlyByCount.Day())

    fun getRepeatInterval(frequency: Int, time: LocalTime): RepeatInterval {
        return if (selectedMonthlyOption.value!! == "By count of day of week in month") {
            RepeatMonthlyByCount(frequency,startingYearMonth, time, daysByCount)
        } else {
            return RepeatMonthlyByNumber(frequency,startingYearMonth,time,daysByNumber.sorted())
        }
    }
}

class ChooseYearlyViewModel {
    var startingYear = Year.now()
    // public mutable for 2 way data binding
    val selectedYearlyOption = MutableLiveData<String>()

    var byNumberMonthDay: MonthDay = MonthDay.now()

    var byCountDayOfWeek = DayOfWeek.SUNDAY

    var byCountDayOfWeekInMonth = 0

    var byCountMonth = Month.JANUARY

    fun getRepeatInterval(frequency: Int, time: LocalTime): RepeatInterval {
        return if (selectedYearlyOption.value!! == "By count of day of week in month") {
            RepeatYearlyByCount(frequency, startingYear, byCountMonth, byCountDayOfWeek, byCountDayOfWeekInMonth, time)
        } else {
            RepeatYearlyByNumber(frequency, LocalDateTime.of(
                startingYear.value,
                byNumberMonthDay.month,
                byNumberMonthDay.dayOfMonth,
                time.hour,
                time.minute))
        }
    }
}