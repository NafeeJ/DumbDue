package com.kiwicorp.dumbdue

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shawnlin.numberpicker.NumberPicker
import java.text.SimpleDateFormat
import java.util.*

class TimeDatePickerSpinnerDialogFragment : BottomSheetDialogFragment() {
    lateinit var onDateChangedListener: OnDateChangedListener

    lateinit var mView: View
    //widgets
    lateinit var datePicker: NumberPicker
    lateinit var hourPicker: NumberPicker
    lateinit var minutePicker: NumberPicker
    lateinit var ampmPicker: NumberPicker

    var calendar = Calendar.getInstance()

    private val dateFormatter = SimpleDateFormat("EEE MMM d", Locale.US)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onDateChangedListener = parentFragment as OnDateChangedListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val timeInMillis: Long = arguments!!.getLong("timeInMillis")
        calendar.timeInMillis = timeInMillis

        mView = layoutInflater.inflate(R.layout.dialog_spinner_time_date_picker,container,false)
        //initialize widgets
        datePicker = mView.findViewById(R.id.datePicker)
        hourPicker = mView.findViewById(R.id.hourPicker)
        minutePicker = mView.findViewById(R.id.minutePicker)
        ampmPicker = mView.findViewById(R.id.ampmPicker)

        fun dateInit(i: Int): Calendar {
            val tempCalendar = Calendar.getInstance()
            tempCalendar.timeInMillis = calendar.timeInMillis
            tempCalendar.add(Calendar.DAY_OF_YEAR,i)
            return tempCalendar
        }

        //configure date picker
        val calendarList: Array<Calendar> = Array(5) {i -> dateInit(i - 2) } //list storing calendars
        val dateList: Array<String> = Array(5) {i -> dateFormatter.format(calendarList[i].time)} //list storing formatted dates of calendars
        datePicker.minValue = 0
        datePicker.maxValue = 4
        datePicker.value = 2//sets the current value to be today's date
        datePicker.displayedValues = dateList
        //todo make it so today's date is displayed as "Today"
        datePicker.setOnValueChangedListener { _, oldVal, newVal ->
            if (oldVal < newVal || (oldVal == 4 && newVal == 0)) {//if the picker is increasing
                //gets the proper index of the new val +1/+2
                val indexPlus1: Int = if (newVal + 1 <= 4) newVal + 1 else 0
                val indexPlus2: Int = if (newVal + 2 <= 4) newVal + 2 else newVal - 4 + 1
                //increments the next two values of the picker to be +1/+2 days from the current val of the picker
                calendarList[indexPlus1].timeInMillis = calendarList[newVal].timeInMillis + 8.64e+7.toLong()
                calendarList[indexPlus2].timeInMillis = calendarList[newVal].timeInMillis + (2 * 8.64e+7).toLong()
                //updates the date list to display the correct calendar dates
                dateList[indexPlus1] = dateFormatter.format(calendarList[indexPlus1].time)
                dateList[indexPlus2] = dateFormatter.format(calendarList[indexPlus2].time)
            } else if (oldVal > newVal || (oldVal == 0 && newVal == 4)) {//if the picker is decreasing
                //gets the proper index of the new val -1/-2
                val indexMinus2: Int = if (newVal - 2 >= 0) newVal - 2 else newVal + 4 - 1
                val indexMinus1: Int = if (newVal - 1 >= 0) newVal - 1 else 4
                //decrements the last two values of the picker to be -1/-2 days from the current val of the picker
                calendarList[indexMinus1].timeInMillis = calendarList[newVal].timeInMillis - 8.64e+7.toLong()
                calendarList[indexMinus2].timeInMillis = calendarList[newVal].timeInMillis - (2 * 8.64e+7).toLong()
                //updates the date list to display the correct dates
                dateList[indexMinus1] = dateFormatter.format(calendarList[indexMinus1].time)
                dateList[indexMinus2] = dateFormatter.format(calendarList[indexMinus2].time)
            }
            calendar.set(Calendar.DAY_OF_YEAR,calendarList[newVal].get(Calendar.DAY_OF_YEAR))
            onDateChangedListener.onDateChanged(calendar.timeInMillis)
        }
        //configure hour picker
        hourPicker.maxValue = 12
        hourPicker.value = calendar.get(Calendar.HOUR)
        hourPicker.setOnValueChangedListener { _, _, newVal ->
            calendar.set(Calendar.HOUR,newVal)
            onDateChangedListener.onDateChanged(calendar.timeInMillis)
        }
        //configure minute picker
        val minuteList: Array<String> = Array(60) {i -> i.toString()}
        for (i in 0..9) { minuteList[i] = ("0").plus(minuteList[i]) }//add 0s before all digits less then 10 in minute list
        minutePicker.maxValue = 59
        minutePicker.minValue = 0
        minutePicker.value = calendar.get(Calendar.MINUTE)
        minutePicker.displayedValues = minuteList
        minutePicker.setOnValueChangedListener { _, _, newVal ->
            calendar.set(Calendar.MINUTE,newVal)
            onDateChangedListener.onDateChanged(calendar.timeInMillis)
        }
        //configure AM PM Picker
        val ampmList: Array<String> = arrayOf("AM","PM")
        ampmPicker.maxValue = 2
        ampmPicker.displayedValues = ampmList
        ampmPicker.value = when (calendar.get(Calendar.AM_PM)) {
            Calendar.AM -> 1
            else -> 2
        }
        ampmPicker.setOnValueChangedListener { _, _, newVal ->
            calendar.set(Calendar.AM_PM, if (newVal == 1) Calendar.AM else Calendar.PM )
            onDateChangedListener.onDateChanged(calendar.timeInMillis)
        }
        return mView
    }

    interface OnDateChangedListener { fun onDateChanged(timeInMillis: Long) }
}