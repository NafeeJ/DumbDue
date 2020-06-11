package com.kiwicorp.dumbdue.ui.settings.edittimesetbuttons.editquickaccesstimesetter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kiwicorp.dumbdue.EventObserver
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentEditQuickAccessTimeSetterBinding
import com.shawnlin.numberpicker.NumberPicker
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class EditQuickAccessTimeSetterFragment : DaggerFragment() {

    private lateinit var binding: FragmentEditQuickAccessTimeSetterBinding

    private val args: EditQuickAccessTimeSetterFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: EditQuickAccessTimeSetterViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_edit_quick_access_time_setter, container, false)
        binding = FragmentEditQuickAccessTimeSetterBinding.bind(root)
        binding.viewmodel = viewModel
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.loadQuickAccessTimeSetter(args.key)
        setupNavigation()
        setupAmpmPicker(binding.pickerAmPm)
        setupMinutePicker(binding.pickerMinutes)
        setupHourPicker(binding.pickerHours)
    }

    private fun setupNavigation() {
        viewModel.eventDone.observe(viewLifecycleOwner, EventObserver{
            close()
        })
    }

    private fun close() {
        findNavController().popBackStack()
    }

    private fun setupAmpmPicker(ampmPicker: NumberPicker) {
        val ampm = arrayOf("AM","PM")
        ampmPicker.displayedValues = ampm
        ampmPicker.value = if (viewModel.quickAccessTimeSetter.hourOfDay < 12) 1 else 2
        ampmPicker.setOnValueChangedListener(viewModel.ampmPickerSetOnValueChangedListener)
    }

    private fun setupMinutePicker(minutePicker: NumberPicker) {
        val minutes = Array(60) { if (it < 10) "0$it" else "$it" }
        minutePicker.displayedValues = minutes
        minutePicker.value = viewModel.quickAccessTimeSetter.min
        minutePicker.setOnValueChangedListener(viewModel.minutePickerOnValueChangedListener)
    }

    private fun setupHourPicker(hourPicker: NumberPicker) {
        val hours = Array(12) { if(it < 9) "0${it + 1}" else "${it + 1}" }
        hourPicker.displayedValues = hours
        val hourOfDay = viewModel.quickAccessTimeSetter.hourOfDay
        hourPicker.value = if (hourOfDay > 12) hourOfDay - 12 else hourOfDay
        hourPicker.setOnValueChangedListener(viewModel.hourPickerOnValueChangedListener)
    }

}