package com.kiwicorp.dumbdue.ui.settings.edittimesetbuttons.editincrementaltimesetter

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
import com.kiwicorp.dumbdue.databinding.FragmentEditIncrementalTimeSetterBinding
import com.kiwicorp.dumbdue.ui.settings.edittimesetbuttons.EditTimeSettersViewModel
import com.kiwicorp.dumbdue.util.RoundedDaggerBottomSheetDialogFragment
import com.shawnlin.numberpicker.NumberPicker
import org.threeten.bp.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.math.absoluteValue

class EditIncrementalTimeSetterFragment : RoundedDaggerBottomSheetDialogFragment() {

    private lateinit var binding: FragmentEditIncrementalTimeSetterBinding

    private val args: EditIncrementalTimeSetterFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel : EditIncrementalTimeSetterViewModel by viewModels { viewModelFactory }

    private val editTimeSetterViewModel: EditTimeSettersViewModel by activityViewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_edit_incremental_time_setter, container, false)
        binding = FragmentEditIncrementalTimeSetterBinding.bind(root)
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
        setupPlusMinusPicker(binding.pickerPlusMinus)
        setupNumberPicker(binding.pickerNumber)
        setupUnitPicker(binding.pickerUnits, binding.pickerNumber)
    }

    private fun setupPlusMinusPicker(plusMinusPicker: NumberPicker) {
        val plusMinus = arrayOf("+","-")
        with(plusMinusPicker) {
            typeface = ResourcesCompat.getFont(requireContext(),R.font.rubik)
            displayedValues = plusMinus
            viewModel.incrementalTimeSetter.observe(viewLifecycleOwner, Observer {
                value = if (it.number > 0) 1 else 2
            })
            setOnValueChangedListener { _, _, newVal ->
                viewModel.onSignChanged( if (newVal == 1) Sign.POSITIVE else Sign.NEGATIVE )
            }
        }
    }

    private fun setupNumberPicker(numberPicker: NumberPicker) {
        with(numberPicker) {
            typeface = ResourcesCompat.getFont(requireContext(),R.font.rubik)
            viewModel.incrementalTimeSetter.observe(viewLifecycleOwner, Observer {
                value = it.number.absoluteValue.toInt()
            })
            setOnValueChangedListener { _, _, newVal ->
                viewModel.onNumberChanged(newVal.toLong())
            }
        }
    }

    private fun setupUnitPicker(unitPicker: NumberPicker, numberPicker: NumberPicker) {
        val units = arrayOf("min","hr","day","wk","mo","yr")
        val unitValues = listOf(ChronoUnit.MINUTES, ChronoUnit.HOURS, ChronoUnit.DAYS, ChronoUnit.WEEKS,ChronoUnit.MONTHS, ChronoUnit.YEARS)

        with(unitPicker) {
            typeface = ResourcesCompat.getFont(requireContext(),R.font.rubik)
            displayedValues = units

            viewModel.incrementalTimeSetter.observe(viewLifecycleOwner, Observer {
                value = unitValues.indexOf(it.unit) + 1
            })
            setOnValueChangedListener { _, _, newVal ->
                val unit = unitValues[newVal - 1]
                viewModel.onUnitChanged(unit)
                numberPicker.maxValue = when(unit) {
                    ChronoUnit.MINUTES -> 59
                    ChronoUnit.HOURS -> 23
                    ChronoUnit.DAYS -> 6
                    ChronoUnit.WEEKS -> 3
                    ChronoUnit.MONTHS -> 11
                    else -> 100
                }
                //update number in case numberPicker's new maxValue was less than numberPicker's value
                viewModel.onNumberChanged(numberPicker.value.toLong())
            }
        }
    }
}

