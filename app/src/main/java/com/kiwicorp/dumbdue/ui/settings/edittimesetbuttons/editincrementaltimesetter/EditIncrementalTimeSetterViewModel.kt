package com.kiwicorp.dumbdue.ui.settings.edittimesetbuttons.editincrementaltimesetter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kiwicorp.dumbdue.Event
import com.kiwicorp.dumbdue.preferences.PreferencesStorage
import com.kiwicorp.dumbdue.timesetters.IncrementalTimeSetter
import com.shawnlin.numberpicker.NumberPicker
import java.util.*
import javax.inject.Inject

class EditIncrementalTimeSetterViewModel @Inject constructor(
    private val preferencesStorage: PreferencesStorage
) : ViewModel() {

    private val _eventDone = MutableLiveData<Event<Unit>>()
    val eventDone: LiveData<Event<Unit>> = _eventDone

    lateinit var incrementalTimeSetter: IncrementalTimeSetter
        private set

    private lateinit var key: String

    fun loadTimeSetter(key: String) {
        this.key = key
        incrementalTimeSetter = preferencesStorage.getIncrementalTimeSetter(key)
    }


    val plusMinusPickerOnValueChangedListener = NumberPicker.OnValueChangeListener { picker, oldVal, newVal ->
        incrementalTimeSetter.number *= -1
    }

    val numberPickerOnValueChangedListener = NumberPicker.OnValueChangeListener { picker, oldVal, newVal ->
        incrementalTimeSetter.number = newVal
    }

    /**
     * Pass the number picker so when the unit changes the number picker can adjust its max value
     */
    fun getUnitPickerOnValueChangeListener(numberPicker: NumberPicker): NumberPicker.OnValueChangeListener {
        return NumberPicker.OnValueChangeListener { picker, oldVal, newVal ->
            when (newVal) {
                1 -> {
                    incrementalTimeSetter.unit = Calendar.MINUTE
                    numberPicker.maxValue = 59
                }
                2 -> {
                    incrementalTimeSetter.unit = Calendar.HOUR
                    numberPicker.maxValue = 23
                }
                3 -> {
                    incrementalTimeSetter.unit = Calendar.DAY_OF_YEAR
                    numberPicker.maxValue = 6
                }
                4 -> {
                    incrementalTimeSetter.unit = Calendar.WEEK_OF_YEAR
                    numberPicker.maxValue = 3
                }
                5 -> {
                    incrementalTimeSetter.unit = Calendar.MONTH
                    numberPicker.maxValue = 11
                }
                else -> {
                    incrementalTimeSetter.unit = Calendar.YEAR
                    numberPicker.maxValue = 100
                }
            }
            //update number in case numberPicker's new maxValue was less than numberPicker's value
            incrementalTimeSetter.number = numberPicker.value
        }
    }

    fun onDone() {
        preferencesStorage.updateIncrementalTimeSetter(key, incrementalTimeSetter)
        _eventDone.value = Event(Unit)
    }
}