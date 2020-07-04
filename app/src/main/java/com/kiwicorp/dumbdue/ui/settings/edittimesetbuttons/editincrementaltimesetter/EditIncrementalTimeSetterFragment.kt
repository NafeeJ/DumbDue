package com.kiwicorp.dumbdue.ui.settings.edittimesetbuttons.editincrementaltimesetter

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
import com.kiwicorp.dumbdue.databinding.FragmentEditIncrementalTimeSetterBinding
import com.shawnlin.numberpicker.NumberPicker
import dagger.android.support.DaggerFragment
import org.threeten.bp.temporal.ChronoField
import org.threeten.bp.temporal.ChronoUnit
import java.util.*
import javax.inject.Inject
import kotlin.math.absoluteValue

class EditIncrementalTimeSetterFragment : DaggerFragment() {

    private lateinit var binding: FragmentEditIncrementalTimeSetterBinding

    private val args: EditIncrementalTimeSetterFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel : EditIncrementalTimeSetterViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_edit_incremental_time_setter, container, false)
        binding = FragmentEditIncrementalTimeSetterBinding.bind(root)
        binding.viewmodel = viewModel
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.loadTimeSetter(args.key)
        setupNavigation()
        setupPlusMinusPicker(binding.pickerPlusMinus)
        setupNumberPicker(binding.pickerNumber)
        setupUnitPicker(binding.pickerUnits, binding.pickerNumber)
    }

    private fun setupNavigation() {
        viewModel.eventDone.observe(viewLifecycleOwner, EventObserver {
            close()
        })
    }

    private fun close() {
        findNavController().popBackStack()
    }

    private fun setupPlusMinusPicker(plusMinusPicker: NumberPicker) {
        val plusMinus = arrayOf("+","-")
        with(plusMinusPicker) {
            displayedValues = plusMinus
            value = if (viewModel.incrementalTimeSetter.number > 0) 1 else 2
            setOnValueChangedListener(viewModel.plusMinusPickerOnValueChangedListener)
        }
    }

    private fun setupNumberPicker(numberPicker: NumberPicker) {
        with(numberPicker) {
            value = viewModel.incrementalTimeSetter.number.absoluteValue.toInt()
            setOnValueChangedListener(viewModel.numberPickerOnValueChangedListener)
        }
    }

    private fun setupUnitPicker(unitPicker: NumberPicker, numberPicker: NumberPicker) {
        val units = arrayOf("min","hr","day","wk","mo","yr")
        with(unitPicker) {
            displayedValues = units
            value = when(viewModel.incrementalTimeSetter.unit) {
                ChronoUnit.MINUTES -> 1
                ChronoUnit.HOURS -> 2
                ChronoUnit.DAYS -> 3
                ChronoUnit.WEEKS -> 4
                ChronoUnit.MONTHS -> 5
                else -> 6
            }
            setOnValueChangedListener(viewModel.getUnitPickerOnValueChangeListener(numberPicker))
        }
    }
}