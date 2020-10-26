package com.kiwicorp.dumbdue.ui.choosecustomrepeat

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentChooseWeeklyStartDateBinding
import com.kiwicorp.dumbdue.util.RoundedBottomSheetDialogFragment
import com.kiwicorp.dumbdue.util.getNavGraphViewModel
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.calendar_day.view.*
import kotlinx.android.synthetic.main.calendar_header.view.*
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.temporal.TemporalAdjusters
import org.threeten.bp.temporal.WeekFields
import java.util.*

@AndroidEntryPoint
class ChooseWeeklyStartDateFragment : RoundedBottomSheetDialogFragment() {

    private lateinit var binding: FragmentChooseWeeklyStartDateBinding

    private val args: ChooseWeeklyStartDateFragmentArgs by navArgs()

    private lateinit var chooseCustomRepeatViewModel: ChooseCustomRepeatViewModel

    private lateinit var chooseWeeklyRepeatViewModel: ChooseWeeklyViewModel

    private val today = LocalDate.now()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        chooseCustomRepeatViewModel = getNavGraphViewModel(args.graphId)
        chooseWeeklyRepeatViewModel = chooseCustomRepeatViewModel.chooseWeeklyViewModel
        val root = inflater.inflate(R.layout.fragment_choose_weekly_start_date,container,false)
        binding = FragmentChooseWeeklyStartDateBinding.bind(root)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentMonth = YearMonth.now()
        val lastMonth = currentMonth.plusMonths(12)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        binding.calendar.setup(currentMonth, lastMonth, firstDayOfWeek)
        binding.calendar.scrollToMonth(currentMonth)

        class DayViewContainer(view: View): ViewContainer(view) {
            lateinit var day: CalendarDay
            val textView = view.calendar_day_text
            val bg = view.calendar_day_bg

            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH && (day.date == today || day.date.isAfter(today))) {
                        with(day.date) {
                            chooseWeeklyRepeatViewModel.setStartingWeek(this.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)))
                        }
                        binding.calendar.notifyCalendarChanged()
                    }
                }
            }
        }

        binding.calendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.textView
                val bg = container.bg

                textView.text = null
                textView.background = null
                bg.visibility = View.INVISIBLE

                val startDate = chooseWeeklyRepeatViewModel.firstDateOfStartingWeek.value!!
                val endDate = startDate.with(TemporalAdjusters.next(DayOfWeek.SATURDAY))

                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.text = day.day.toString()
                    if (day.date.isBefore(today) && day.date !in startDate..endDate) {
                        textView.setTextColor(Color.parseColor("#4DFFFFFF"))
                    } else {
                        when {
                            day.date == startDate -> {
                                textView.setBackgroundResource(R.drawable.calendar_continuous_selected_start)
                            }
                            day.date > startDate && day.date < endDate -> {
                                textView.setBackgroundResource(R.drawable.calendar_continous_selected_middle)
                            }
                            day.date == endDate -> {
                                textView.setBackgroundResource(R.drawable.calendar_continuous_selected_end)
                            }
                            day.date == today -> {
                                textView.setTextColor(Color.GRAY)
                                bg.visibility = View.VISIBLE
                                bg.setBackgroundResource(R.drawable.calendar_today_bg)
                            }
                            else -> {
                                textView.setTextColor(Color.WHITE)
                            }
                        }
                    }
                } else {
                    // This part is to make the coloured selection background continuous
                    // on the blank in and out dates across various months and also on dates(months)
                    // between the start and end dates if the selection spans across multiple months.

                    // Mimic selection of inDates that are less than the startDate.
                    // Example: When 26 Feb 2019 is startDate and 5 Mar 2019 is endDate,
                    // this makes the inDates in Mar 2019 for 24 & 25 Feb 2019 look selected.
                    if ((day.owner == DayOwner.PREVIOUS_MONTH &&
                                startDate.monthValue == day.date.monthValue &&
                                endDate.monthValue != day.date.monthValue) ||
                        // Mimic selection of outDates that are greater than the endDate.
                        // Example: When 25 Apr 2019 is startDate and 2 May 2019 is endDate,
                        // this makes the outDates in Apr 2019 for 3 & 4 May 2019 look selected.
                        (day.owner == DayOwner.NEXT_MONTH &&
                                startDate.monthValue != day.date.monthValue &&
                                endDate.monthValue == day.date.monthValue) ||

                        // Mimic selection of in and out dates of intermediate
                        // months if the selection spans across multiple months.
                        (startDate < day.date && endDate > day.date &&
                                startDate.monthValue != day.date.monthValue &&
                                endDate.monthValue != day.date.monthValue)
                    ) {
                        textView.setBackgroundResource(R.drawable.calendar_continous_selected_middle)
                    }
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val textView = view.calendar_header_text
        }

        binding.calendar.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                container.textView.text = "${month.yearMonth.month.name.toLowerCase().capitalize()} ${month.year}"
            }
        }
    }

}