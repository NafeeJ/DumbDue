package com.kiwicorp.dumbdue.ui.archive

import com.kiwicorp.dumbdue.data.Reminder

data class CheckableReminder(val reminder: Reminder, val isChecked: Boolean)