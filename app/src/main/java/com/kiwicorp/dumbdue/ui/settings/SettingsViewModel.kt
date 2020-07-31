package com.kiwicorp.dumbdue.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kiwicorp.dumbdue.Event
import javax.inject.Inject

class SettingsViewModel @Inject constructor(): ViewModel() {
    private val _eventOpenEditTimeSetButtons = MutableLiveData<Event<Unit>>()
    val eventOpenEditTimeSetButton: LiveData<Event<Unit>> = _eventOpenEditTimeSetButtons

    fun openEditTimeSetButtons() {
        _eventOpenEditTimeSetButtons.value = Event(Unit)
    }
}