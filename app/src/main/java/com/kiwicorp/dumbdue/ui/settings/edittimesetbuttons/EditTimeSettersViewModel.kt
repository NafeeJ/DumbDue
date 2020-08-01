package com.kiwicorp.dumbdue.ui.settings.edittimesetbuttons

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kiwicorp.dumbdue.Event
import com.kiwicorp.dumbdue.preferences.PreferencesStorage
import com.kiwicorp.dumbdue.timesetters.OnTimeSetterClick

class EditTimeSettersViewModel @ViewModelInject constructor(
    private val preferencesStorage: PreferencesStorage
) : ViewModel(), OnTimeSetterClick {

    private val _eventEditQuickAccessTimeSetter = MutableLiveData<Event<String>>()
    val eventEditQuickAccessTimeSetter: LiveData<Event<String>> = _eventEditQuickAccessTimeSetter

    private val _eventEditIncrementalTimeSetter = MutableLiveData<Event<String>>()
    val eventEditIncrementalTimeSetter : LiveData<Event<String>> = _eventEditIncrementalTimeSetter

    private val _eventTimeSettersUpdated = MutableLiveData<Event<Unit>>()
    val eventTimeSettersUpdated: LiveData<Event<Unit>> = _eventTimeSettersUpdated

    override fun onQuickAccessTimeSetterClick(key: String) {
        _eventEditQuickAccessTimeSetter.value = Event(key)
    }

    override fun onIncrementalTimeSetterClick(key: String) {
        _eventEditIncrementalTimeSetter.value = Event(key)
    }

    fun onReset() {
        preferencesStorage.resetTimeSetters()
        _eventTimeSettersUpdated.value = Event(Unit)
    }

    fun notifyTimeSettersUpdated() {
        _eventTimeSettersUpdated.value = Event(Unit)
    }

}
