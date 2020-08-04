package com.kiwicorp.dumbdue.ui.addeditreminder.customrepeat

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.kiwicorp.dumbdue.Event
import com.kiwicorp.dumbdue.data.repeat.*
import com.kiwicorp.dumbdue.preferences.PreferencesStorage
import com.kiwicorp.dumbdue.util.getFullName
import com.kiwicorp.dumbdue.util.sortedSundayFirst
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoField
import org.threeten.bp.temporal.TemporalAdjusters

class ChooseCustomRepeatViewModel @ViewModelInject constructor(val preferencesStorage: PreferencesStorage): ViewModel() {

    private val _dueDate = MutableLiveData(ZonedDateTime.now())
    val dueDate: LiveData<ZonedDateTime> = _dueDate

    val time = Transformations.map(dueDate) { it.toLocalTime() }
    val timeStr = Transformations.map(this.time) { time ->
        time.format(DateTimeFormatter.ofPattern("h:mm a"))
    }
    // public mutable for 2 way data binding
    var frequency = MutableLiveData("1")

    private val _type = MutableLiveData("weeks")
    var type : LiveData<String> = _type

    private val _eventOpenTimePicker = MutableLiveData<Event<Unit>>()
    val eventOpenTimePicker: LiveData<Event<Unit>> = _eventOpenTimePicker

    val chooseDailyViewModel = ChooseDailyViewModel(dueDate)
    val chooseWeeklyViewModel = ChooseWeeklyViewModel(dueDate)
    val chooseMonthlyViewModel = ChooseMonthlyViewModel(dueDate)
    val chooseYearlyViewModel = ChooseYearlyViewModel(dueDate)

    var repeatInterval: RepeatInterval
        get() {
            val frequency = this.frequency.value!!.toInt()
            val time = time.value!!

            return when (type.value!!) {
                "days" -> chooseDailyViewModel.getRepeatInterval(frequency, time)
                "weeks" -> chooseWeeklyViewModel.getRepeatInterval(frequency, time)
                "months" -> chooseMonthlyViewModel.getRepeatInterval(frequency, time)
                "years" -> chooseYearlyViewModel.getRepeatInterval(frequency, time)
                else -> throw Exception("The done button was pressed when it should've have been able to")
            }
        }
        set(value) {
            setTime(value.time)
            frequency.value = value.frequency.toString()

            when (value) {
                is RepeatDailyInterval -> {
                    setType("days")
                    chooseDailyViewModel.loadRepeatDailyInterval(value)
                }
                is RepeatWeeklyInterval -> {
                    setType("weeks")
                    chooseWeeklyViewModel.loadRepeatWeeklyInterval(value)
                }
                is RepeatMonthlyInterval -> {
                    setType("months")
                    chooseMonthlyViewModel.loadRepeatMonthlyInterval(value)
                }
                is RepeatYearlyInterval -> {
                    setType("years")
                    chooseYearlyViewModel.loadRepeatYearlyInterval(value)
                }
            }
        }

    fun setDueDate(dueDate: ZonedDateTime) {
        _dueDate.value = dueDate
    }

    fun openTimePicker() {
        _eventOpenTimePicker.value = Event(Unit)
    }

    fun setTime(time: LocalTime) {
        (this.time as? MutableLiveData<LocalTime>)?.value = time
    }

    fun setType(type: String) {
        _type.value = type
    }
}

class ChooseDailyViewModel(dueDate: LiveData<ZonedDateTime>) {
    private val _startingDate = Transformations.map(dueDate) { it.toLocalDate() } as MutableLiveData
    val startingDate: LiveData<LocalDate> = _startingDate

    val startingDateStr = Transformations.map(_startingDate) { date ->
        date.format(DateTimeFormatter.ofPattern("MMMM d, yyy"))
    }

    private val _eventOpenChooseDailyStartDate = MutableLiveData<Event<Unit>>()
    val eventOpenChooseDailyStartDate: LiveData<Event<Unit>> = _eventOpenChooseDailyStartDate

    private val _eventOnStartingDateChosen = MutableLiveData<Event<Unit>>()
    val eventOnStartingDateChosen: LiveData<Event<Unit>> = _eventOnStartingDateChosen

    fun getRepeatInterval(frequency: Int, time: LocalTime): RepeatDailyInterval {
        return RepeatDailyInterval(frequency, time, _startingDate.value!!)
    }

    fun openChooseDailyStartDateDialog() {
        _eventOpenChooseDailyStartDate.value = Event(Unit)
    }

    fun setStartingDate(date: LocalDate) {
        _startingDate.value = date
        _eventOnStartingDateChosen.value = Event(Unit)
    }

