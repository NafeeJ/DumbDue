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
import com.kiwicorp.dumbdue.data.repeat.RepeatDailyInterval
import com.kiwicorp.dumbdue.data.repeat.RepeatMonthlyByNumberInterval
import com.kiwicorp.dumbdue.data.repeat.RepeatWeeklyInterval
import com.kiwicorp.dumbdue.data.repeat.RepeatYearlyByNumberInterval
import com.kiwicorp.dumbdue.databinding.FragmentChooseRepeatBinding
import com.kiwicorp.dumbdue.util.RoundedDaggerBottomSheetDialogFragment
import com.kiwicorp.dumbdue.util.getNavGraphViewModel
import org.threeten.bp.DayOfWeek
import org.threeten.bp.YearMonth
import org.threeten.bp.temporal.TemporalAdjusters
import javax.inject.Inject

class ChooseRepeatFragment : RoundedDaggerBottomSheetDialogFragment() {

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
        viewModel.eventRepeatChosen.observe(viewLifecycleOwner, EventObserver {
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
        val time = dueDate.toLocalTime()
        val date = dueDate.toLocalDate()
        //todo move these to view model
        binding.repeatOffText.apply {
            text = getString(R.string.repeat_off)

            setOnClickListener { viewModel.onChooseRepeatInterval(null) }
        }

        binding.repeatDailyText.apply {
            val repeatInterval = RepeatDailyInterval(
                1,
                time,
                date)

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
            val thisSunday = dueDate.toLocalDate()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
            val repeatInterval = RepeatWeeklyInterval(
                1,
                time,
                thisSunday
                ,weekDays)

            text = repeatInterval.toString()

            setOnClickListener { viewModel.onChooseRepeatInterval(repeatInterval) }
        }

        binding.repeatWeeklyText.apply {
            val thisSunday = dueDate.toLocalDate()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
            val repeatInterval = RepeatWeeklyInterval(
                1,
                time,
                thisSunday,
                listOf(dueDate.dayOfWeek))

            text = repeatInterval.toString()

            setOnClickListener { viewModel.onChooseRepeatInterval(repeatInterval) }
        }

        binding.repeatMonthlyText.apply {
            val repeatInterval = RepeatMonthlyByNumberInterval(
                1,
                YearMonth.from(dueDate),
                time,
                listOf(dueDate.dayOfMonth)
            )

            text = repeatInterval.toString()

            setOnClickListener { viewModel.onChooseRepeatInterval(repeatInterval) }
        }

        binding.repeatYearlyText.apply {
            val repeatInterval = RepeatYearlyByNumberInterval(1, time, date)
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