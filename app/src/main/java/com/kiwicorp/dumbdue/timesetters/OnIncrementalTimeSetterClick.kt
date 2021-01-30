package com.kiwicorp.dumbdue.timesetters

import com.kiwicorp.dumbdue.preferences.PreferencesStorage

interface OnIncrementalTimeSetterClick {
    val preferencesStorage: PreferencesStorage

    fun onIncrementalTimeSetterClick(key: String)
}