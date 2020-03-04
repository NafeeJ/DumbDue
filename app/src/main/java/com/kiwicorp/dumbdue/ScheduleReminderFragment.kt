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


class ScheduleReminderFragment : AbstractReminderButtonFragment(), DatePickerDialogFragment.OnDateChangedListener {
    //widgets
    lateinit var titleEditText: EditText
    lateinit var cancelButton: ImageButton
    lateinit var addButton: ImageButton
    lateinit var repeatButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_schedule_reminder,container,false)

        titleEditText = mView.findViewById(R.id.titleEditText)
        titleEditText.requestFocus() //opens keyboard when window opens
        val imm: InputMethodManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,InputMethodManager.HIDE_NOT_ALWAYS) //shows keyboard when title edit text is focused

        //closes keyboard if the current focus is not the edit text
        fun closeKeyboard() {
            val view: View? = activity!!.currentFocus
            if (view != null) {
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        cancelButton = mView.findViewById(R.id.cancelButton)
        addButton= mView.findViewById(R.id.addButton)
        repeatButton= mView.findViewById(R.id.repeatButton)

        cancelButton.setOnClickListener{
            activity!!.supportFragmentManager.popBackStack()
            closeKeyboard()
        }
        addButton.setOnClickListener {
            Reminder(titleEditText.text.toString(),dueDateCalendar,repeatVal,context!!)
            activity!!.supportFragmentManager.popBackStack()
            closeKeyboard()
        }
        repeatButton.setOnClickListener {
            closeKeyboard()
            //create and show dialog
            val dialog = BottomSheetDialog(context!!)
            val dialogView = layoutInflater.inflate(R.layout.dialog_choose_repeat,null)
            dialog.setContentView(dialogView)
            dialog.setCanceledOnTouchOutside(true)

            //widgets
            val repeatOffTextView: TextView = dialogView.findViewById(R.id.repeatOffText)
            val repeatDailyTextView: TextView = dialogView.findViewById(R.id.repeatDailyText)
            val repeatWeekdaysTextView: TextView = dialogView.findViewById(R.id.repeatWeekdays)
            val repeatWeeklyTextView: TextView = dialogView.findViewById(R.id.repeatWeekly)
            val repeatMonthlyTextView: TextView = dialogView.findViewById(R.id.repeatMonthly)

            //set text views text
            repeatOffTextView.text = "Repeat Off"
            repeatDailyTextView.text = "Daily ".plus(timeFormatter.format(dueDateCalendar.time))
            repeatWeekdaysTextView.text = "Weekdays ".plus(timeFormatter.format(dueDateCalendar.time))
            repeatWeeklyTextView.text = dayOfWeekFormatter.format(dueDateCalendar.time)
                .plus("s ")
                .plus(timeFormatter.format(dueDateCalendar.time))
            repeatMonthlyTextView.text = dueDateCalendar
                .get(Calendar.DAY_OF_MONTH).toString()
                .plus(MainActivity.daySuffixFinder(dueDateCalendar))
                .plus(" each month at ")
                .plus(timeFormatter.format(dueDateCalendar.time))
            //set click listeners
            repeatOffTextView.setOnClickListener {
                repeatVal = Reminder.REPEAT_NONE
                repeatTextView.visibility = View.GONE
                dialog.dismiss()
            }
            repeatDailyTextView.setOnClickListener {
                repeatVal = Reminder.REPEAT_DAILY
                repeatTextView.text =  "Daily "
                    .plus(timeFormatter.format(dueDateCalendar.time))
                repeatTextView.visibility = View.VISIBLE
                dialog.dismiss()
            }
            repeatWeekdaysTextView.setOnClickListener {
                repeatVal = Reminder.REPEAT_WEEKDAYS
                repeatTextView.text = "Weekdays "
                    .plus(timeFormatter.format(dueDateCalendar.time))
                repeatTextView.visibility = View.VISIBLE
                dialog.dismiss()
            }
            repeatWeeklyTextView.setOnClickListener {
                repeatVal = Reminder.REPEAT_WEEKLY
                repeatTextView.text = dayOfWeekFormatter
                    .format(dueDateCalendar.time)
                    .plus("s ")
                    .plus(timeFormatter.format(dueDateCalendar.time))
                repeatTextView.visibility = View.VISIBLE
                dialog.dismiss()
            }
            repeatMonthlyTextView.setOnClickListener {
                repeatVal = Reminder.REPEAT_MONTHLY
                repeatTextView.text = dueDateCalendar.get(Calendar.DAY_OF_MONTH).toString()
                    .plus(MainActivity.daySuffixFinder(dueDateCalendar))
                    .plus(" each month at ")
                    .plus(timeFormatter.format(dueDateCalendar.time))
                repeatTextView.visibility = View.VISIBLE
                dialog.dismiss()
            }
            dialog.show()
        }

        dateTextView = mView.findViewById(R.id.dateTextView)
        dateTextView.setOnClickListener {
            closeKeyboard()
            val datePickerDialogFragment: Fragment = DatePickerDialogFragment()
            val args = Bundle()
            args.putLong("timeInMillis",dueDateCalendar.timeInMillis)
            datePickerDialogFragment.arguments = args
            childFragmentManager.beginTransaction()
                .replace(R.id.container,datePickerDialogFragment)
                .addToBackStack(null)
                .commit()
        }

        super.onCreateView(inflater, container, savedInstanceState)

        return mView
    }

    override fun onDateChanged(timeInMillis: Long) {
        dueDateCalendar.timeInMillis = timeInMillis
        updateTextViews()
    }


}