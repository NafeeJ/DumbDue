package com.kiwicorp.dumbdue.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.kiwicorp.dumbdue.preferences.PreferencesStorage

class MainActivityViewModel @ViewModelInject constructor(private val preferencesStorage: PreferencesStorage) : ViewModel() {
    val theme
        get() = preferencesStorage.theme
}