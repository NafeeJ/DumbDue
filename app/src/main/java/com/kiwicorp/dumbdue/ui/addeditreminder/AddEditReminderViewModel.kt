package com.kiwicorp.dumbdue.ui.addeditreminder

import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import com.kiwicorp.dumbdue.Event
import com.kiwicorp.dumbdue.REQUEST_COMPLETE
import com.kiwicorp.dumbdue.REQUEST_DELETE
import com.kiwicorp.dumbdue.SnackbarMessage
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.data.repeat.RepeatInterval
import com.kiwicorp.dumbdue.data.source.ReminderRepository
import com.kiwicorp.dumbdue.preferences.PreferencesStorage
import com.kiwicorp.dumbdue.timesetters.OnTimeSetterClick
import com.kiwicorp.dumbdue.ui.addeditreminder.customrepeat.ChooseCustomRepeatViewModel
import kotlinx.coroutines.launch
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

class AddEditReminderViewModel @Inject constructor(
    private val repository: ReminderRepository,
    private val preferencesStorage: PreferencesStorage
) : ViewModel(), OnTimeSetterClick {
    //Public mutable for two-way data binding
    val title = MutableLiveData("")

    private val _dueDate = MutableLiveData(ZonedDateTime.now().withSecond(0).withNano(0))
    val dueDate: LiveData<ZonedDateTime> = _dueDate

    private val _repeatInterval = MutableLiveData<RepeatInterval>()
    val repeatInterval: LiveData<RepeatInterval> = _repeatInterval

    private val _autoSnoozeVal = MutableLiveData(preferencesStorage.defaultAutoSnooze)
    val autoSnoozeVal: LiveData<Long> = _autoSnoozeVal

    var reminderId: String? = null
        private set //makes value read only externally

    val chooseCustomRepeatViewModel = ChooseCustomRepeatViewModel(dueDate,preferencesStorage.repeatIntervalUsesRemindersTime)

    private val _eventOpenRepeatMenu = MutableLiveData<Event<Unit>>()
    val eventOpenRepeatMenu: LiveData<Event<Unit>> = _eventOpenRepeatMenu

    private val _eventOpenAutoSnoozeMenu = MutableLiveData<Event<Unit>>()
    val eventOpenAutoSnoozeMenu: LiveData<Event<Unit>> = _eventOpenAutoSnoozeMenu

    private val _eventOpenTimePicker = MutableLiveData<Event<Unit>>()
    val eventOpenTimePicker: LiveData<Event<Unit>> = _eventOpenTimePicker

    private val _eventRepeatChosen = MutableLiveData<Event<Unit>>()
    val eventRepeatChosen: LiveData<Event<Unit>> = _eventRepeatChosen

    private val _eventCustomRepeatChosen = MutableLiveData<Event<Unit>>()
    val eventCustomRepeatChosen: LiveData<Event<Unit>> = _eventCustomRepeatChosen

    private val _eventAutoSnoozeChosen = MutableLiveData<Event<Unit>>()
    val eventAutoSnoozeChosen: LiveData<Event<Unit>> = _eventAutoSnoozeChosen

    private val _eventClose = MutableLiveData<Event<Unit>>()
    val eventClose: LiveData<Event<Unit>> = _eventClose

    private val _eventOpenChooseCustomRepeat = MutableLiveData<Event<Unit>>()
    val eventOpenChooseCustomRepeat: LiveData<Event<Unit>> = _eventOpenChooseCustomRepeat

    private val _eventCompleteDelete = MutableLiveData<Event<Int>>()
    val eventCompleteDelete: LiveData<Event<Int>> = _eventCompleteDelete

    private val _eventSnackbar = MutableLiveData<Event<SnackbarMessage>>()
    val eventSnackbar: LiveData<Event<SnackbarMessage>> = _eventSnackbar

    /**
     * Called by ImageButtons in AddReminderFragment/EditReminderFragment via listener binding.
     */
    fun openRepeatMenu() {
        _eventOpenRepeatMenu.value = Event(Unit)
    }

    /**
     * Called by ImageButton in AddReminderFragment/EditReminderFragment via listener binding.
     */
    fun openAutoSnoozeMenu() {
        _eventOpenAutoSnoozeMenu.value = Event(Unit)
    }

    /**
     * Called by TextView in AddReminderFragment/EditReminderFragment via listener binding.
     */
    fun openTimePicker() {
        _eventOpenTimePicker.value = Event(Unit)
    }

    fun onOpenChooseCustomRepeat() {
        _eventOpenChooseCustomRepeat.value = Event(Unit)
    }

    /**
     * Called by the TextViews in ChooseRepeatFragment.
     */
    fun onChooseRepeatInterval(repeatInterval: RepeatInterval?) {
        _repeatInterval.value = repeatInterval
        _eventRepeatChosen.value = Event(Unit)
    }

    fun onChooseCustomRepeatInterval() {
        onChooseRepeatInterval(chooseCustomRepeatViewModel.getRepeatInterval())
        _eventCustomRepeatChosen.value = Event(Unit)
    }

    /**
     * Called by the TextViews in ChooseAutoSnoozeFragment via Listener Binding.
     */
    fun onChooseAutoSnooze(autoSnoozeVal: Long) {
        _autoSnoozeVal.value = autoSnoozeVal
        _eventAutoSnoozeChosen.value = Event(Unit)
    }

    /**
     * Called by ImageButton in AddReminderFragment/EditReminderFragment
     */
    fun cancel() {
        _eventClose.value = Event(Unit)
    }

    /**
     * Called by TextView in EditReminderFragment vis listener binding.
     */
    fun deleteReminder() {
        _eventCompleteDelete.value = Event(REQUEST_DELETE)
    }

    /**
     * Called by TextView in EditReminderFragment vis listener binding.
     */
    fun completeReminder() {
        _eventCompleteDelete.value = Event(REQUEST_COMPLETE)
    }

    /**
     * Called by the ImageButton in AddReminderFragment via listener binding.
     */
    fun addReminder() {
        viewModelScope.launch {
            val reminder = Reminder(title.value!!,
                dueDate.value!!,
                repeatInterval.value,
                autoSnoozeVal.value!!)

            repository.insertReminder(reminder)

            _eventClose.value = Event(Unit)


        }
    }

    /**
     * Updates reminder, returns true if update successful, returns false if update unsuccessful
     */
    fun updateReminder() {
        viewModelScope.launch {
            val originalReminder = reminderId?.let { repository.getReminder(it) }
            val reminder = Reminder(title.value!!,dueDate.value!!,repeatInterval.value,autoSnoozeVal.value!!,reminderId!!)
            if (originalReminder != reminder) {
                repository.updateReminder(reminder)
            }
        }
        _eventClose.value = Event(Unit)
    }

    /**
     * Populates the properties of the reminder that corresponds to the reminderId.
     *
     * Used only by EditReminderFragment.
     */
    fun loadReminder(reminderId: String) {
        viewModelScope.launch {
            val reminder = repository.getReminder(reminderId)
            if (reminder != null) {
                onReminderLoaded(reminder)
            }
        }
    }

    /**
     * sets  this ViewModel's properties to the reminder's properties
     */
    private fun onReminderLoaded(reminder: Reminder) {
        title.value = reminder.title
        _dueDate.value = reminder.dueDate
        _repeatInterval.value = reminder.repeatInterval
        _autoSnoozeVal.value = reminder.autoSnoozeVal
        reminderId = reminder.id
    }

    /**
     * Called by the QuickAccess buttons in time_button.xml via listener binding to update the due date.
     */
    override fun onQuickAccessTimeSetterClick(key: String) {
        val timeSetter = preferencesStorage.getQuickAccessTimeSetter(key)
        updateDueDate(dueDate.value!!.with(timeSetter))
    }

    /**
     * Called by the TimeSetter buttons in time_button.xml via listener binding to update the due date.
     */
    override fun onIncrementalTimeSetterClick(key: String) {
        val timeSetter = preferencesStorage.getIncrementalTimeSetter(key)
        updateDueDate(dueDate.value!!.with(timeSetter))
    }

    fun updateDueDate(dueDate: ZonedDateTime) {
        _dueDate.value = dueDate
        if (_repeatInterval.value != null && preferencesStorage.repeatIntervalUsesRemindersTime) {
            _repeatInterval.value = _repeatInterval.value!!.apply { time = dueDate.toLocalTime() }
        }
    }

}