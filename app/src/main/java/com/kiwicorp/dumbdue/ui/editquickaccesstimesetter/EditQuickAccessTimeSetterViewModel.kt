package com.kiwicorp.dumbdue.ui.editquickaccesstimesetter

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kiwicorp.dumbdue.preferences.PreferencesStorage
import com.kiwicorp.dumbdue.timesetters.AmPm
import com.kiwicorp.dumbdue.timesetters.QuickAccessTimeSetter

class EditQuickAccessTimeSetterViewModel @ViewModelInject constructor(
    private val preferencesStorage: PreferencesStorage
) : ViewModel() {

    private val _quickAccessTimeSetter = MutableLiveData<QuickAccessTimeSetter>()
    var quickAccessTimeSetter: LiveData<QuickAccessTimeSetter> = _quickAccessTimeSetter

    private lateinit var key: String

    fun loadTimeSetter(key: String) {
        _quickAccessTimeSetter.value = preferencesStorage.getQuickAccessTimeSetter(key)
        this.key = key
    }

    /**
     * Updates the period of the time setter
     */
    fun onAmPmChanged(amPm: AmPm) {
        quickAccessTimeSetter.value!!.amPm = amPm
        updateTimeSetterInPreferences()
    }

    /**
     * Updates the hour of the time setter
     */
    fun onHourChanged(hour: Int) {
        _quickAccessTimeSetter.value!!.hour = hour
        updateTimeSetterInPreferences()
    }

    /**
     * Updates the minute of the time setter
     */
    fun onMinuteChanged(minute: Int) {
        _quickAccessTimeSetter.value!!.minute = minute
        updateTimeSetterInPreferences()
    }

    fun resetTimeSetter() {
        preferencesStorage.resetTimeSetter(key)
        loadTimeSetter(key)
    }

    private fun updateTimeSetterInPreferences() {
        preferencesStorage.updateQuickAccessTimeSetter(key, quickAccessTimeSetter.value!!)
    }
}