     fun loadRepeatDailyInterval(repeatDailyInterval: RepeatDailyInterval) {
         _startingDate.value = repeatDailyInterval.startingDate
     }
}

class ChooseWeeklyViewModel(dueDate: LiveData<ZonedDateTime>) {
    private val _firstDateOfStartingWeek = Transformations.map(dueDate) { it.toLocalDate().with(
        TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)) } as MutableLiveData
    val firstDateOfStartingWeek: LiveData<LocalDate> = _firstDateOfStartingWeek

    val firstDateOfStartingWeekStr = Transformations.map(_firstDateOfStartingWeek) { date ->
        "week starting on ${date.format(DateTimeFormatter.ofPattern("MMMM d"))}"
    }

    private val _eventOpenChooseWeeklyStartDate = MutableLiveData<Event<Unit>>()
    val eventOpenChooseWeeklyStartDate: LiveData<Event<Unit>> = _eventOpenChooseWeeklyStartDate

    private val _eventOnStartingWeekChosen = MutableLiveData<Event<Unit>>()
    val eventOnFirstDateOfStartingWeekChosen: LiveData<Event<Unit>> = _eventOnStartingWeekChosen

    private val _daysOfWeek = MutableLiveData(listOf(dueDate.value!!.dayOfWeek))
    val daysOfWeek: LiveData<List<DayOfWeek>> = _daysOfWeek

    fun getRepeatInterval(frequency: Int, time: LocalTime): RepeatWeeklyInterval {
        return RepeatWeeklyInterval(
            frequency,
            time,
            _firstDateOfStartingWeek.value!!,
            daysOfWeek.value!!.sortedSundayFirst())
    }

    fun openChooseWeeklyStartDateDialog() {
        _eventOpenChooseWeeklyStartDate.value = Event(Unit)
    }

    fun setStartingWeek(firstDateOfWeek: LocalDate) {
        _firstDateOfStartingWeek.value = firstDateOfWeek
        _eventOnStartingWeekChosen.value = Event(Unit)
    }

    fun addDayOfWeek(dayOfWeek: DayOfWeek) {
        _daysOfWeek.value = _daysOfWeek.value!!.toMutableList().apply { add(dayOfWeek) }
    }

    fun removeDayOfWeek(dayOfWeek: DayOfWeek) {
        _daysOfWeek.value = _daysOfWeek.value!!.toMutableList().apply { remove(dayOfWeek) }
    }

    fun loadRepeatWeeklyInterval(repeatWeeklyInterval: RepeatWeeklyInterval) {
        _firstDateOfStartingWeek.value = repeatWeeklyInterval.dateOfFirstDayOfStartingWeek
        _daysOfWeek.value = repeatWeeklyInterval.daysOfWeek
    }
}


class ChooseMonthlyViewModel(dueDate: LiveData<ZonedDateTime>) {
    private val _startingYearMonth = Transformations.map(dueDate) { YearMonth.from(it) } as MutableLiveData

    val startingYearMonthStr: LiveData<String> = Transformations.map(_startingYearMonth) {
        "${it.month.getFullName()} ${it.year + if (it.month < YearMonth.now().month) 1 else 0}"
    }

    private val _selectedMonthlyOption = MutableLiveData("By number of day in month")
    val selectedMonthlyOption: LiveData<String> = _selectedMonthlyOption

    private val _daysByCount = MutableLiveData(listOf(RepeatMonthlyByCountInterval.Day(dueDate.value!!.get(ChronoField.ALIGNED_WEEK_OF_MONTH),dueDate.value!!.dayOfWeek)))
    val daysByCount: LiveData<List<RepeatMonthlyByCountInterval.Day>> = _daysByCount

    private val _daysByNumber = MutableLiveData(listOf(dueDate.value!!.dayOfMonth))
    val daysByNumber: LiveData<List<Int>> = _daysByNumber

    fun getRepeatInterval(frequency: Int, time: LocalTime): RepeatInterval {
        return if (selectedMonthlyOption.value!! == "By count of day of week in month") {
            RepeatMonthlyByCountInterval(frequency,_startingYearMonth.value!!, time, daysByCount.value!!.distinct().sorted())
        } else {
            return RepeatMonthlyByNumberInterval(frequency,_startingYearMonth.value!!,time,daysByNumber.value!!.sorted())
        }
    }

    fun updateStartingYearMonth(yearMonth: YearMonth) {
        _startingYearMonth.value = yearMonth
    }

    fun addDayInDaysByNumber(day: Int) {
        _daysByNumber.value = daysByNumber.value!!.toMutableList().apply { add(day) }
    }

