package com.kiwicorp.dumbdue.ui.addeditreminder

import android.view.View
import android.widget.Button
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.kiwicorp.dumbdue.Event
import com.kiwicorp.dumbdue.REQUEST_COMPLETE
import com.kiwicorp.dumbdue.REQUEST_DELETE
import com.kiwicorp.dumbdue.SnackbarMessage
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.data.source.ReminderRepository
import com.kiwicorp.dumbdue.notifications.ReminderAlarmManager
import com.kiwicorp.dumbdue.util.isOverdue
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

class AddEditReminderViewModel @Inject constructor(
    private val repository: ReminderRepository,
    private val reminderAlarmManager: ReminderAlarmManager
) : ViewModel() {
    /**
     * viewModelJob allows us to cancel all coroutines started by this ViewModel
     */
    private var viewModelJob = Job()

    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //Public mutable for two-way data binding
    val title = MutableLiveData<String>()

    private val _calendar = MutableLiveData(Calendar.getInstance().apply {
        set(Calendar.MILLISECOND,0)
        set(Calendar.SECOND,0)
    })

    val calendar: LiveData<Calendar> = _calendar

    private val _repeatVal = MutableLiveData(Reminder.REPEAT_NONE)
    val repeatVal: LiveData<Int> = _repeatVal

    private val _autoSnoozeVal = MutableLiveData(Reminder.AUTO_SNOOZE_MINUTE)
    val autoSnoozeVal: LiveData<Long> = _autoSnoozeVal

    var reminderId: String? = null
        private set //makes value read only externally

    private val _eventOpenRepeatMenu = MutableLiveData<Event<Unit>>()
    val eventOpenRepeatMenu: LiveData<Event<Unit>> = _eventOpenRepeatMenu

    private val _eventOpenAutoSnoozeMenu = MutableLiveData<Event<Unit>>()
    val eventOpenAutoSnoozeMenu: LiveData<Event<Unit>> = _eventOpenAutoSnoozeMenu

    private val _eventOpenTimePicker = MutableLiveData<Event<Unit>>()
    val eventOpenTimePicker: LiveData<Event<Unit>> = _eventOpenTimePicker

    private val _eventChooseRepeat = MutableLiveData<Event<Unit>>()
    val eventChooseRepeat: LiveData<Event<Unit>> = _eventChooseRepeat

    private val _eventChooseAutoSnooze = MutableLiveData<Event<Unit>>()
    val eventChooseAutoSnooze: LiveData<Event<Unit>> = _eventChooseAutoSnooze

    private val _eventClose = MutableLiveData<Event<Unit>>()
    val eventClose: LiveData<Event<Unit>> = _eventClose

    private val _eventCompleteDelete = MutableLiveData<Event<Int>>()
    val eventCompleteDelete: LiveData<Event<Int>> = _eventCompleteDelete

    private val _snackbarData = MutableLiveData<Event<SnackbarMessage>>()
    val snackbarMessage: LiveData<Event<SnackbarMessage>> = _snackbarData

    /**
     * Called by ImageButtons in AddReminderFragment/EditReminderFragment via listener binding.
     */
    fun onOpenRepeatMenu() {
        _eventOpenRepeatMenu.value = Event(Unit)
    }

    /**
     * Called by ImageButton in AddReminderFragment/EditReminderFragment via listener binding.
     */
    fun onOpenAutoSnoozeMenu() {
        _eventOpenAutoSnoozeMenu.value = Event(Unit)
    }

    /**
     * Called by TextView in AddReminderFragment/EditReminderFragment via listener binding.
     */
    fun onOpenTimePicker() {
        _eventOpenTimePicker.value = Event(Unit)
    }

    /**
     * Called by the TextViews in ChooseRepeatFragment via Listener Binding.
     */
    fun onChooseRepeat(repeatVal: Int) {
        _repeatVal.value = repeatVal
        _eventChooseRepeat.value = Event(Unit)
    }

    /**
     * Called by the TextViews in ChooseAutoSnoozeFragment via Listener Binding.
     */
    fun onChooseAutoSnooze(autoSnoozeVal: Long) {
        _autoSnoozeVal.value = autoSnoozeVal
        _eventChooseAutoSnooze.value = Event(Unit)
    }

    /**
     * Called by ImageButton in AddReminderFragment/EditReminderFragment
     */
    fun onCancel() {
        _eventClose.value = Event(Unit)
    }

    /**
     * Called by TextView in EditReminderFragment vis listener binding.
     */
    fun onDeleteReminder() {
        _eventCompleteDelete.value = Event(REQUEST_DELETE)
    }

    /**
     * Called by TextView in EditReminderFragment vis listener binding.
     */
    fun onCompleteReminder() {
        _eventCompleteDelete.value = Event(REQUEST_COMPLETE)
    }

    /**
     * Called by the ImageButton in AddReminderFragment via listener binding.
     */
    fun addReminder() {
        uiScope.launch {
            if (title.value == null || title.value == "") {
                _snackbarData.value = Event(SnackbarMessage("Title Cannot Be Empty.", Snackbar.LENGTH_SHORT))
            } else if (calendar.value!!.isOverdue()) {
                _snackbarData.value = Event(SnackbarMessage("Due date cannot be in the past", Snackbar.LENGTH_SHORT))
            } else {
                val reminder = Reminder(title.value!!,
                    calendar.value!!,
                    repeatVal.value!!,
                    autoSnoozeVal.value!!)

                insert(reminder)

                _eventClose.value = Event(Unit)
            }

        }
    }

    /**
     * Called by ImageButton in EditReminderFragment via listener binding.
     */
    fun onUpdateReminder() {
        uiScope.launch {
            val reminder = Reminder(title = title.value!!,calendar = calendar.value!!,repeatVal = repeatVal.value!!,autoSnoozeVal = autoSnoozeVal.value!!,id = reminderId!!)
            update(reminder)
        }
        _eventClose.value = Event(Unit)
    }

    /**
     * Populates the properties of the reminder that corresponds to the reminderId.
     *
     * Used only by EditReminderFragment.
     */
    fun loadReminder(reminderId: String) {
        uiScope.launch {
            val reminder = getReminder(reminderId)
            if (reminder != null) {
                onReminderLoaded(reminder)
            }
        }
    }

    /**
     * Inserts the given reminder into the repository
     */
    private suspend fun insert(reminder: Reminder) {
        reminderAlarmManager.setAlarm(reminder)
        withContext(Dispatchers.IO) {
            repository.insertReminder(reminder)
        }
    }

    /**
     * Updates the given reminder in the repository
     */
    private suspend fun update(reminder: Reminder) {
        reminderAlarmManager.updateAlarm(reminder)
        withContext(Dispatchers.IO) {
            repository.updateReminder(reminder)
        }
    }

    /**
     * Gets the reminder that corresponds to reminderId from the repository
     */
    private suspend fun getReminder(reminderId: String): Reminder? {
        return withContext(Dispatchers.IO) {
            repository.getReminder(reminderId)
        }
    }

    /**
     * sets  this ViewModel's properties to the reminder's properties
     */
    private fun onReminderLoaded(reminder: Reminder) {
        title.value = reminder.title
        _calendar.value = reminder.calendar
        _repeatVal.value = reminder.repeatVal
        _autoSnoozeVal.value = reminder.autoSnoozeVal
        reminderId = reminder.id
    }

    /**
     * Called by the QuickAccess buttons in time_button.xml via listener binding to update the calendar.
     * Sets [calendar] to the hour and minute of the invoking Button's text.
     */
    fun onQuickAccessClick(view: View) {
        _calendar.value = _calendar.value?.apply {
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

    /**
     * Called by the TimeSetter buttons in time_button.xml via listener binding to update the calendar.
     * Increments/Decrements [calendar] by the number and unit that the invoking Button's text indicates.
     */
    fun onTimeSetterClick(view: View) {
        _calendar.value = _calendar.value?.apply {
            // number is the actual number of how much to increment/decrement, notDigits contains "+ unit"
            var (number,notDigits)= ((view as Button).text as String).partition { it.isDigit() }
            val unit: Int = when (notDigits.substring(2)) {
                "min" -> Calendar.MINUTE
                "hr" -> Calendar.HOUR
                "day" -> Calendar.DAY_OF_YEAR
                "wk" -> Calendar.WEEK_OF_YEAR
                "mo" -> Calendar.MONTH
                else -> Calendar.YEAR
            }
            if (notDigits[0] == '-') number = "-$number"
            add(unit,number.toInt())
        }
    }

    fun onCalendarUpdated(calendar: Calendar) {
        _calendar.value = _calendar.value!!.apply { timeInMillis = calendar.timeInMillis }
    }

}