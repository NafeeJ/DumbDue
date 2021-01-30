package com.kiwicorp.dumbdue.ui.editduedate

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.kiwicorp.dumbdue.preferences.PreferencesStorage

class EditDueDateViewModel @ViewModelInject constructor(
    private val preferencesStorage: PreferencesStorage
): ViewModel() {


   fun onIncrementalTimeSetterClick(key: String) {
       val timeSetter = preferencesStorage.getIncrementalTimeSetter(key)

    }

}