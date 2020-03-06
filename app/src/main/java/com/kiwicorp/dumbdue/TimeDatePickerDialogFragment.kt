package com.kiwicorp.dumbdue

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import java.util.*

class TimeDatePickerDialogFragment : DialogFragment() {
    private lateinit var onDateChangedListener: OnDateChangedListener

    lateinit var mView: View
    //widgets
    private lateinit var calendarView: CalendarView
    private lateinit var setButton: Button
    private lateinit var cancelButton: Button
    private lateinit var timePickerButton: ImageButton
    private lateinit var timeTextView: TextView

    val calendar: Calendar = Calendar.getInstance()

    private val timeFormatter = SimpleDateFormat("h:mm a", Locale.US)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onDateChangedListener = parentFragment as OnDateChangedListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //get time in millis of parent fragment
        val timeMillis: Long = this.arguments!!.getLong("timeInMillis")
        calendar.timeInMillis = timeMillis

        mView = inflater.inflate(R.layout.dialog_date_picker,container,false)
        //initialize widgets
        calendarView = mView.findViewById(R.id.calendarView)
        setButton = mView.findViewById(R.id.dateTimeSetButton)
        cancelButton = mView.findViewById(R.id.dateTimeCancelButton)
        timePickerButton = mView.findViewById(R.id.timePickerImageButton)
        timeTextView = mView.findViewById(R.id.timeTextView)

        timeTextView.text = timeFormatter.format(calendar.time)
        //set listeners
        setButton.setOnClickListener {
            onDateChangedListener.onDateChanged(calendar.timeInMillis)
            parentFragment!!.childFragmentManager.popBackStack()
        }
        cancelButton.setOnClickListener { parentFragment!!.childFragmentManager.popBackStack()}

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)
        }

        timePickerButton.setOnClickListener { showTimePicker(calendar,timeTextView) }
        timeTextView.setOnClickListener { showTimePicker(calendar,timeTextView) }

        return mView
    }

    private fun showTimePicker(calendar: Calendar,textView: TextView) {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(context,
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY,hourOfDay)
                calendar.set(Calendar.MINUTE,minute)
                textView.text = timeFormatter.format(calendar.time)
            },hour,minute,false)

        timePicker.show()
    }
    //interface for parent fragment to implement to transfer data
    interface OnDateChangedListener { fun onDateChanged(timeInMillis: Long) }
}