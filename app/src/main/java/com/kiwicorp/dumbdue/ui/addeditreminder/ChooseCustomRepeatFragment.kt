package com.kiwicorp.dumbdue.ui.addeditreminder

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.data.repeat.RepeatMonthlyByCount.Day
import com.kiwicorp.dumbdue.databinding.FragmentChooseCustomRepeatBinding
import com.kiwicorp.dumbdue.util.NoScrollGridLayoutManager
import com.kiwicorp.dumbdue.util.daggerext.DaggerBottomSheetDialogFragment
import com.kiwicorp.dumbdue.util.getNavGraphViewModel
import javax.inject.Inject

class ChooseCustomRepeatFragment : DaggerBottomSheetDialogFragment(), DayItemClickListener, OnDayDeletedListener {

    private val args: ChooseCustomRepeatFragmentArgs by navArgs()

    lateinit var binding: FragmentChooseCustomRepeatBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: AddEditReminderViewModel

    private val monthlyByNumberDays = List(32) { DayItem(it + 1) }

    private val monthlyByCountDays = mutableListOf(Day())

    private val monthlyByNumberCalendarAdapter = ChooseRepeatMonthlyByNumberCalendarAdapter(this)

    private val monthlyByCountRecyclerViewAdapter = ChooseMonthlyRepeatByCountAdapter(this)

    private val options = listOf("By the count of the day of the week in the month", "By the number of the day in the month")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = getNavGraphViewModel(args.graphId)
        val root = layoutInflater.inflate(R.layout.fragment_choose_custom_repeat,container,false)
        binding = FragmentChooseCustomRepeatBinding.bind(root).apply {
            viewmodel = viewModel
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFrequencyText()
        setupScopeText()
        setupStartText()
        setupMonthlyOptionsText()
        setupMonthlyByNumber()
        setupMonthlyByCount()
        setupYearlyOptionsText()
        setupYearlyByCount()
        setupYearlyByNumber()
    }

    private fun setupStartText() {

    }

