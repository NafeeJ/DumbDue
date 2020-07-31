package com.kiwicorp.dumbdue.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kiwicorp.dumbdue.Event
import com.kiwicorp.dumbdue.preferences.PreferencesStorage
import javax.inject.Inject

class SettingsViewModel @Inject constructor(private val preferencesStorage: PreferencesStorage): ViewModel() {
    private val _eventOpenEditTimeSetButtons = MutableLiveData<Event<Unit>>()
    val eventOpenEditTimeSetButton: LiveData<Event<Unit>> = _eventOpenEditTimeSetButtons

    private val _eventOpenChooseThemeDialog = MutableLiveData<Event<Unit>>()
    val eventOpenChooseThemeDialog: LiveData<Event<Unit>> = _eventOpenChooseThemeDialog

    fun openEditTimeSetButtons() {
        _eventOpenEditTimeSetButtons.value = Event(Unit)
    }

    fun openChooseThemeDialog() {
        _eventOpenChooseThemeDialog.value = Event(Unit)
    }

    fun changeTheme(theme: Int) {
        preferencesStorage.theme = theme
    }
}