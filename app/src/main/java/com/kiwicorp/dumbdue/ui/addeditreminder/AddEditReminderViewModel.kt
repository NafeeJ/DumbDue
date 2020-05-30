package com.kiwicorp.dumbdue.ui.addeditreminder

import android.view.View
import android.widget.Button
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.kiwicorp.dumbdue.Event
import com.kiwicorp.dumbdue.SnackbarMessage
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.data.source.ReminderRepository
import com.kiwicorp.dumbdue.util.timeFromNowMins
import kotlinx.coroutines.*
import java.util.*

class AddEditReminderViewModel internal constructor(private val repository: ReminderRepository) : ViewModel() {
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
    val autoSnoozeVal: LiveData<Int> = _autoSnoozeVal

    private var reminderId: String? = null

    private val _eventOpenRepeatMenu = MutableLiveData<Event<Unit>>()
    val eventOpenRepeatMenu: LiveData<Event<Unit>> = _eventOpenRepeatMenu

    private val _eventOpenAutoSnoozeMenu = MutableLiveData<Event<Unit>>()
    val eventOpenAutoSnoozeMenu: LiveData<Event<Unit>> = _eventOpenAutoSnoozeMenu

    private val _eventChooseRepeat = MutableLiveData<Event<Unit>>()
    val eventChooseRepeat: LiveData<Event<Unit>> = _eventChooseRepeat

    private val _eventChooseAutoSnooze = MutableLiveData<Event<Unit>>()
    val eventChooseAutoSnooze: LiveData<Event<Unit>> = _eventChooseAutoSnooze

    private val _eventCancel = MutableLiveData<Event<Unit>>()
    val eventCancel: LiveData<Event<Unit>> = _eventCancel

    private val _snackbarData = MutableLiveData<Event<SnackbarMessage>>()
    val snackbarMessage: LiveData<Event<SnackbarMessage>> = _snackbarData

    /**
     * Called by ImageButtons in AddReminderFragment/EditReminderFragment via listener binding.
     */
    fun onOpenRepeatMenu() {
        _eventOpenRepeatMenu.value = Event(Unit)
    }

    /**
     * Called by ImageButton in AddReminderFragment/EditReminderButton via listener binding.
     */
    fun onOpenAutoSnoozeMenu() {
        _eventOpenAutoSnoozeMenu.value = Event(Unit)
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
    fun onChooseAutoSnooze(autoSnoozeVal: Int) {
        _autoSnoozeVal.value = autoSnoozeVal
        _eventChooseAutoSnooze.value = Event(Unit)
    }

    /**
     * Called by ImageButton in AddReminderFragment/EditReminderFragment
     */
    fun onCancel() {
        _eventCancel.value = Event(Unit)
    }

    /**
     * Called by the ImageButton in AddReminderFragment via listener binding.
     */
    fun addReminder() {
        uiScope.launch {
            if (title.value == null || title.value == "") {
                _snackbarData.value = Event(SnackbarMessage("Title Cannot Be Empty.", Snackbar.LENGTH_SHORT))
            } else if (calendar.value!!.timeFromNowMins() <= 0) {
                _snackbarData.value = Event(SnackbarMessage("Due date cannot be in the past", Snackbar.LENGTH_SHORT))
            } else {
                insert(Reminder(title = title.value!!,calendar = calendar.value!!,repeatVal = repeatVal.value!!,autoSnoozeVal = autoSnoozeVal.value!!))
                _eventCancel.value = Event(Unit)
            }

        }
    }

    /**
     * Inserts the given reminder into the repository
     */
    private suspend fun insert(reminder: Reminder) {
        withContext(Dispatchers.IO) {
            repository.insertReminder(reminder)
        }
    }

    /**
     * Called by ImageButton in EditReminderFragment via listener binding.
     */
    fun onUpdateReminder() {
        uiScope.launch {
            update(Reminder(title = title.value!!,calendar = calendar.value!!,repeatVal = repeatVal.value!!,autoSnoozeVal = autoSnoozeVal.value!!,id = reminderId!!))
        }
        _eventCancel.value = Event(Unit)
    }

    /**
     * Updates the given reminder in the repository
     */
    private suspend fun update(reminder: Reminder) {
        withContext(Dispatchers.IO) {
            repository.updateReminder(reminder)
        }
    }

    /**
     * Called by ImageButton in EditReminderFragment vis listener binding.
     */
    fun onDeleteReminder() {
        uiScope.launch {
            delete(Reminder(title = title.value!!,calendar = calendar.value!!,repeatVal = repeatVal.value!!,autoSnoozeVal = autoSnoozeVal.value!!,id = reminderId!!))
        }
        _eventCancel.value = Event(Unit)
    }

    /**
     * Deletes the given reminder in the repository
     */
    private suspend fun delete(reminder: Reminder) {
        withContext(Dispatchers.IO) {
            repository.deleteReminder(reminder)
        }
    }

    /**
     * Populates the properties of the reminder that corresponds to the reminderId.
     *
     * Used only by EditReminderFragment.
     */
    fun loadReminder(reminderId: String) {
        uiScope.launch {
            val reminder = getReminderFromDatabase(reminderId)
            if (reminder != null) {
                onReminderLoaded(reminder)
            }
        }
    }

    /**
     * Gets the reminder that corresponds to reminderId from the repository
     */
    private suspend fun getReminderFromDatabase(reminderId: String): Reminder? {
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

    /**
     * Called by the TimeSetter buttons in time_button.xml via listener binding to update the calendar.
     * Increments/Decrements [calendar] by the number and unit that the invoking Button's text indicates.
     */
    fun onTimeSetterClick(view: View) {
        _calendar.value = _calendar.value!!.apply {
            // number is the actual number of how much to increment/decrement, notDigits contains "+ unit"
            val (numbers,notDigits)= ((view as Button).text as String).partition { it.isDigit() }
            val unit: Int = when (notDigits.substring(2)) {
                "min" -> Calendar.MINUTE
                "hr" -> Calendar.HOUR
                "day" -> Calendar.DAY_OF_YEAR
                "wk" -> Calendar.WEEK_OF_YEAR
                "mo" -> Calendar.MONTH
                else -> Calendar.YEAR
            }
            var number: Int = numbers.toInt()
            if (notDigits[0] == '-') number *= -1
            add(unit,number)
        }
    }

}