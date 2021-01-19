package com.kiwicorp.dumbdue.timesetters

import com.kiwicorp.dumbdue.preferences.PreferencesStorage

/**
 * Interface for any ui that includes uses time setters
 */
interface OnTimeSetterClick {
    val preferencesStorage: PreferencesStorage

    fun onQuickAccessTimeSetterClick(key: String)

    fun onIncrementalTimeSetterClick(key: String)
}