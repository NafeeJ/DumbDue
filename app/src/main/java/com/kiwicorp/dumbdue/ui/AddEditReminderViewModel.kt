package com.kiwicorp.dumbdue.ui

import android.view.View
import android.widget.Button
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.data.source.ReminderRepository
import kotlinx.coroutines.*
import java.util.*

class AddEditReminderViewModel internal constructor(private val reminderRepository: ReminderRepository) : ViewModel() {
    //viewModelJob allows us to cancel all coroutines started by this ViewModel
    private var viewModelJob = Job()

    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //for two way data-binding
    val title = MutableLiveData<String>()

    private val _calendar = MutableLiveData(Calendar.getInstance())
    val calendar: LiveData<Calendar>
        get() = _calendar

    private val _repeatVal = MutableLiveData(Reminder.REPEAT_NONE)
    val repeatVal: LiveData<Int>
        get() = _repeatVal

    private val _autoSnoozeVal = MutableLiveData(Reminder.AUTO_SNOOZE_MINUTE)
    val autoSnoozeVal: LiveData<Int>
        get() = _autoSnoozeVal

    private val _eventChooseRepeat = MutableLiveData<Boolean>()
    val eventChooseRepeat: LiveData<Boolean>
        get()=_eventChooseRepeat

    /**
     * Called by TextViews in ChooseRepeatFragment via DataBinding
     */
    fun onChooseRepeat(repeatVal: Int) {
        _repeatVal.value = repeatVal
        _eventChooseRepeat.value = true
    }

    fun onChooseRepeatComplete() {
        _eventChooseRepeat.value = null
    }

    /**
     * Called by TextViews in ChooseAutoSnooze via DataBinding
     */
    private val _eventChooseAutoSnooze = MutableLiveData<Boolean>()
    val eventChooseAutoSnooze: LiveData<Boolean>
            get() = _eventChooseAutoSnooze

    fun onChooseAutoSnooze(autoSnoozeVal: Int) {
        _autoSnoozeVal.value = autoSnoozeVal
        _eventChooseAutoSnooze.value = true
    }

    fun onChooseAutoSnoozeComplete() {
        _eventChooseAutoSnooze.value = null
    }

    //live data to allow for communication to fragment to open the repeat menu
    private val _eventOpenRepeatMenu = MutableLiveData<Boolean>()
    val eventOpenRepeatMenu: LiveData<Boolean>
        get() = _eventOpenRepeatMenu

    fun onOpenRepeatMenu() {
        _eventOpenRepeatMenu.value = true
    }

    fun onOpenRepeatMenuComplete() {
        _eventChooseRepeat.value = null
    }

    private val _eventOpenAutoSnoozeMenu = MutableLiveData<Boolean>()
    val eventOpenAutoSnoozeMenu: LiveData<Boolean>
        get() = _eventOpenAutoSnoozeMenu

    fun onOpenAutoSnoozeMenu() {
        _eventOpenAutoSnoozeMenu.value = true
    }

    fun onOpenAutoSnoozeMenuComplete() {
        _eventOpenAutoSnoozeMenu.value = null
    }

    private val _eventCancel = MutableLiveData<Boolean>()
    val eventCancel: LiveData<Boolean>
        get() = _eventCancel

    fun onCancel() {
        _eventCancel.value = true
    }

    fun onCancelComplete() {
        _eventCancel.value = false
    }


    fun onQuickAccessClick(view: View) {
        _calendar.value = _calendar.value!!.apply {
            val text = (view as Button).text as String

            val minute: Int = text.substringAfter(':').substringBefore(' ').toInt()
            var hour: Int = text.substringBefore(':').toInt()
            if (text.takeLast(2) == "PM") {
                if (hour < 12) hour += 12
            } else {
                if (hour == 12) hour = 0
            }
            set(Calendar.HOUR_OF_DAY,hour)
            set(Calendar.MINUTE,minute)
        }
    }

    fun onTimeSetterClick(view: View) {
        _calendar.value = _calendar.value!!.apply {
            //update due date calendar
            // number is the actual number of how much to increment/decrement, notDigits contains "+ unit"
            val (number,notDigits)= ((view as Button).text as String).partition { it.isDigit() }
            val unit: Int = when (notDigits.substring(2)) {
                "min" -> Calendar.MINUTE
                "hr" -> Calendar.HOUR
                "day" -> Calendar.DAY_OF_YEAR
                "wk" -> Calendar.WEEK_OF_YEAR
                "mo" -> Calendar.MONTH
                else -> Calendar.YEAR
            }
            var incrementNumber: Int = number.toInt()
            if (notDigits[0] == '-') incrementNumber *= -1
            add(unit,incrementNumber)
        }
    }

    //called by data binding
    fun addReminder() {
        uiScope.launch {
            //todo make snackbar when title is empty and don't allow for reminder to be created
            insert(Reminder(title = title.value ?: "",calendar = calendar.value!!))
        }
    }

    private suspend fun insert(reminder: Reminder) {
        withContext(Dispatchers.IO) {
            reminderRepository.insertReminder(reminder)
        }
    }

}