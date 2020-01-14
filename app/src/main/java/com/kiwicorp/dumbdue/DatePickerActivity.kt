package com.kiwicorp.dumbdue

import android.app.Activity
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class DatePickerActivity : Activity() {

    private val timeFormatter = SimpleDateFormat("h:mm a")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_date_picker)


        val calendarView: CalendarView = findViewById(R.id.calendarView)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = intent.getLongExtra("timeInMillis",calendar.timeInMillis)

        val setButton: Button = findViewById(R.id.dateTimeSetButton)
        val cancelButton: Button = findViewById(R.id.dateTimeCancelButton)
        val timePickerButton: ImageButton = findViewById(R.id.timePickerImageButton)
        val timeTextView: TextView = findViewById(R.id.timeTextView)

        timeTextView.text = timeFormatter.format(calendar.time)

        setButton.setOnClickListener {
            intent.putExtra("newTimeInMillis",calendar.timeInMillis)
            setResult(RESULT_OK,intent)
            finish()
        }
        cancelButton.setOnClickListener {
            setResult(RESULT_CANCELED,intent)
            finish()
        }

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)
        }

        timePickerButton.setOnClickListener {
            showTimePicker(calendar,timeTextView)
        }

        timeTextView.setOnClickListener {
            showTimePicker(calendar,timeTextView)
        }
    }

    private fun showTimePicker(calendar: Calendar,textView: TextView) {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(this,TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY,hourOfDay)
            calendar.set(Calendar.MINUTE,minute)
            textView.text = timeFormatter.format(calendar.time)
        },hour,minute,false)

        timePicker.show()
    }

}