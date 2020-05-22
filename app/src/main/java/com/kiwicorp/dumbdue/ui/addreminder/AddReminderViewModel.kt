package com.kiwicorp.dumbdue.ui.addreminder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.data.source.ReminderRepository
import com.kiwicorp.dumbdue.util.TimeSetter
import com.kiwicorp.dumbdue.util.timeFromNowString
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class AddReminderViewModel internal constructor(private val reminderRepository: ReminderRepository): ViewModel() {

    //viewModelJob allows us to cancel all coroutines started by this ViewModel
    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //for two way data-binding
    val title = MutableLiveData<String>()

    private val calendar = MutableLiveData(Calendar.getInstance())
    val timeSetter = TimeSetter(calendar)

    val timeFromNow: LiveData<String> = Transformations.map(calendar) {calendar ->
        val dateFormatter = SimpleDateFormat("EEE, d MMM, h:mm a", Locale.US)
        "${dateFormatter.format(calendar.time)} in ${calendar.timeFromNowString()}"
    }

    private suspend fun insert(reminder: Reminder) {
        withContext(Dispatchers.IO) {
            reminderRepository.insertReminder(reminder)
        }
    }

    fun onAdd() {
        uiScope.launch {
            insert(Reminder(title = title.value ?: "",calendar = calendar.value ?: Calendar.getInstance()))
        }
    }

}
