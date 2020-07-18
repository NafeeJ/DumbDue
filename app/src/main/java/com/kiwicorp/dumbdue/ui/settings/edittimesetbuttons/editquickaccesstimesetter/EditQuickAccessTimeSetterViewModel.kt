package com.kiwicorp.dumbdue.ui.settings.edittimesetbuttons.editquickaccesstimesetter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kiwicorp.dumbdue.Event
import com.kiwicorp.dumbdue.preferences.PreferencesStorage
import com.kiwicorp.dumbdue.timesetters.QuickAccessTimeSetter
import com.shawnlin.numberpicker.NumberPicker
import javax.inject.Inject

class EditQuickAccessTimeSetterViewModel @Inject constructor(
    private val preferencesStorage: PreferencesStorage
) : ViewModel() {

    lateinit var quickAccessTimeSetter: QuickAccessTimeSetter
        private set

    private lateinit var key: String

    fun loadQuickAccessTimeSetter(key: String) {
        quickAccessTimeSetter = preferencesStorage.getQuickAccessTimeSetter(key)
        this.key = key
    }

    val ampmPickerSetOnValueChangedListener = NumberPicker.OnValueChangeListener { picker, oldVal, newVal ->
        when(newVal) {
            1 -> quickAccessTimeSetter.hourOfDay -= 12
            2 -> quickAccessTimeSetter.hourOfDay += 12
        }
    }

    val minutePickerOnValueChangedListener = NumberPicker.OnValueChangeListener { picker, oldVal, newVal ->
        quickAccessTimeSetter.minute = newVal
    }

    val hourPickerOnValueChangedListener = NumberPicker.OnValueChangeListener { picker, oldVal, newVal ->
       if (quickAccessTimeSetter.hourOfDay > 12) {
           quickAccessTimeSetter.hourOfDay = newVal + 12
       } else {
           quickAccessTimeSetter.hourOfDay = newVal
       }

    }

    fun updateTimeSetter() {
        preferencesStorage.updateQuickAccessTimeSetter(key, quickAccessTimeSetter)
    }
}