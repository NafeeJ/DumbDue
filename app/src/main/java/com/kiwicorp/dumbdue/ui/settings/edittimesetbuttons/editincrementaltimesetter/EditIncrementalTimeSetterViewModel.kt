package com.kiwicorp.dumbdue.ui.settings.edittimesetbuttons.editincrementaltimesetter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kiwicorp.dumbdue.Event
import com.kiwicorp.dumbdue.preferences.PreferencesStorage
import com.kiwicorp.dumbdue.timesetters.IncrementalTimeSetter
import com.shawnlin.numberpicker.NumberPicker
import org.threeten.bp.temporal.ChronoUnit
import javax.inject.Inject

class EditIncrementalTimeSetterViewModel @Inject constructor(
    private val preferencesStorage: PreferencesStorage
) : ViewModel() {

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
        incrementalTimeSetter.number = newVal.toLong()
    }

    /**
     * Pass the number picker so when the unit changes the number picker can adjust its max value
     */
    fun getUnitPickerOnValueChangeListener(numberPicker: NumberPicker): NumberPicker.OnValueChangeListener {
        return NumberPicker.OnValueChangeListener { picker, oldVal, newVal ->
            when (newVal) {
                1 -> {
                    incrementalTimeSetter.unit = ChronoUnit.MINUTES
                    numberPicker.maxValue = 59
                }
                2 -> {
                    incrementalTimeSetter.unit = ChronoUnit.HOURS
                    numberPicker.maxValue = 23
                }
                3 -> {
                    incrementalTimeSetter.unit = ChronoUnit.DAYS
                    numberPicker.maxValue = 6
                }
                4 -> {
                    incrementalTimeSetter.unit = ChronoUnit.WEEKS
                    numberPicker.maxValue = 3
                }
                5 -> {
                    incrementalTimeSetter.unit = ChronoUnit.MONTHS
                    numberPicker.maxValue = 11
                }
                else -> {
                    incrementalTimeSetter.unit = ChronoUnit.YEARS
                    numberPicker.maxValue = 100
                }
            }
            //update number in case numberPicker's new maxValue was less than numberPicker's value
            incrementalTimeSetter.number = numberPicker.value.toLong()
        }
    }

    fun updateTimeSetter() {
        preferencesStorage.updateIncrementalTimeSetter(key, incrementalTimeSetter)
    }
}