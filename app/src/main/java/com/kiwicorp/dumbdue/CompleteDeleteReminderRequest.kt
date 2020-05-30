package com.kiwicorp.dumbdue

/**
 * Used to store a [request]: either [REQUEST_COMPLETE] or [REQUEST_DELETE] and a [reminderId] to be
 * able to expose this data in a single LiveData object.
 */
data class CompleteDeleteReminderRequest(val request: Int = 0, val reminderId: String = "")