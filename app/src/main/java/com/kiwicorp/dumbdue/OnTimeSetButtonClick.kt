package com.kiwicorp.dumbdue

/**
 * Interface for any layout that includes time_buttons.xml
 */
interface OnTimeSetButtonClick {
    fun onQuickAccessTimeSetterClick(key: String)

    fun onIncrementalTimeSetterClick(key: String)
}