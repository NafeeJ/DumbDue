package com.kiwicorp.dumbdue.ui.addeditreminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kiwicorp.dumbdue.EventObserver
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.data.repeat.*
import com.kiwicorp.dumbdue.databinding.FragmentChooseRepeatBinding
import com.kiwicorp.dumbdue.util.daggerext.DaggerBottomSheetDialogFragment
import com.kiwicorp.dumbdue.util.getNavGraphViewModel
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.YearMonth
import org.threeten.bp.temporal.TemporalAdjusters
import java.util.*
import javax.inject.Inject

class ChooseRepeatFragment : DaggerBottomSheetDialogFragment() {

    private lateinit var binding: FragmentChooseRepeatBinding

    private val args: ChooseRepeatFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    // Shares ViewModel with Add/Edit ReminderFragment
    private lateinit var viewModel: AddEditReminderViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = getNavGraphViewModel(args.graphId) { viewModelFactory }
        val root = inflater.inflate(R.layout.fragment_choose_repeat,container,false)
        binding = FragmentChooseRepeatBinding.bind(root).apply {
            viewmodel = viewModel
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupTextViews()
        setupNavigation()
    }

    private fun setupNavigation() {
        viewModel.eventChooseRepeat.observe(viewLifecycleOwner, EventObserver {
            close()
        })
    }

    /**
     * Closes this Dialog
     */
    private fun close() {
        findNavController().popBackStack()
    }

    private fun setupTextViews() {
        val dueDate = viewModel.dueDate.value!!

        binding.repeatOffText.apply {
            text = getString(R.string.repeat_off)

            setOnClickListener { viewModel.onChooseRepeatInterval(null) }
        }

        binding.repeatDailyText.apply {
            val repeatInterval = RepeatDaily(
                1,
                viewModel.dueDate.value!!.toLocalDateTime())

            text = repeatInterval.toString()

            setOnClickListener { viewModel.onChooseRepeatInterval(repeatInterval) }
        }

        binding.repeatWeekdaysText.apply {
            val weekDays = listOf(
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY
            )
            val thisSunday = dueDate.toLocalDateTime()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
            val repeatInterval = RepeatWeekly(1,thisSunday,weekDays)

            text = repeatInterval.toString()

            setOnClickListener { viewModel.onChooseRepeatInterval(repeatInterval) }
        }

        binding.repeatWeeklyText.apply {
            val thisSunday = dueDate.toLocalDateTime()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
            val repeatInterval = RepeatWeekly(1,thisSunday,listOf(dueDate.dayOfWeek))

            text = repeatInterval.toString()

            setOnClickListener { viewModel.onChooseRepeatInterval(repeatInterval) }
        }

        binding.repeatMonthlyText.apply {
            val repeatInterval = RepeatMonthlyByNumber(
                1,
                YearMonth.from(dueDate),
                LocalTime.from(dueDate),
                listOf(dueDate.dayOfMonth)
            )

            text = repeatInterval.toString()

            setOnClickListener { viewModel.onChooseRepeatInterval(repeatInterval) }
        }

        binding.repeatYearlyText.apply {
            val repeatInterval = RepeatYearlyByNumber(1, LocalDateTime.from(dueDate))
            text = repeatInterval.toString()
            setOnClickListener { viewModel.onChooseRepeatInterval(repeatInterval) }
        }

        binding.repeatCustomText.apply {
            text = getString(R.string.repeat_custom)
            setOnClickListener {
                navigateToCustomRepeatMenu()
            }
        }

    }

    private fun navigateToCustomRepeatMenu() {
        val action = ChooseRepeatFragmentDirections.actionChooseRepeatFragmentDestToCustomRepeatFragment(args.graphId)
        findNavController().navigate(action)
    }
}