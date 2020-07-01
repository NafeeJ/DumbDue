package com.kiwicorp.dumbdue.ui.addeditreminder.customrepeat

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.TimePicker
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.data.repeat.RepeatMonthlyByCount.Day
import com.kiwicorp.dumbdue.databinding.FragmentChooseCustomRepeatBinding
import com.kiwicorp.dumbdue.ui.addeditreminder.*
import com.kiwicorp.dumbdue.util.NoScrollGridLayoutManager
import com.kiwicorp.dumbdue.util.daggerext.DaggerBottomSheetDialogFragment
import com.kiwicorp.dumbdue.util.getDropDownMenuAdapter
import com.kiwicorp.dumbdue.util.getNavGraphViewModel
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class ChooseCustomRepeatFragment : DaggerBottomSheetDialogFragment(),
    DayItemClickListener,
    OnDayDeletedListener {

    private val args: ChooseCustomRepeatFragmentArgs by navArgs()

    lateinit var binding: FragmentChooseCustomRepeatBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var addEditViewModel: AddEditReminderViewModel

    private lateinit var chooseCustomRepeatViewModel: ChooseCustomRepeatViewModel

    val daysByNumber = List(32) { DayItem(it + 1) }

    private val monthlyByNumberCalendarAdapter = ChooseRepeatMonthlyByNumberCalendarAdapter(this)

    private val monthlyByCountRecyclerViewAdapter = ChooseMonthlyRepeatByCountAdapter(this)

    private val options = listOf("By count of day of week in month", "By number of day in month")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        chooseCustomRepeatViewModel = getNavGraphViewModel(args.graphId) { viewModelFactory }
        addEditViewModel = getNavGraphViewModel(args.graphId) { viewModelFactory }
        val root = layoutInflater.inflate(R.layout.fragment_choose_custom_repeat, container,false)
        binding = FragmentChooseCustomRepeatBinding.bind(root).apply {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = chooseCustomRepeatViewModel
            chooseDailyLayout.viewmodel = chooseCustomRepeatViewModel.chooseDailyViewModel
            chooseWeeklyLayout.viewmodel = chooseCustomRepeatViewModel.chooseWeeklyViewModel
            chooseMonthlyLayout.viewmodel = chooseCustomRepeatViewModel.chooseMonthlyViewModel
            chooseYearlyLayout.viewmodel = chooseCustomRepeatViewModel.chooseYearlyViewModel
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.doneButton.setOnClickListener {
            addEditViewModel.onChooseRepeatInterval(chooseCustomRepeatViewModel.getRepeatInterval())
            findNavController().popBackStack()
        }
        binding.timeText.setOnClickListener {
            val onTimeSetListener = TimePickerDialog.OnTimeSetListener { timePicker: TimePicker, hourOfDay: Int, minute: Int ->
                chooseCustomRepeatViewModel.time.value = LocalTime.of(hourOfDay, minute)
            }
            val now = LocalTime.now()
            val timePickerDialog = TimePickerDialog(
                requireContext(),
                onTimeSetListener,
                now.hour,
                now.minute,
                false
            )
            timePickerDialog.show()
        }
        setupFrequencyText()
        setupTypeText()
        setupStartDateTexts()
        setupWeekly()
        setupMonthly()
        setupMonthlyByNumber()
        setupMonthlyByCount()
        setupYearlyOptionsText()
        setupYearlyByCount()
        setupYearlyByNumber()
    }

    private fun setupStartDateTexts() {
        with(ChooseCustomRepeatFragmentDirections) {
            binding.chooseDailyLayout.startDateText.setOnClickListener {
                val action = actionCustomRepeatFragmentToChooseDailyStartDateFragment(args.graphId)
                findNavController().navigate(action)
            }
            binding.chooseWeeklyLayout.startDateText.setOnClickListener {
                val action = actionCustomRepeatFragmentToChooseWeeklyStartDateFragment(args.graphId)
                findNavController().navigate(action)
            }
        }
        val today = LocalDate.now()

        val months = getMonths()
        //list of the months with year next to it ex: "June 2020"
        val monthsStr = List(12) {
            "${months[it].getDisplayName(TextStyle.FULL, Locale.ENGLISH)} " +
                    "${today.year + if (months[it] < today.month) 1 else 0}" }
        (binding.chooseMonthlyLayout.startDateTextLayout.editText as? AutoCompleteTextView)?.apply {
                setAdapter(getDropDownMenuAdapter(monthsStr))
                doOnTextChanged { text, start, before, count ->
                    with(text.toString()) {
                        val year = this.substringAfter(' ').toInt()
                        val month: Month = Month.valueOf(this.substringBefore(' ').toUpperCase())
                        chooseCustomRepeatViewModel.chooseMonthlyViewModel.startingYearMonth = YearMonth.of(year, month)
                    }
                }
            }

        val years = List(11) {today.year + it}
        (binding.chooseYearlyLayout.startDateTextLayout.editText as? AutoCompleteTextView)?.apply {
            setAdapter(getDropDownMenuAdapter(years))
            doOnTextChanged { text, start, before, count ->
                chooseCustomRepeatViewModel.chooseYearlyViewModel.startingYear = text.toString().toInt()
            }
        }

    }

    private fun setupTypeText() {
        val types = listOf("days","weeks","months","years")
        (binding.typeTextLayout.editText as? AutoCompleteTextView)?.apply {
            setAdapter(getDropDownMenuAdapter(types))
            setOnItemClickListener { parent, view, position, id ->
                binding.mainLayout.visibility = View.VISIBLE
                // make corresponding layout visible and the rest invisible
                when (position) {
                    0 -> {
                        binding.chooseDailyLayout.root.visibility = View.VISIBLE
                        binding.chooseWeeklyLayout.root.visibility = View.GONE
                        binding.chooseMonthlyLayout.root.visibility = View.GONE
                        binding.chooseYearlyLayout.root.visibility = View.GONE
                    }
                    1 -> {
                        binding.chooseDailyLayout.root.visibility = View.GONE
                        binding.chooseWeeklyLayout.root.visibility = View.VISIBLE
                        binding.chooseMonthlyLayout.root.visibility = View.GONE
                        binding.chooseYearlyLayout.root.visibility  = View.GONE
                    }
                    2 -> {
                        binding.chooseDailyLayout.root.visibility = View.GONE
                        binding.chooseWeeklyLayout.root.visibility = View.GONE
                        binding.chooseMonthlyLayout.root.visibility = View.VISIBLE
                        binding.chooseYearlyLayout.root.visibility = View.GONE
                    }
                    3 -> {
                        binding.chooseDailyLayout.root.visibility = View.GONE
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
        binding.frequencyTextLayout.editText?.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                v?.let {
                    val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(it.windowToken, 0)
                }
            }
        }
    }

    private fun setupMonthly() {
        (binding.chooseMonthlyLayout.monthlyOptionsText.editText as? AutoCompleteTextView)?.apply {
            setAdapter(getDropDownMenuAdapter(options))
            setOnItemClickListener { parent, view, position, id ->
                with (binding.chooseMonthlyLayout) {
                    if (position == 0) {
                        monthlyByCountGroup.visibility = View.VISIBLE
                        monthlyByNumberCalendar.visibility = View.GONE
                    } else {
                        monthlyByCountGroup.visibility = View.GONE
                        monthlyByNumberCalendar.visibility = View.VISIBLE
                    }
                }
            }
        }
    }


    private fun setupMonthlyByNumber() {
        binding.chooseMonthlyLayout.monthlyByNumberCalendar.apply {
            setHasFixedSize(true)
            adapter = monthlyByNumberCalendarAdapter
            monthlyByNumberCalendarAdapter.days = daysByNumber
            layoutManager = NoScrollGridLayoutManager(requireContext(),7)
        }
    }

    private fun setupMonthlyByCount() {
        binding.chooseMonthlyLayout.monthlyByCountRecyclerView.adapter = monthlyByCountRecyclerViewAdapter
        val days = chooseCustomRepeatViewModel.chooseMonthlyViewModel.daysByCount
        monthlyByCountRecyclerViewAdapter.daysInMonth = days

        binding.chooseMonthlyLayout.monthlyByCountAddButton.setOnClickListener {
            days.add(Day())
            monthlyByCountRecyclerViewAdapter.notifyItemInserted(days.lastIndex)
        }
    }

    private fun setupYearlyOptionsText() {
        (binding.chooseYearlyLayout.yearlyOptionsText.editText as? AutoCompleteTextView)?.apply {
            setAdapter(getDropDownMenuAdapter(options))
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

        val monthsAdapter = getDropDownMenuAdapter(months)
        val daysOfMonthAdapter = getDropDownMenuAdapter(daysOfMonth)

        (binding.chooseYearlyLayout.yearlyByNumberMonthText.editText as? AutoCompleteTextView)?.apply {
            setAdapter(monthsAdapter)
            setOnItemClickListener { parent, view, position, id ->
                // adjust the maximum number of days based on the selected month
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

                val month = Month.valueOf(text.toString().toUpperCase())
                val day = chooseCustomRepeatViewModel.chooseYearlyViewModel.byNumberMonthDay.dayOfMonth
                chooseCustomRepeatViewModel.chooseYearlyViewModel.byNumberMonthDay = MonthDay.of(month,day)
            }
        }
        (binding.chooseYearlyLayout.yearlyByNumberDayText.editText as? AutoCompleteTextView)?.apply {
            setAdapter(daysOfMonthAdapter)
            setOnItemClickListener { parent, view, position, id ->
                val month = chooseCustomRepeatViewModel.chooseYearlyViewModel.byNumberMonthDay.month
                val day = text.toString().toInt()
                chooseCustomRepeatViewModel.chooseYearlyViewModel.byNumberMonthDay = MonthDay.of(month, day)
            }
        }
    }

    private fun setupYearlyByCount() {


        (binding.chooseYearlyLayout.yearlyByCountDayOfWeekInMonthText.editText as? AutoCompleteTextView)?.apply {
            val counts = listOf("First","Second","Third","Fourth","Last")
            setAdapter(getDropDownMenuAdapter(counts))
            setOnItemClickListener { parent, view, position, id ->
                chooseCustomRepeatViewModel.chooseYearlyViewModel.byCountDayOfWeekInMonth = position + 1
            }
        }

        (binding.chooseYearlyLayout.yearlyByCountDayOfWeekText.editText as? AutoCompleteTextView)?.apply {
            val daysOfTheWeek = listOf("Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday")
            val daysOfTheWeekValues = listOf(Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY)

            setAdapter(getDropDownMenuAdapter(daysOfTheWeek))
            setOnItemClickListener { parent, view, position, id ->
                setText(daysOfTheWeek[position].substring(0..2),false)
                chooseCustomRepeatViewModel.chooseYearlyViewModel.byCountDayOfWeek = daysOfTheWeekValues[position]
            }
        }
        (binding.chooseYearlyLayout.yearlyByCountMonthText.editText as? AutoCompleteTextView)?.apply {
            val months = listOf("January","February","March","April","May","June","July","August","September","October","November","December")
            val monthsValue = listOf(Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH, Calendar.APRIL, Calendar.MAY, Calendar.JUNE, Calendar.JULY, Calendar.AUGUST, Calendar.SEPTEMBER, Calendar.OCTOBER, Calendar.NOVEMBER, Calendar.DECEMBER)
            setAdapter(getDropDownMenuAdapter(months))
            setOnItemClickListener { parent, view, position, id ->
                setText(months[position].substring(0..2),false)
                chooseCustomRepeatViewModel.chooseYearlyViewModel.byCountMonth = monthsValue[position]
            }
        }
    }

    private fun setupWeekly() {
        with(binding.chooseWeeklyLayout) {
            val clickListener = { chip: Chip, dayOfWeek: Int ->
                if (chip.isChecked) {
                    chooseCustomRepeatViewModel.chooseWeeklyViewModel.daysOfWeek.add(dayOfWeek)
                } else {
                    chooseCustomRepeatViewModel.chooseWeeklyViewModel.daysOfWeek.remove(dayOfWeek)
                }
            }
            chipSunday.setOnClickListener {
                clickListener(it as Chip, Calendar.SUNDAY)
            }
            chipMonday.setOnClickListener {
                clickListener(it as Chip, Calendar.MONDAY)
            }
            chipTuesday.setOnClickListener {
                clickListener(it as Chip, Calendar.TUESDAY)
            }
            chipWednesday.setOnClickListener {
                clickListener(it as Chip, Calendar.WEDNESDAY)
            }
            chipThursday.setOnClickListener {
                clickListener(it as Chip, Calendar.THURSDAY)
            }
            chipFriday.setOnClickListener {
                clickListener(it as Chip, Calendar.FRIDAY)
            }
            chipSaturday.setOnClickListener {
                clickListener(it as Chip, Calendar.SATURDAY)
            }
        }
    }


    override fun onDayItemClicked(day: DayItem) {
        val index = day.number - 1
        if (day.isChecked) {
            chooseCustomRepeatViewModel.chooseMonthlyViewModel.daysByNumber.remove(day.number)
        } else {
            chooseCustomRepeatViewModel.chooseMonthlyViewModel.daysByNumber.add(day.number)
        }
        daysByNumber[index].isChecked = !day.isChecked

        monthlyByNumberCalendarAdapter.notifyItemChanged(index)
    }

    override fun onDayDeleted(day: Day) {
        chooseCustomRepeatViewModel.chooseMonthlyViewModel.daysByCount.remove(day)
        monthlyByCountRecyclerViewAdapter.notifyDataSetChanged()
    }

    private fun getMonths(): List<Month> {
        val thisMonth = LocalDate.now().month
        val months = Month.values()
        val rhs = months.sliceArray(thisMonth.ordinal..months.lastIndex)
        val lhs = months.sliceArray(0..thisMonth.ordinal)
        return (rhs + lhs).toList()
    }

}