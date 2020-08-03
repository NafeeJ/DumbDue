package com.kiwicorp.dumbdue.ui.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kiwicorp.dumbdue.Event
import com.kiwicorp.dumbdue.preferences.PreferencesStorage

class SettingsViewModel @ViewModelInject constructor(private val preferencesStorage: PreferencesStorage): ViewModel() {
    private val _eventOpenEditTimeSetButtons = MutableLiveData<Event<Unit>>()
    val eventOpenEditTimeSetButton: LiveData<Event<Unit>> = _eventOpenEditTimeSetButtons

    private val _eventOpenChooseThemeDialog = MutableLiveData<Event<Unit>>()
    val eventOpenChooseThemeDialog: LiveData<Event<Unit>> = _eventOpenChooseThemeDialog

    private val _repeatIntervalUsesReminderDueDate = MutableLiveData<Boolean>(preferencesStorage.repeatIntervalUsesRemindersTime)
    val repeatIntervalUsesReminderDueDate: LiveData<Boolean> = _repeatIntervalUsesReminderDueDate

    fun openEditTimeSetButtons() {
        _eventOpenEditTimeSetButtons.value = Event(Unit)
    }

    fun openChooseThemeDialog() {
        _eventOpenChooseThemeDialog.value = Event(Unit)
    }

    fun setTheme(theme: Int) {
        preferencesStorage.theme = theme
    }

    fun setRepeatIntervalUsesReminderDueDate(repeatIntervalUsesReminderDueDate: Boolean) {
        preferencesStorage.repeatIntervalUsesRemindersTime = repeatIntervalUsesReminderDueDate
    }
}