package com.kiwicorp.dumbdue

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*


open class ScheduleReminderFragment : AbstractReminderButtonFragment(),
    TimeDatePickerDialogFragment.OnDateChangedListener,
    TimeDatePickerSpinnerDialogFragment.OnDateChangedListener {
    //widgets
    protected lateinit var titleEditText: EditText
    protected lateinit var cancelButton: ImageButton
    protected lateinit var addButton: ImageButton
    protected lateinit var repeatButton: ImageButton
    protected lateinit var snoozeButton: ImageButton
    protected lateinit var imm: InputMethodManager

    protected var isEditReminderFragment: Boolean = false //boolean used to determine if the keyboard should open when the fragment starts

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_schedule_reminder,container,false)

        titleEditText = mView.findViewById(R.id.titleEditText)
        imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (!isEditReminderFragment) {
            //opens keyboard when window opens
            titleEditText.requestFocus()
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,InputMethodManager.HIDE_NOT_ALWAYS)
        }
        cancelButton = mView.findViewById(R.id.cancelButton)
        snoozeButton = mView.findViewById(R.id.snoozeButton)
        repeatButton = mView.findViewById(R.id.repeatButton)
        addButton = mView.findViewById(R.id.addButton)
        //set auto snooze based off of default value
        val sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(activity!!.applicationContext)
        val autoSnoozeString: String = sharedPreferences.getString("default_auto_snooze",Reminder.AUTO_SNOOZE_MINUTE.toString()) as String
        autoSnoozeVal = autoSnoozeString.toInt()
        updateSnoozeButtonImage()
        //set click listeners
        cancelButton.setOnClickListener{
            activity!!.supportFragmentManager.popBackStack()
            closeKeyboard()
        }
        repeatButton.setOnClickListener {
            closeKeyboard()
            showRepeatDialog()
        }
        snoozeButton.setOnClickListener {
            closeKeyboard()
           showSnoozeDialog()
        }

        addButton.setOnClickListener {
            Reminder(titleEditText.text.toString(),reminderCalendar,repeatVal,autoSnoozeVal,context!!)
            activity!!.supportFragmentManager.popBackStack()
            closeKeyboard()
        }
        dateTextView = mView.findViewById(R.id.dateTextView)
        dateTextView.setOnClickListener {
            closeKeyboard()
            val timeDatePickerString = sharedPreferences.getString("time_date_picker","spinner")
            if (timeDatePickerString == "stock") {
                val datePickerDialogFragment: Fragment = TimeDatePickerDialogFragment()
                val args = Bundle()
                args.putLong("timeInMillis",reminderCalendar.timeInMillis)
                datePickerDialogFragment.arguments = args
                childFragmentManager.beginTransaction()
                    .replace(R.id.container,datePickerDialogFragment)
                    .addToBackStack(null)
                    .commit()
            } else if (timeDatePickerString == "spinner") {
                val datePickerSpinnerDialogFragment = TimeDatePickerSpinnerDialogFragment()
                val args = Bundle()
                args.putLong("timeInMillis",reminderCalendar.timeInMillis)
                datePickerSpinnerDialogFragment.arguments = args
                datePickerSpinnerDialogFragment.show(childFragmentManager,null)
            }
        }
        super.onCreateView(inflater, container, savedInstanceState)
        updateSnoozeButtonImage()
        return mView
    }
    private fun showRepeatDialog() {
        //create and show dialog
        val dialog = BottomSheetDialog(context!!)
        val dialogView = layoutInflater.inflate(R.layout.dialog_choose_repeat,null)
        dialog.setContentView(dialogView)
        dialog.setCanceledOnTouchOutside(true)

        val repeatOffTextView: TextView = dialogView.findViewById(R.id.repeatOffText)
        val repeatDailyTextView: TextView = dialogView.findViewById(R.id.repeatDailyText)
        val repeatWeekdaysTextView: TextView = dialogView.findViewById(R.id.repeatWeekdays)
        val repeatWeeklyTextView: TextView = dialogView.findViewById(R.id.repeatWeekly)
        val repeatYearlyTextView: TextView = dialogView.findViewById(R.id.repeatYearly)
        val repeatMonthlyTextView: TextView = dialogView.findViewById(R.id.repeatMonthly)
        val repeatCustomTextView: TextView = dialogView.findViewById(R.id.repeatCustom)
        //set text views text
        repeatOffTextView.text = "Repeat Off"
        repeatDailyTextView.text = "Daily ".plus(timeFormatter.format(reminderCalendar.time))
        repeatWeekdaysTextView.text = "Weekdays ".plus(timeFormatter.format(reminderCalendar.time))
        repeatWeeklyTextView.text = dayOfWeekFormatter.format(reminderCalendar.time)
            .plus("s ")
            .plus(timeFormatter.format(reminderCalendar.time))
        repeatMonthlyTextView.text = reminderCalendar
            .get(Calendar.DAY_OF_MONTH).toString()
            .plus(MainFragment.daySuffixFinder(reminderCalendar))
            .plus(" each month at ")
            .plus(timeFormatter.format(reminderCalendar.time))
        repeatYearlyTextView.text = "Every ".plus(dateFormatter2.format(reminderCalendar.time))
        repeatCustomTextView.text = "Custom"
        //set click listeners
        //todo make a loop of this?
        repeatOffTextView.setOnClickListener {
            repeatVal = Reminder.REPEAT_NONE
            repeatTextView.visibility = View.GONE
            dialog.dismiss()
        }
        repeatDailyTextView.setOnClickListener {
            repeatVal = Reminder.REPEAT_DAILY
            updateTextViews()
            repeatTextView.visibility = View.VISIBLE
            dialog.dismiss()
        }
        repeatWeekdaysTextView.setOnClickListener {
            repeatVal = Reminder.REPEAT_WEEKDAYS
            updateTextViews()
            repeatTextView.visibility = View.VISIBLE
            dialog.dismiss()
        }
        repeatWeeklyTextView.setOnClickListener {
            repeatVal = Reminder.REPEAT_WEEKLY
            updateTextViews()
            repeatTextView.visibility = View.VISIBLE
            dialog.dismiss()
        }
        repeatMonthlyTextView.setOnClickListener {
            repeatVal = Reminder.REPEAT_MONTHLY
            updateTextViews()
            repeatTextView.visibility = View.VISIBLE
            dialog.dismiss()
        }
        //todo implement this
        repeatCustomTextView.setOnClickListener {
            repeatVal = Reminder.REPEAT_CUSTOM
            updateTextViews()
            repeatTextView.visibility = View.VISIBLE
        }
        dialog.show()
    }
    private fun showSnoozeDialog() {
        //create and show dialog
        val dialog = BottomSheetDialog(context!!)
        val dialogView = layoutInflater.inflate(R.layout.dialog_choose_snooze,null)
        dialog.setContentView(dialogView)
        dialog.setCanceledOnTouchOutside(true)

        val noneTextView: TextView = dialogView.findViewById(R.id.noneTextView)
        val everyMinuteTextView: TextView = dialogView.findViewById(R.id.everyMinuteTextView)
        val every5MinutesTextView: TextView = dialogView.findViewById(R.id.every5MinutesTextView)
        val every10MinutesTextView: TextView = dialogView.findViewById(R.id.every10MinutesTextView)
        val every15MinutesTextView: TextView = dialogView.findViewById(R.id.every15MinutesTextView)
        val every30MinutesTextView: TextView = dialogView.findViewById(R.id.every30MinutesTextView)
        val everyHourTextView: TextView = dialogView.findViewById(R.id.everyHourTextView)

        noneTextView.setOnClickListener { autoSnoozeVal = Reminder.AUTO_SNOOZE_NONE; dialog.dismiss(); updateSnoozeButtonImage() }
        everyMinuteTextView.setOnClickListener { autoSnoozeVal = Reminder.AUTO_SNOOZE_MINUTE; dialog.dismiss(); updateSnoozeButtonImage() }
        every5MinutesTextView.setOnClickListener { autoSnoozeVal = Reminder.AUTO_SNOOZE_5_MINUTES; dialog.dismiss(); updateSnoozeButtonImage() }
        every10MinutesTextView.setOnClickListener { autoSnoozeVal = Reminder.AUTO_SNOOZE_10_MINUTES; dialog.dismiss(); updateSnoozeButtonImage() }
        every15MinutesTextView.setOnClickListener { autoSnoozeVal = Reminder.AUTO_SNOOZE_15_MINUTES; dialog.dismiss(); updateSnoozeButtonImage() }
        every30MinutesTextView.setOnClickListener { autoSnoozeVal = Reminder.AUTO_SNOOZE_30_MINUTES; dialog.dismiss(); updateSnoozeButtonImage() }
        everyHourTextView.setOnClickListener { autoSnoozeVal = Reminder.AUTO_SNOOZE_HOUR; dialog.dismiss(); updateSnoozeButtonImage() }

        dialog.show()
    }
    //closes keyboard if the current focus is not the edit text
    protected fun closeKeyboard() {
        val view: View? = activity!!.currentFocus
        if (view != null) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
    protected fun updateSnoozeButtonImage() {
        val image = when(autoSnoozeVal) {
            Reminder.AUTO_SNOOZE_NONE -> R.drawable.white_none_square
            Reminder.AUTO_SNOOZE_MINUTE -> R.drawable.one_white
            Reminder.AUTO_SNOOZE_5_MINUTES -> R.drawable.five_white
            Reminder.AUTO_SNOOZE_10_MINUTES -> R.drawable.ten_white
            Reminder.AUTO_SNOOZE_15_MINUTES -> R.drawable.fifteen_white
            Reminder.AUTO_SNOOZE_30_MINUTES -> R.drawable.thirty_white
            Reminder.AUTO_SNOOZE_HOUR -> R.drawable.one_hour_white
            else -> 0
        }
        snoozeButton.setImageResource(image)
    }
    override fun onDateChanged(timeInMillis: Long) {
        reminderCalendar.timeInMillis = timeInMillis
        updateTextViews()
    }
}