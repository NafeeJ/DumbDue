package com.kiwicorp.dumbdue.ui.settings.edittimesetbuttons

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kiwicorp.dumbdue.Event
import com.kiwicorp.dumbdue.OnTimeSetButtonClick
import com.kiwicorp.dumbdue.ui.settings.PreferencesStorage
import javax.inject.Inject

class EditTimeSetButtonsViewModel @Inject constructor(
    val preferencesStorage: PreferencesStorage
) : ViewModel(), OnTimeSetButtonClick {

    private val _eventEditQuickAccessTimeSetter = MutableLiveData<Event<String>>()
    val eventEditQuickAccessTimeSetter: LiveData<Event<String>> = _eventEditQuickAccessTimeSetter

    private val _eventEditIncrementalTimeSetter = MutableLiveData<Event<String>>()
    val eventEditIncrementalTimeSetter : LiveData<Event<String>> = _eventEditIncrementalTimeSetter

    override fun onQuickAccessTimeSetterClick(key: String) {
        _eventEditQuickAccessTimeSetter.value = Event(key)
    }

    override fun onIncrementalTimeSetterClick(key: String) {
        _eventEditIncrementalTimeSetter.value = Event(key)
    }

}
