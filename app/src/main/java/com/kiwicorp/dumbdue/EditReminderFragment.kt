package com.kiwicorp.dumbdue

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.*

class EditReminderFragment : ScheduleReminderFragment(),
    TimeDatePickerDialogFragment.OnDateChangedListener,
    TimeDatePickerSpinnerDialogFragment.OnDateChangedListener {

    lateinit var onReminderEditListener: OnReminderEditListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onReminderEditListener = context as OnReminderEditListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        closeKeyboard()
        titleEditText.clearFocus()

        //get reminder data and set vals
        val reminderData: Reminder.ReminderData =
            arguments!!.getParcelable("ReminderData") as Reminder.ReminderData
        dueDateCalendar.timeInMillis = reminderData.remindCalendar.timeInMillis
        titleEditText.setText(reminderData.text)
        autoSnoozeVal = reminderData.autoSnoozeVal
        repeatVal = reminderData.repeatVal

        addButton.setOnClickListener {
            onReminderEditListener.onReminderEdited(titleEditText.text.toString(),
                dueDateCalendar,repeatVal,autoSnoozeVal,reminderData.sectionTitle,reminderData.positionInSection)
            activity!!.supportFragmentManager.popBackStack()
            closeKeyboard()
        }

        return mView
    }

    override fun onDateChanged(timeInMillis: Long) {
        dueDateCalendar.timeInMillis = timeInMillis
        updateTextViews()
    }

    interface OnReminderEditListener {
        fun onReminderEdited(newText: String, newRemindCalendar: Calendar, newRepeatVal: Int,
                             newAutoSnoozeVal: Int, oldSectionTitle: String, oldPositionInSection: Int)
    }
}