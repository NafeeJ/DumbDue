package com.kiwicorp.dumbdue.ui

import androidx.lifecycle.ViewModel
import com.kiwicorp.dumbdue.preferences.PreferencesStorage
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(private val preferencesStorage: PreferencesStorage) : ViewModel() {
    val theme
        get() = preferencesStorage.theme
}