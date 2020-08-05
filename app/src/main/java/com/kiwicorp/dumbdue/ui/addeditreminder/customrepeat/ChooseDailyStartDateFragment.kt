package com.kiwicorp.dumbdue.ui.addeditreminder.customrepeat

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kiwicorp.dumbdue.EventObserver
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentChooseDailyStartDateBinding
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
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.temporal.WeekFields
import java.util.*

@AndroidEntryPoint
class ChooseDailyStartDateFragment : RoundedBottomSheetDialogFragment() {

    private lateinit var binding: FragmentChooseDailyStartDateBinding

    private val args: ChooseDailyStartDateFragmentArgs by navArgs()

    private lateinit var chooseCustomRepeatViewModel: ChooseCustomRepeatViewModel

    private lateinit var chooseDailyRepeatViewModel: ChooseDailyViewModel

    private val today = LocalDate.now()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        chooseCustomRepeatViewModel = getNavGraphViewModel(args.graphId)
        chooseDailyRepeatViewModel = chooseCustomRepeatViewModel.chooseDailyViewModel
        val root = inflater.inflate(R.layout.fragment_choose_daily_start_date, container,false)
        binding = FragmentChooseDailyStartDateBinding.bind(root)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentMonth = YearMonth.now()
        val lastMonth = currentMonth.plusMonths(10)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        binding.calendar.setup(currentMonth, lastMonth, firstDayOfWeek)
        binding.calendar.scrollToMonth(currentMonth)

        class DayViewContainer(view: View): ViewContainer(view) {
            lateinit var day: CalendarDay
            val textView = view.calendar_day_text
            val bg = view.calendar_day_bg
            init {
                textView.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH && (day.date.isAfter(today) || day.date == today)) {
                        val oldDate = chooseDailyRepeatViewModel.startingDate.value!!
                        chooseDailyRepeatViewModel.setStartingDate(day.date)
                        binding.calendar.notifyDateChanged(day.date)
                        oldDate?.let { binding.calendar.notifyDateChanged(oldDate) }
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
                textView.text = day.date.dayOfMonth.toString()
                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.visibility = View.VISIBLE
                    if (day.date.isBefore(today)) {
                        textView.setTextColor(Color.parseColor("#4DFFFFFF"))
                    } else {
                        when(day.date) {
                            chooseDailyRepeatViewModel.startingDate.value!! -> bg.setBackgroundResource(R.drawable.calendar_selected)
                            today -> bg.setBackgroundResource(R.drawable.calendar_today_bg)
                            else -> bg.background = null
                        }
                    }
                } else {
                    textView.visibility = View.INVISIBLE
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