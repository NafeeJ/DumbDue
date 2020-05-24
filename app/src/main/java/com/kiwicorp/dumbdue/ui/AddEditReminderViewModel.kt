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

    /**
     * Communicates to ChooseRepeatFragment that a [repeatVal] has been chosen
     */
    private val _eventChooseRepeat = MutableLiveData<Boolean>()
    val eventChooseRepeat: LiveData<Boolean>
        get()=_eventChooseRepeat

    /**
     * Called by the TextViews in ChooseRepeatFragment via Listener Binding.
     * Updates [_repeatVal] and updates [_eventChooseRepeat] to let ChooseRepeatFragment know that a
     * [repeatVal] has been chosen.
     */
    fun onChooseRepeat(repeatVal: Int) {
        _repeatVal.value = repeatVal
        _eventChooseRepeat.value = true
    }

    /**
     * Called by Fragment after repeat menu is opened to reset [_eventChooseRepeat] so it can be
     * used again.
     */
    fun onChooseRepeatComplete() {
        _eventChooseRepeat.value = null
    }

    /**
     * Communicates to ChooseAutoSnoozeFragment that a [autoSnoozeVal] has been chosen.
     */
    private val _eventChooseAutoSnooze = MutableLiveData<Boolean>()
    val eventChooseAutoSnooze: LiveData<Boolean>
            get() = _eventChooseAutoSnooze

    /**
     * Called by the TextViews in ChooseAutoSnoozeFragment via Listener Binding.
     * Updates [_autoSnoozeVal] and updates [_eventChooseAutoSnooze] to let ChooseAutoSnoozeFragment
     * know that a [autoSnoozeVal] has been chosen.
     */
    fun onChooseAutoSnooze(autoSnoozeVal: Int) {
        _autoSnoozeVal.value = autoSnoozeVal
        _eventChooseAutoSnooze.value = true
    }

    /**
     * Function that resets [_eventChooseAutoSnooze] so it can be used again.
     */
    fun onChooseAutoSnoozeComplete() {
        _eventChooseAutoSnooze.value = null
    }

    /**
     * Communicates to AddReminderFragment/EditReminderFragment that the users wants to open the
     * repeat menu.
     */
    private val _eventOpenRepeatMenu = MutableLiveData<Boolean>()
    val eventOpenRepeatMenu: LiveData<Boolean>
        get() = _eventOpenRepeatMenu

    /**
     * Called by ImageButtons in AddReminderFragment and EditReminderFragment to communicate to the
     * fragments that the user wants to open the repeat menu.
     */
    fun onOpenRepeatMenu() {
        _eventOpenRepeatMenu.value = true
    }

    /**
     * Called by Fragment after the repeat menu is opened to reset [_eventChooseRepeat] so it can be
     * used again.
     */
    fun onOpenRepeatMenuComplete() {
        _eventChooseRepeat.value = null
    }

    /**
     * Indicate to the fragment that the user wants to open the auto snooze menu.
     */
    private val _eventOpenAutoSnoozeMenu = MutableLiveData<Boolean>()
    val eventOpenAutoSnoozeMenu: LiveData<Boolean>
        get() = _eventOpenAutoSnoozeMenu

    /**
     * Called by ImageButton in AddReminderFragment/EditReminderButton via listener binding.
     */
    fun onOpenAutoSnoozeMenu() {
        _eventOpenAutoSnoozeMenu.value = true
    }

    /**
     * Called by fragment after the AutoSnoozeMenu is opened so [_eventOpenAutoSnoozeMenu] can be used
     * again.
     */
    fun onOpenAutoSnoozeMenuComplete() {
        _eventOpenAutoSnoozeMenu.value = null
    }

    /**
     * Communicates to fragment that the user wishes to cancel adding/editing a reminder
     */
    private val _eventCancel = MutableLiveData<Boolean>()
    val eventCancel: LiveData<Boolean>
        get() = _eventCancel

    /**
     * Called by ImageButton in AddReminderFragment/EditReminderFragment
     */
    fun onCancel() {
        _eventCancel.value = true
    }

    /**
     * Called after adding/editing a reminder has been canceled
     */
    fun onCancelComplete() {
        _eventCancel.value = false
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

    /**
     * Called by the ImageButton in AddReminderFragment/EditReminderFragment via listener binding.
     */
    fun addReminder() {
        uiScope.launch {
            //todo make snackbar when title is empty and don't allow for reminder to be created
            insert(Reminder(title = title.value ?: "",calendar = calendar.value!!,repeatVal = repeatVal.value!!,autoSnoozeVal = autoSnoozeVal.value!!))
        }
    }

    /**
     * Inserts Reminder into the repository
     */
    private suspend fun insert(reminder: Reminder) {
        withContext(Dispatchers.IO) {
            reminderRepository.insertReminder(reminder)
        }
    }

}