    private fun setupScopeText() {
        val scopes = listOf("days","weeks","months","years")
        val adapter = ArrayAdapter(requireContext(), R.layout.item_drop_down_menu, scopes)
        (binding.scopeText.editText as? AutoCompleteTextView)?.apply {
            setAdapter(adapter)
            setOnItemClickListener { parent, view, position, id ->
                binding.mainLayout.visibility = View.VISIBLE
                when (position) {
                    0 -> {
                        binding.chooseWeeklyLayout.root.visibility = View.GONE
                        binding.chooseMonthlyLayout.root.visibility = View.GONE
                        binding.chooseYearlyLayout.root.visibility = View.GONE
                    }
                    1 -> {
                        binding.chooseWeeklyLayout.root.visibility = View.VISIBLE
                        binding.chooseMonthlyLayout.root.visibility = View.GONE
                        binding.chooseYearlyLayout.root.visibility  = View.GONE
                    }
                    2 -> {
                        binding.chooseWeeklyLayout.root.visibility = View.GONE
                        binding.chooseMonthlyLayout.root.visibility = View.VISIBLE
                        binding.chooseYearlyLayout.root.visibility = View.GONE
                    }
                    3 -> {
                        binding.chooseWeeklyLayout.root.visibility = View.GONE
                        binding.chooseMonthlyLayout.root.visibility = View.GONE
                        binding.chooseYearlyLayout.root.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setupFrequencyText() {
        //closes keyboard if keyboard is open and the user clicks on a different text field
        binding.frequencyText.editText?.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                v?.let {
                    val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(it.windowToken, 0)
                }
            }
        }
    }

    private fun setupMonthlyOptionsText() {
        val adapter = ArrayAdapter(requireContext(), R.layout.item_drop_down_menu, options)
        (binding.chooseMonthlyLayout.monthlyOptionsText.editText as? AutoCompleteTextView)?.apply {
            setAdapter(adapter)
            setOnItemClickListener { parent, view, position, id ->
                if (position == 0) {
                    binding.chooseMonthlyLayout.monthlyByCountGroup.visibility = View.VISIBLE
                    binding.chooseMonthlyLayout.monthlyByNumberCalendar.visibility = View.GONE
                } else {
                    binding.chooseMonthlyLayout.monthlyByCountGroup.visibility = View.GONE
                    binding.chooseMonthlyLayout.monthlyByNumberCalendar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupMonthlyByNumber() {
        binding.chooseMonthlyLayout.monthlyByNumberCalendar.apply {
            setHasFixedSize(true)
            adapter = monthlyByNumberCalendarAdapter
            monthlyByNumberCalendarAdapter.days = monthlyByNumberDays
            layoutManager = NoScrollGridLayoutManager(requireContext(),7)
        }
    }

    private fun setupMonthlyByCount() {
        binding.chooseMonthlyLayout.monthlyByCountRecyclerView.adapter = monthlyByCountRecyclerViewAdapter
        monthlyByCountRecyclerViewAdapter.daysInMonth = monthlyByCountDays

        binding.chooseMonthlyLayout.monthlyByCountAddButton.setOnClickListener {
            monthlyByCountDays.add(Day())
            monthlyByCountRecyclerViewAdapter.notifyItemInserted(monthlyByCountDays.lastIndex)
        }
    }

    private fun setupYearlyOptionsText() {
        val adapter = ArrayAdapter(requireContext(), R.layout.item_drop_down_menu, options)
        (binding.chooseYearlyLayout.yearlyOptionsText.editText as? AutoCompleteTextView)?.apply {
            setAdapter(adapter)
            setOnItemClickListener { parent, view, position, id ->
                if (position == 0) {
                    binding.chooseYearlyLayout.yearlyByCountGroup.visibility = View.VISIBLE
                    binding.chooseYearlyLayout.yearlyByNumberGroup.visibility = View.GONE
                } else {
                    binding.chooseYearlyLayout.yearlyByNumberGroup.visibility = View.VISIBLE
                    binding.chooseYearlyLayout.yearlyByCountGroup.visibility = View.GONE
                }
            }
        }
    }

    private fun setupYearlyByNumber() {
        val months = listOf("January","February","March","April","May","June","July","August","September","October","November","December")
        var daysOfMonth = List(31) {it + 1}

        val monthsAdapter = ArrayAdapter(requireContext(), R.layout.item_drop_down_menu, months)
        val daysOfMonthAdapter = ArrayAdapter(requireContext(), R.layout.item_drop_down_menu,daysOfMonth)

        (binding.chooseYearlyLayout.yearlyByNumberMonthText.editText as? AutoCompleteTextView)?.apply {
            setAdapter(monthsAdapter)
            setOnItemClickListener { parent, view, position, id ->
                val size = when (months[position]) {
                    "January" -> 31
                    "February" -> 29
                    "March" -> 31
                    "April" -> 30
                    "May" -> 31
                    "June" -> 30
                    "July" -> 31
                    "August" -> 31
                    "September" -> 30
                    "October" -> 31
                    "November" -> 30
                    else -> 31
                }
                daysOfMonth = List(size) {it + 1}
                daysOfMonthAdapter.notifyDataSetChanged()
                with(binding.chooseYearlyLayout.yearlyByNumberDayText.editText?.text.toString().toIntOrNull()) {
                    if (this != null && this > size) {
                        (binding.chooseYearlyLayout.yearlyByNumberDayText.editText as? AutoCompleteTextView)?.setText(size.toString(),false)
                    }
                }
            }
        }
        (binding.chooseYearlyLayout.yearlyByNumberDayText.editText as? AutoCompleteTextView)?.apply {
            setAdapter(daysOfMonthAdapter)
        }
    }

    private fun setupYearlyByCount() {
        val counts = listOf("First","Second","Third","Fourth","Last")
        val daysOfTheWeek = listOf("Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday")
        val months = listOf("January","February","March","April","May","June","July","August","September","October","November","December")

        val countAdapter = ArrayAdapter(requireContext(),R.layout.item_drop_down_menu,counts)
        val daysOfWeekAdapter = ArrayAdapter(requireContext(),R.layout.item_drop_down_menu,daysOfTheWeek)
        val monthsAdapter = ArrayAdapter(requireContext(),R.layout.item_drop_down_menu, months)

        (binding.chooseYearlyLayout.yearlyByCountCountText.editText as? AutoCompleteTextView)?.apply {
            setAdapter(countAdapter)
            setOnItemClickListener { parent, view, position, id ->

            }
        }
        (binding.chooseYearlyLayout.yearlyByCountDayOfWeekText.editText as? AutoCompleteTextView)?.apply {
            setAdapter(daysOfWeekAdapter)
            setOnItemClickListener { parent, view, position, id ->
                setText(daysOfTheWeek[position].substring(0..2),false)
            }
        }
        (binding.chooseYearlyLayout.yearlyByCountMonthText.editText as? AutoCompleteTextView)?.apply {
            setAdapter(monthsAdapter)
            setOnItemClickListener { parent, view, position, id ->
                setText(months[position].substring(0..2),false)
            }
        }
    }


    override fun onDayItemClicked(day: DayItem) {
        val index = day.number - 1
        monthlyByNumberDays[index].isChecked = !day.isChecked
        monthlyByNumberCalendarAdapter.notifyItemChanged(index)
    }

    override fun onDayDeleted(day: Day) {
        monthlyByCountDays.remove(day)
        monthlyByCountRecyclerViewAdapter.notifyDataSetChanged()
    }

}