    fun removeDayInDaysByNumber(day: Int) {
        _daysByNumber.value = daysByNumber.value!!.toMutableList().apply { remove(day) }
    }

    fun addDayInDayByCount(day: RepeatMonthlyByCountInterval.Day) {
        _daysByCount.value = daysByCount.value!!.toMutableList().apply { add(day) }
    }

    fun removeDayInDayByCount(day: RepeatMonthlyByCountInterval.Day) {
        _daysByCount.value = daysByCount.value!!.toMutableList().apply { remove(day) }
    }

    fun loadRepeatMonthlyInterval(repeatMonthlyInterval: RepeatMonthlyInterval) {
        _startingYearMonth.value = repeatMonthlyInterval.startingYearMonth
        if (repeatMonthlyInterval is RepeatMonthlyByCountInterval) {
            updateSelectedMonthlyOption("By count of day of week in month")
            _daysByCount.value = repeatMonthlyInterval.days

        } else if (repeatMonthlyInterval is RepeatMonthlyByNumberInterval) {
            updateSelectedMonthlyOption("By number of day in month")
            _daysByNumber.value = repeatMonthlyInterval.days
        }
    }

    fun updateSelectedMonthlyOption(option: String) {
        _selectedMonthlyOption.value = option
    }
}

class ChooseYearlyViewModel(dueDate: LiveData<ZonedDateTime>) {
    val _selectedYearlyOption = MutableLiveData("By number of day in month")
    val selectedYearlyOption: LiveData<String> = _selectedYearlyOption

    private val _startingYear = Transformations.map(dueDate) { Year.from(it) } as MutableLiveData
    val startingYear: LiveData<Year> = _startingYear

    private val _byCountDayOfWeek = MutableLiveData(dueDate.value!!.dayOfWeek)
    val byCountDayOfWeek: LiveData<DayOfWeek> = _byCountDayOfWeek

    private val _byCountDayOfWeekInMonth = MutableLiveData(dueDate.value!!.get(ChronoField.ALIGNED_WEEK_OF_MONTH))
    val byCountDayOfWeekInMonth: LiveData<Int> = _byCountDayOfWeekInMonth

    private val _byCountMonth = MutableLiveData(Month.from(dueDate.value!!))
    var byCountMonth: LiveData<Month> = _byCountMonth

    private val _byNumberMonthDay = MutableLiveData(MonthDay.from(dueDate.value!!))
    val byNumberMonthDay: LiveData<MonthDay> = _byNumberMonthDay

    fun updateSelectedYearlyOption(option: String) {
        _selectedYearlyOption.value = option
    }

    fun updateStartingYear(year: Year) {
        _startingYear.value = year
    }

    fun updateByCountDayOfWeek(dayOfWeek: DayOfWeek) {
        _byCountDayOfWeek.value = dayOfWeek
    }

    fun updateByCountDayOfWeekInMonth(dayOfWeekInMonth: Int) {
        _byCountDayOfWeekInMonth.value = dayOfWeekInMonth
    }

    fun updateByCountMonth(month: Month) {
        _byCountMonth.value = month
    }

    fun updateByNumberMonthDay(monthDay: MonthDay) {
        _byNumberMonthDay.value = monthDay
    }

    fun getRepeatInterval(frequency: Int, time: LocalTime): RepeatInterval {
        return if (selectedYearlyOption.value!! == "By count of day of week in month") {
            RepeatYearlyByCountInterval(
                frequency,
                startingYear.value!!,
                byCountMonth.value!!,
                byCountDayOfWeek.value!!,
                byCountDayOfWeekInMonth.value!!,
                time
            )
        } else {
            RepeatYearlyByNumberInterval(
                frequency,
                time,
                LocalDate.of(
                    startingYear.value!!.value,
                    byNumberMonthDay.value!!.month,
                    byNumberMonthDay.value!!.dayOfMonth
                )
            )
        }
    }

    fun loadRepeatYearlyInterval(repeatYearlyInterval: RepeatYearlyInterval) {
        _startingYear.value = repeatYearlyInterval.startingYear
        if (repeatYearlyInterval is RepeatYearlyByCountInterval) {
            updateSelectedYearlyOption("By count of day of week in month")
            _byCountMonth.value = repeatYearlyInterval.month
            _byCountDayOfWeek.value = repeatYearlyInterval.dayOfWeek
            _byCountDayOfWeekInMonth.value = repeatYearlyInterval.dayOfWeekInMonth
        } else if (repeatYearlyInterval is RepeatYearlyByNumberInterval) {
            updateSelectedYearlyOption("By number of day in month")
            _byNumberMonthDay.value = MonthDay.from(repeatYearlyInterval.startingDate)
        }
    }
}