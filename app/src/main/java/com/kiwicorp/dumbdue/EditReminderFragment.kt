package com.kiwicorp.dumbdue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.*

class EditReminderFragment : ScheduleReminderFragment(),
    TimeDatePickerDialogFragment.OnDateChangedListener,
    TimeDatePickerSpinnerDialogFragment.OnDateChangedListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        isEditReminderFragment = true
        //get reminder data and set vals
        val reminderData: OldReminder.ReminderData =
            arguments!!.getParcelable("ReminderData") as OldReminder.ReminderData
        reminderCalendar.timeInMillis = reminderData.remindCalendar.timeInMillis
        titleEditText.setText(reminderData.text)
        autoSnoozeVal = reminderData.autoSnoozeVal
        repeatVal = reminderData.repeatVal

        updateTextViews()
        updateSnoozeButtonImage()

        addButton.setOnClickListener {
//            onReminderEditListener.onReminderEdited(titleEditText.text.toString(),
//                reminderCalendar,repeatVal,autoSnoozeVal,reminderData.sectionTitle,reminderData.positionInSection)
//            activity!!.supportFragmentManager.popBackStack()
            closeKeyboard()
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDateChanged(timeInMillis: Long) {
        reminderCalendar.timeInMillis = timeInMillis
        updateTextViews()
    }

    interface OnReminderEditListener {
        fun onReminderEdited(newText: String, newRemindCalendar: Calendar, newRepeatVal: Int,
                             newAutoSnoozeVal: Int, oldSectionTitle: String, oldPositionInSection: Int)
    }
}