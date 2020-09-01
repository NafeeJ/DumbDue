package com.kiwicorp.dumbdue.ui.addeditreminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.navArgs
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentTimePickerBinding
import com.kiwicorp.dumbdue.util.RoundedBottomSheetDialogFragment
import com.kiwicorp.dumbdue.util.getNavGraphViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoField

@AndroidEntryPoint
class TimePickerFragment : RoundedBottomSheetDialogFragment() {

    private lateinit var binding: FragmentTimePickerBinding

    private val args: TimePickerFragmentArgs by navArgs()

    private lateinit var viewModel: AddEditReminderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = getNavGraphViewModel(args.graphId)
        val root = inflater.inflate(R.layout.fragment_time_picker, container, false)
        binding = FragmentTimePickerBinding.bind(root).apply {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = viewModel
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupDatePicker()
        setupHourPicker()
        setupMinutePicker()
        setupAmpmPicker()
    }

    private fun setupDatePicker() {
        // a list of 5 dates where each calendar is a day apart and the calendar at [2] is the
        // current due date
        val dates = MutableList(5) { LocalDate.from(viewModel.dueDate.value!!).plusDays( it - 2L) }

        val dateFormatter = DateTimeFormatter.ofPattern("EEE MMM d")
        //return the date of the calendar formatted. If the calendar's date is today, return "Today"
        fun format(date: LocalDate): String {
            return if (date == LocalDate.now()) {
                "Today"
            } else {
                dateFormatter.format(date)
            }
        }
        // a list of the 5 calendars from [calendars] formatted
        val datesStr = Array(5) { format(dates[it]) }
        with(binding.datePicker) {
            typeface = ResourcesCompat.getFont(requireContext(),R.font.rubik)
            displayedValues = datesStr

            setOnValueChangedListener { _, oldVal, newVal ->
                if (oldVal < newVal || (oldVal == 4 && newVal == 0)) { //if the date picker is increasing
                    //gets the proper index of the new val +1/+2
                    val indexPlus1: Int = if (newVal + 1 <= 4) newVal + 1 else 0
                    val indexPlus2: Int = if (newVal + 2 <= 4) newVal + 2 else newVal - 4 + 1
                    //increments the next two values of the picker to be +1/+2 days from the current val of the picker
                    dates[indexPlus1] = dates[newVal].plusDays(1)
                    dates[indexPlus2] = dates[newVal].plusDays(2)
                    //updates the date list to display the correct calendar dates
                    datesStr[indexPlus1] = format(dates[indexPlus1])
                    datesStr[indexPlus2] = format(dates[indexPlus2])
                } else { //if the date picker is decreasing
                    //gets the proper index of the new val -1/-2
                    val indexMinus1: Int = if (newVal - 1 >= 0) newVal - 1 else 4
                    val indexMinus2: Int = if (newVal - 2 >= 0) newVal - 2 else newVal + 4 - 1
                    //decrements the last two values of the picker to be -1/-2 days from the current val of the picker
                    dates[indexMinus1] = dates[newVal].minusDays(1)
                    dates[indexMinus2] = dates[newVal].minusDays(2)
                    //updates the date list to display the correct dates
                    datesStr[indexMinus1] = format(dates[indexMinus1])
                    datesStr[indexMinus2] = format(dates[indexMinus2])
                }
                viewModel.updateDueDate(viewModel.dueDate.value!!.with(dates[newVal]))
            }
        }

    }

    private fun setupHourPicker() {
        with(binding.hourPicker) {
            typeface = ResourcesCompat.getFont(requireContext(),R.font.rubik)
            value = viewModel.dueDate.value!!.get(ChronoField.HOUR_OF_AMPM)
            setOnValueChangedListener { _, _, newVal ->
                viewModel.updateDueDate(ZonedDateTime.from(viewModel.dueDate.value!!).with(ChronoField.HOUR_OF_AMPM, if (newVal == 12) 0 else newVal.toLong()))
            }
        }
    }

    private fun setupMinutePicker() {
        val minutes = Array(60) {i -> i.toString() }
        for (i in 0..9) {
            minutes[i] = "0${minutes[i]}"
        }
        with(binding.minutePicker) {
            typeface = ResourcesCompat.getFont(requireContext(),R.font.rubik)
            value = viewModel.dueDate.value!!.minute
            displayedValues = minutes
            setOnValueChangedListener { _, _, newVal ->
                viewModel.updateDueDate(ZonedDateTime.from(viewModel.dueDate.value!!).withMinute(newVal))
            }
        }
    }

    private fun setupAmpmPicker() {
        val ampm = arrayOf("AM","PM")
        with(binding.ampmPicker) {
            typeface = ResourcesCompat.getFont(requireContext(),R.font.rubik)
            displayedValues = ampm
            value = when(viewModel.dueDate.value!!.get(ChronoField.AMPM_OF_DAY)) {
                0 -> 1 // AM
                else -> 2 // PM
            }
            setOnValueChangedListener { _, _, newVal ->
                viewModel.updateDueDate(ZonedDateTime.from(viewModel.dueDate.value!!).with(ChronoField.AMPM_OF_DAY,newVal - 1L))
            }
        }
    }

}