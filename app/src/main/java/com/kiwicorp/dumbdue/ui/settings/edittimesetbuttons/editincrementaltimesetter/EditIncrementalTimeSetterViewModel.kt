package com.kiwicorp.dumbdue.ui.settings.edittimesetbuttons.editincrementaltimesetter

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kiwicorp.dumbdue.preferences.PreferencesStorage
import com.kiwicorp.dumbdue.timesetters.IncrementalTimeSetter
import org.threeten.bp.temporal.ChronoUnit
import kotlin.math.absoluteValue

class EditIncrementalTimeSetterViewModel @ViewModelInject constructor(
    private val preferencesStorage: PreferencesStorage
) : ViewModel() {

    private val _incrementalTimeSetter = MutableLiveData<IncrementalTimeSetter>()
    val incrementalTimeSetter: LiveData<IncrementalTimeSetter> = _incrementalTimeSetter

    private lateinit var key: String

    fun loadTimeSetter(key: String) {
        this.key = key
        _incrementalTimeSetter.value = preferencesStorage.getIncrementalTimeSetter(key)
    }

    /**
     * Updates the sign of the number of the incremental time setter
     */
    fun onSignChanged(sign: Sign) {
        var num = _incrementalTimeSetter.value!!.number.absoluteValue
        if (sign == Sign.NEGATIVE) {
            num *= -1
        }
        _incrementalTimeSetter.value!!.number = num
        updateTimeSetterInPreferences()
    }

    /**
     * Updates the magnitude of the number of the time setter.
     *
     * [number] is the new number of the time setter
     */
    fun onNumberChanged(number: Long) {
        if (incrementalTimeSetter.value!!.number < 0) {
            _incrementalTimeSetter.value!!.number = number.absoluteValue * -1
        } else {
            _incrementalTimeSetter.value!!.number = number.absoluteValue
        }
        updateTimeSetterInPreferences()
    }

    /**
     * Updates the unit of the incremental time setter
     */
    fun onUnitChanged(unit: ChronoUnit) {
        _incrementalTimeSetter.value!!.unit = unit
        updateTimeSetterInPreferences()
    }

    /**
     * Resets the time setter to its default value
     */
    fun resetTimeSetter() {
        preferencesStorage.resetTimeSetter(key)
        loadTimeSetter(key)
    }

    /**
     * Updates the time setter in preferences
     */
    private fun updateTimeSetterInPreferences() {
        preferencesStorage.updateIncrementalTimeSetter(key, incrementalTimeSetter.value!!)
    }
}

enum class Sign {
    POSITIVE,
    NEGATIVE
}