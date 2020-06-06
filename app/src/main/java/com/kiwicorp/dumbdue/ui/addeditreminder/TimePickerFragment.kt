package com.kiwicorp.dumbdue.ui.addeditreminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentTimePickerBinding
import com.kiwicorp.dumbdue.util.DaggerBottomSheetDialogFragment
import com.kiwicorp.dumbdue.util.getNavGraphViewModel
import com.shawnlin.numberpicker.NumberPicker
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TimePickerFragment : DaggerBottomSheetDialogFragment() {

    private lateinit var binding: FragmentTimePickerBinding

    private val args: TimePickerFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: AddEditReminderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = getNavGraphViewModel(args.graphId) { viewModelFactory }
        val root = inflater.inflate(R.layout.fragment_time_picker, container, false)
        binding = FragmentTimePickerBinding.bind(root).apply {
            viewmodel = viewModel
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupDatePicker(binding.datePicker, viewModel.calendar.value!!)
        setupHourPicker(binding.hourPicker, viewModel.calendar.value!!)
        setupMinutePicker(binding.minutePicker, viewModel.calendar.value!!)
        setupAmpmPicker(binding.ampmPicker, viewModel.calendar.value!!)
    }

    private fun setupDatePicker(datePicker: NumberPicker, calendar: Calendar) {
        // a list of 5 calendars where each calendar is a day apart and the calendar at [2] is the
        // current due date
        val calendars = List(5) { i ->
            Calendar.getInstance().apply {
                timeInMillis = calendar.timeInMillis
                add(Calendar.DAY_OF_YEAR,i - 2)
            }
        }

        val today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val dateFormatter = SimpleDateFormat("EEE MMM d", Locale.US)
        //return the date of the calendar formatted. If the calendar's date is today, return "Today"
        fun format(calendar: Calendar): String {
            return if (calendar.get(Calendar.DAY_OF_YEAR) == today) {
                "Today"
            } else {
                dateFormatter.format(calendar.time)
            }
        }

        // a list of the 5 calendars from [calendars] formatted
        val dates = Array(5) { i -> format(calendars[i]) }

        datePicker.displayedValues = dates

        datePicker.setOnValueChangedListener { picker, oldVal, newVal ->
            if (oldVal < newVal || (oldVal == 4 && newVal == 0)) { //if the date picker is increasing
                //gets the proper index of the new val +1/+2
                val indexPlus1: Int = if (newVal + 1 <= 4) newVal + 1 else 0
                val indexPlus2: Int = if (newVal + 2 <= 4) newVal + 2 else newVal - 4 + 1
                //increments the next two values of the picker to be +1/+2 days from the current val of the picker
                calendars[indexPlus1].timeInMillis = calendars[newVal].timeInMillis + 8.64e+7.toLong()
                calendars[indexPlus2].timeInMillis = calendars[newVal].timeInMillis + (2 * 8.64e+7).toLong()
                //updates the date list to display the correct calendar dates
                dates[indexPlus1] = format(calendars[indexPlus1])
                dates[indexPlus2] = format(calendars[indexPlus2])
            } else { //if the date picker is decreasing
                //gets the proper index of the new val -1/-2
                val indexMinus1: Int = if (newVal - 1 >= 0) newVal - 1 else 4
                val indexMinus2: Int = if (newVal - 2 >= 0) newVal - 2 else newVal + 4 - 1
                //decrements the last two values of the picker to be -1/-2 days from the current val of the picker
                calendars[indexMinus1].timeInMillis = calendars[newVal].timeInMillis - 8.64e+7.toLong()
                calendars[indexMinus2].timeInMillis = calendars[newVal].timeInMillis - (2 * 8.64e+7).toLong()
                //updates the date list to display the correct dates
                dates[indexMinus1] = format(calendars[indexMinus1])
                dates[indexMinus2] = format(calendars[indexMinus2])
            }
            viewModel.onCalendarUpdated(calendars[newVal])
        }
    }

    private fun setupHourPicker(hourPicker: NumberPicker, calendar: Calendar) {
        with(hourPicker) {
            value = calendar.get(Calendar.HOUR)
            setOnValueChangedListener { picker, oldVal, newVal ->
                calendar.set(Calendar.HOUR, newVal)
                viewModel.onCalendarUpdated(calendar)
            }
        }
    }

    private fun setupMinutePicker(minutePicker: NumberPicker, calendar: Calendar) {
        val minutes = Array(60) {i -> i.toString() }
        for (i in 0..9) {
            minutes[i] = "0${minutes[i]}"
        }
        with(minutePicker) {
            value = calendar.get(Calendar.MINUTE)
            displayedValues = minutes
            setOnValueChangedListener { picker, oldVal, newVal ->
                calendar.set(Calendar.MINUTE, newVal)
                viewModel.onCalendarUpdated(calendar)
            }
        }
    }

    private fun setupAmpmPicker(ampmPicker: NumberPicker, calendar: Calendar) {
        val ampm = arrayOf("AM","PM")
        with(ampmPicker) {
            displayedValues = ampm
            value = when(calendar.get(Calendar.AM_PM)) {
                Calendar.AM -> 1
                else -> 2
            }
            setOnValueChangedListener { picker, oldVal, newVal ->
                calendar.set(Calendar.AM_PM, if (newVal == 1) Calendar.AM else Calendar.PM)
                viewModel.onCalendarUpdated(calendar)
            }
        }
    }

}