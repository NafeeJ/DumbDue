package com.kiwicorp.dumbdue.ui.reminders

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReminderRequest(val request: Int, val reminderId: String): Parcelable {
    companion object {
        const val REQUEST_ARCHIVE = 1
        const val REQUEST_COMPLETE = 2
        const val REQUEST_UNARCHIVE = 3
        const val REQUEST_DELETE = 4
    }
}

