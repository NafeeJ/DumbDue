package com.kiwicorp.dumbdue.ui.settings.edittimesetbuttons.editquickaccesstimesetter

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentEditQuickAccessTimeSetterBinding
import com.kiwicorp.dumbdue.timesetters.AmPm
import com.kiwicorp.dumbdue.ui.settings.edittimesetbuttons.EditTimeSettersViewModel
import com.kiwicorp.dumbdue.util.RoundedDaggerBottomSheetDialogFragment
import com.shawnlin.numberpicker.NumberPicker
import javax.inject.Inject

class EditQuickAccessTimeSetterFragment : RoundedDaggerBottomSheetDialogFragment() {

    private lateinit var binding: FragmentEditQuickAccessTimeSetterBinding

    private val args: EditQuickAccessTimeSetterFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: EditQuickAccessTimeSetterViewModel by viewModels { viewModelFactory }

    private val editTimeSetterViewModel: EditTimeSettersViewModel by activityViewModels { viewModelFactory }

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

    override fun onDismiss(dialog: DialogInterface) {
        editTimeSetterViewModel.notifyTimeSettersUpdated()
        super.onDismiss(dialog)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.loadTimeSetter(args.key)
        setupAmpmPicker(binding.pickerAmPm)
        setupMinutePicker(binding.pickerMinutes)
        setupHourPicker(binding.pickerHours)
    }

    private fun setupAmpmPicker(ampmPicker: NumberPicker) {
        val ampm = arrayOf("AM","PM")
        ampmPicker.apply {
            typeface = ResourcesCompat.getFont(requireContext(),R.font.rubik)
            displayedValues = ampm
            viewModel.quickAccessTimeSetter.observe(viewLifecycleOwner, Observer {
                value = it.amPm.ordinal + 1
            })
            setOnValueChangedListener { _, _, newVal ->
                viewModel.onAmPmChanged(if (newVal == 1) AmPm.AM else AmPm.PM)
            }
        }
    }

    private fun setupMinutePicker(minutePicker: NumberPicker) {
        val minutes = Array(60) { if (it < 10) "0$it" else "$it" }
        minutePicker.apply {
            typeface = ResourcesCompat.getFont(requireContext(),R.font.rubik)
            displayedValues = minutes
            viewModel.quickAccessTimeSetter.observe(viewLifecycleOwner, Observer {
                value = it.minute
            })
            setOnValueChangedListener { _, _, newVal ->
                viewModel.onMinuteChanged(newVal)
            }
        }

    }

    private fun setupHourPicker(hourPicker: NumberPicker) {
        val hours = Array(12) { if(it < 9) "0${it + 1}" else "${it + 1}" }
        hourPicker.apply {
            typeface = ResourcesCompat.getFont(requireContext(),R.font.rubik)
            displayedValues = hours
            viewModel.quickAccessTimeSetter.observe(viewLifecycleOwner, Observer {
                value = it.hour
            })
            setOnValueChangedListener { _, _, newVal ->
                viewModel.onHourChanged(newVal)
            }
        }
    }

}