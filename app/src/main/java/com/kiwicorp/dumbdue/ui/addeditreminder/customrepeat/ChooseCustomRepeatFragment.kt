package com.kiwicorp.dumbdue.ui.addeditreminder.customrepeat

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import android.widget.TimePicker
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.kiwicorp.dumbdue.EventObserver
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.data.repeat.RepeatMonthlyByCountInterval.Day
import com.kiwicorp.dumbdue.databinding.FragmentChooseCustomRepeatBinding
import com.kiwicorp.dumbdue.ui.addeditreminder.*
import com.kiwicorp.dumbdue.util.*
import com.kiwicorp.dumbdue.util.daggerext.DaggerBottomSheetDialogFragment
import org.threeten.bp.*
import javax.inject.Inject

class ChooseCustomRepeatFragment : DaggerBottomSheetDialogFragment(),
    DayItemClickListener,
    OnDayDeletedListener {

    private val args: ChooseCustomRepeatFragmentArgs by navArgs()

    lateinit var binding: FragmentChooseCustomRepeatBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: AddEditReminderViewModel

    private lateinit var chooseCustomRepeatViewModel: ChooseCustomRepeatViewModel

    private val monthlyByNumberCalendarAdapter = ChooseRepeatMonthlyByNumberCalendarAdapter(this)

    private val monthlyByCountRecyclerViewAdapter = ChooseMonthlyRepeatByCountAdapter(this)

    private val options = listOf("By count of day of week in month", "By number of day in month")

    private val today = LocalDate.now()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = getNavGraphViewModel(args.graphId) { viewModelFactory }
        chooseCustomRepeatViewModel = viewModel.chooseCustomRepeatViewModel
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
    // Expands the BottomSheetDialog so the entire dialog is shown when the keyboard is first opened
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheet: FrameLayout = dialog.findViewById(R.id.design_bottom_sheet)!!
            val bottomSheetBehavior: BottomSheetBehavior<FrameLayout> = BottomSheetBehavior.from(bottomSheet)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }
        return bottomSheetDialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupNavigation()
        viewModel.repeatInterval.value?.let {
            chooseCustomRepeatViewModel.loadRepeatInterval(it)
        }
    }

    private fun setupNavigation() {
        chooseCustomRepeatViewModel.chooseDailyViewModel.eventOpenChooseDailyStartDate.observe(viewLifecycleOwner, EventObserver {
            navigateToChooseDailyStartDate()
        })
        chooseCustomRepeatViewModel.chooseWeeklyViewModel.eventOpenChooseWeeklyStartDate.observe(viewLifecycleOwner, EventObserver {
            navigateToChooseWeeklyStartDate()
        })
        chooseCustomRepeatViewModel.eventOpenTimePicker.observe(viewLifecycleOwner, EventObserver {
            openTimePicker()
        })
    }

    private fun navigateToChooseDailyStartDate() {
        val action = ChooseCustomRepeatFragmentDirections.actionCustomRepeatFragmentToChooseDailyStartDateFragment(args.graphId)
        findNavController().navigate(action)
    }

    private fun navigateToChooseWeeklyStartDate() {
        val action = ChooseCustomRepeatFragmentDirections.actionCustomRepeatFragmentToChooseWeeklyStartDateFragment(args.graphId)
        findNavController().navigate(action)
    }

    private fun openTimePicker() {
        val onTimeSetListener = TimePickerDialog.OnTimeSetListener { timePicker: TimePicker, hourOfDay: Int, minute: Int ->
            chooseCustomRepeatViewModel.updateTime(LocalTime.of(hourOfDay, minute))
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRepeatTypeText()
        setupFrequencyText()
        setupDoneButton()
        setupWeekly()
        setupMonthly()
        setupYearly()
    }

    private fun setupRepeatTypeText() {
        val types = listOf("days","weeks","months","years")
        (binding.typeTextLayout.editText as? AutoCompleteTextView)?.apply {
            setAdapter(getDropDownMenuAdapter(types))
            doOnTextChanged { text, start, before, count ->
                chooseCustomRepeatViewModel.updateType(text.toString())
            }
            chooseCustomRepeatViewModel.type.observe(viewLifecycleOwner, Observer {
                if (it != text.toString()) {
                    setText(it, false)
                }
                binding.mainLayout.visibility = View.VISIBLE
                binding.chooseDailyLayout.root.visibility = if (it == "days") View.VISIBLE else View.GONE
                binding.chooseWeeklyLayout.root.visibility = if (it == "weeks") View.VISIBLE else View.GONE
                binding.chooseMonthlyLayout.root.visibility = if (it == "months") View.VISIBLE else View.GONE
                binding.chooseYearlyLayout.root.visibility = if (it == "years") View.VISIBLE else View.GONE
            })
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

    private fun setupDoneButton() {
        binding.doneButton.setOnClickListener {
            //todo
            viewModel.onChooseRepeatInterval(chooseCustomRepeatViewModel.getRepeatInterval())
            dialog?.cancel()
        }
    }

    private fun setupWeekly() {
        with(binding.chooseWeeklyLayout) {
            val clickListener = { chip: Chip, dayOfWeek: DayOfWeek ->
                if (chip.isChecked) {
                    chooseCustomRepeatViewModel.chooseWeeklyViewModel.addDayOfWeek(dayOfWeek)
                } else {
                    chooseCustomRepeatViewModel.chooseWeeklyViewModel.removeDayOfWeek(dayOfWeek)
                }
            }
            //todo make own implementation with recycler view instead of chips
            chipSunday.setOnClickListener {
                clickListener(it as Chip, DayOfWeek.SUNDAY)
            }
            chipMonday.setOnClickListener {
                clickListener(it as Chip, DayOfWeek.MONDAY)
            }
            chipTuesday.setOnClickListener {
                clickListener(it as Chip, DayOfWeek.TUESDAY)
            }
            chipWednesday.setOnClickListener {
                clickListener(it as Chip, DayOfWeek.WEDNESDAY)
            }
            chipThursday.setOnClickListener {
                clickListener(it as Chip, DayOfWeek.THURSDAY)
            }
            chipFriday.setOnClickListener {
                clickListener(it as Chip, DayOfWeek.FRIDAY)
            }
            chipSaturday.setOnClickListener {
                clickListener(it as Chip, DayOfWeek.SATURDAY)
            }
            chooseCustomRepeatViewModel.chooseWeeklyViewModel.daysOfWeek.observe(viewLifecycleOwner, Observer {
                chipSunday.isChecked = it.contains(DayOfWeek.SUNDAY)
                chipMonday.isChecked = it.contains(DayOfWeek.MONDAY)
                chipTuesday.isChecked = it.contains(DayOfWeek.TUESDAY)
                chipWednesday.isChecked = it.contains(DayOfWeek.WEDNESDAY)
                chipThursday.isChecked = it.contains(DayOfWeek.THURSDAY)
                chipFriday.isChecked = it.contains(DayOfWeek.FRIDAY)
                chipSaturday.isChecked = it.contains(DayOfWeek.SATURDAY)
            })
        }
    }

    private fun setupMonthly() {
        setupMonthlyOptionsText()
        setupMonthlyStartText()
        setupMonthlyByNumber()
        setupMonthlyByCount()
    }

    private fun setupMonthlyStartText() {
        val months = getMonths()
        //list of the months with year next to it ex: "June 2020"
        val monthsStr = List(12) {
            "${months[it].getFullName()} " +
                    "${today.year + if (months[it] < today.month) 1 else 0}" }
        (binding.chooseMonthlyLayout.startDateTextLayout.editText as? AutoCompleteTextView)?.apply {
            setAdapter(getDropDownMenuAdapter(monthsStr))

            setOnItemClickListener { parent, view, position, id ->
                with(text.toString()) {
                    val year = this.substringAfter(' ').toInt()
                    val month: Month = Month.valueOf(this.substringBefore(' ').toUpperCase())
                    chooseCustomRepeatViewModel.chooseMonthlyViewModel.updateStartingYearMonth(YearMonth.of(year, month))
                }
            }

            chooseCustomRepeatViewModel.chooseMonthlyViewModel.startingYearMonthStr.observe(viewLifecycleOwner, Observer {
                setText(it,false)
            })
        }
    }

    private fun setupMonthlyOptionsText() {
        (binding.chooseMonthlyLayout.monthlyOptionsText.editText as? AutoCompleteTextView)?.apply{
            setAdapter(getDropDownMenuAdapter(options))
            doOnTextChanged { text, start, before, count ->
                chooseCustomRepeatViewModel.chooseMonthlyViewModel.updateSelectedMonthlyOption(text.toString())
            }
            chooseCustomRepeatViewModel.chooseMonthlyViewModel.selectedMonthlyOption.observe(viewLifecycleOwner, Observer {
                if (it != text.toString()) {
                    setText(it, false)
                }
                with(binding.chooseMonthlyLayout) {
                    monthlyByCountGroup.visibility = if (it == options[0]) View.VISIBLE else View.GONE
                    monthlyByNumberCalendar.visibility = if (it == options[1]) View.VISIBLE else View.GONE
                }
            })
        }
    }


    private fun setupMonthlyByNumber() {
        binding.chooseMonthlyLayout.monthlyByNumberCalendar.apply {
            setHasFixedSize(true)
            adapter = monthlyByNumberCalendarAdapter
            chooseCustomRepeatViewModel.chooseMonthlyViewModel.daysByNumber.observe(viewLifecycleOwner, Observer {days ->
                monthlyByNumberCalendarAdapter.days = MutableList(32) { DayItem(it + 1) }.apply {
                    for (day in days) {
                        this[day - 1] = DayItem(day, true)
                    }
                }
                monthlyByNumberCalendarAdapter.notifyDataSetChanged()
            })
            layoutManager = NoScrollGridLayoutManager(requireContext(),7)
        }
    }

    private fun setupMonthlyByCount() {
        binding.chooseMonthlyLayout.monthlyByCountRecyclerView.adapter = monthlyByCountRecyclerViewAdapter

        chooseCustomRepeatViewModel.chooseMonthlyViewModel.daysByCount.observe(viewLifecycleOwner, Observer {
            monthlyByCountRecyclerViewAdapter.daysInMonth = it
            monthlyByCountRecyclerViewAdapter.notifyDataSetChanged()
        })

        binding.chooseMonthlyLayout.monthlyByCountAddButton.setOnClickListener {
            chooseCustomRepeatViewModel.chooseMonthlyViewModel.addDayInDayByCount(Day())
        }
    }

    private fun setupYearly() {
        setupYearlyOptionsText()
        setupYearlyStartText()
        setupYearlyByCount()
        setupYearlyByNumber()
    }

    private fun setupYearlyOptionsText() {
        (binding.chooseYearlyLayout.yearlyOptionsText.editText as? AutoCompleteTextView)?.apply{
            setAdapter(getDropDownMenuAdapter(options))
            doOnTextChanged { text, start, before, count ->
                chooseCustomRepeatViewModel.chooseYearlyViewModel.updateSelectedYearlyOption(text.toString())
            }
            chooseCustomRepeatViewModel.chooseYearlyViewModel.selectedYearlyOption.observe(viewLifecycleOwner, Observer {
                if (it != text.toString()) {
                    setText(it, false)
                }
                with(binding.chooseYearlyLayout) {
                    yearlyByCountGroup.visibility = if (it == options[0]) View.VISIBLE else View.GONE
                    yearlyByNumberGroup.visibility = if (it == options[1]) View.VISIBLE else View.GONE
                }
            })
        }
    }

    private fun setupYearlyStartText() {
        val years = List(11) {today.year + it}
        (binding.chooseYearlyLayout.startDateTextLayout.editText as? AutoCompleteTextView)?.apply {
            setAdapter(getDropDownMenuAdapter(years))
            setOnItemClickListener { parent, view, position, id ->
                chooseCustomRepeatViewModel.chooseYearlyViewModel.updateStartingYear(Year.parse(text))
            }
            chooseCustomRepeatViewModel.chooseYearlyViewModel.startingYear.observe(viewLifecycleOwner, Observer {
                setText(it.toString(),false)
            })
        }
    }

    private fun setupYearlyByNumber() {
        val months = Month.values()
        val monthsAdapter = getDropDownMenuAdapter(List(12) { months[it].getFullName() })

        var daysOfMonth = List(31) {it + 1}
        val daysOfMonthAdapter = getDropDownMenuAdapter(daysOfMonth)

        (binding.chooseYearlyLayout.yearlyByNumberMonthText.editText as? AutoCompleteTextView)?.apply {
            setAdapter(monthsAdapter)
            setOnItemClickListener { parent, view, position, id ->
                val month = Month.valueOf(text.toString().toUpperCase())
                // update the size of daysOfMonth based on the month selected
                val size = month.maxLength()
                daysOfMonthAdapter.clear()
                daysOfMonth = List(size) {it + 1}
                daysOfMonthAdapter.addAll(daysOfMonth)
                daysOfMonthAdapter.notifyDataSetChanged()
                val day: Int
                with(binding.chooseYearlyLayout.yearlyByNumberDayText.editText) {
                    // if current day of month exceeds the max, set current day of month to the max
                    if (this@with!!.text.toString().toInt() > size) {
                        (this@with as? AutoCompleteTextView)?.setText(size.toString(),false)
                    }
                    day = this@with.text.toString().toInt()
                }
                chooseCustomRepeatViewModel.chooseYearlyViewModel.updateByNumberMonthDay(MonthDay.of(month,day))
            }
        }

        (binding.chooseYearlyLayout.yearlyByNumberDayText.editText as? AutoCompleteTextView)?.apply {
            setAdapter(daysOfMonthAdapter)
            setOnItemClickListener { parent, view, position, id ->
                val month = chooseCustomRepeatViewModel.chooseYearlyViewModel.byNumberMonthDay.value!!.month
                val day = text.toString().toInt()
                chooseCustomRepeatViewModel.chooseYearlyViewModel.updateByNumberMonthDay(MonthDay.of(month, day))
            }
        }

        chooseCustomRepeatViewModel.chooseYearlyViewModel.byNumberMonthDay.observe(viewLifecycleOwner, Observer { monthDay ->
            (binding.chooseYearlyLayout.yearlyByNumberDayText.editText as? AutoCompleteTextView)?.setText(monthDay.dayOfMonth.toString(), false)
            (binding.chooseYearlyLayout.yearlyByNumberMonthText.editText as? AutoCompleteTextView)?.setText(monthDay.month.getFullName(), false)
        })
    }

    private fun setupYearlyByCount() {
        (binding.chooseYearlyLayout.yearlyByCountDayOfWeekInMonthTextLayout.editText as? AutoCompleteTextView)?.apply {
            val counts = listOf("First","Second","Third","Fourth","Last")
            setAdapter(getDropDownMenuAdapter(counts))

            setOnItemClickListener { parent, view, position, id ->
                chooseCustomRepeatViewModel.chooseYearlyViewModel.updateByCountDayOfWeekInMonth(position + 1)
            }

            chooseCustomRepeatViewModel.chooseYearlyViewModel.byCountDayOfWeekInMonth.observe(viewLifecycleOwner, Observer {
                setText(counts[it - 1], false)
            })
        }

        (binding.chooseYearlyLayout.yearlyByCountDayOfWeekText.editText as? AutoCompleteTextView)?.apply {
            val daysOfTheWeek = DayOfWeek.values().toList().sortedSundayFirst()
            setAdapter(getDropDownMenuAdapter(List(7) { daysOfTheWeek[it].getFullName() }))

            setOnItemClickListener { parent, view, position, id ->
                chooseCustomRepeatViewModel.chooseYearlyViewModel.updateByCountDayOfWeek(daysOfTheWeek[position])
            }

            chooseCustomRepeatViewModel.chooseYearlyViewModel.byCountDayOfWeek.observe(viewLifecycleOwner, Observer {
                setText(it.getFullName().substring(0..2), false)
            })
        }

        (binding.chooseYearlyLayout.yearlyByCountMonthText.editText as? AutoCompleteTextView)?.apply {
            val months = Month.values()
            setAdapter(getDropDownMenuAdapter(List(12) { months[it].getFullName() }))

            setOnItemClickListener { parent, view, position, id ->
                chooseCustomRepeatViewModel.chooseYearlyViewModel.updateByCountMonth(months[position])
            }

            chooseCustomRepeatViewModel.chooseYearlyViewModel.byCountMonth.observe(viewLifecycleOwner, Observer {
                setText(it.getFullName().substring(0..2),false)
            })
        }
    }


    override fun onDayItemClicked(dayItem: DayItem) {
        if (dayItem.isChecked) {
            chooseCustomRepeatViewModel.chooseMonthlyViewModel.removeDayInDaysByNumber(dayItem.number)
        } else {
            chooseCustomRepeatViewModel.chooseMonthlyViewModel.addDayInDaysByNumber(dayItem.number)
        }
    }

    override fun onDayDeleted(day: Day) {
        chooseCustomRepeatViewModel.chooseMonthlyViewModel.removeDayInDayByCount(day)
    }

    // returns the a list of all the months with the current month first and the rest in order
    private fun getMonths(): List<Month> {
        val thisMonth = LocalDate.now().month
        val months = Month.values()
        val rhs = months.sliceArray(thisMonth.ordinal..months.lastIndex)
        val lhs = months.sliceArray(0..thisMonth.ordinal)
        return (rhs + lhs).toList()
    }

}