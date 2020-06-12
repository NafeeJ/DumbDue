package com.kiwicorp.dumbdue.timesetters

/**
 * Interface for any ui that includes uses time setters
 */
interface OnTimeSetterClick {
    fun onQuickAccessTimeSetterClick(key: String)

    fun onIncrementalTimeSetterClick(key: String)
}