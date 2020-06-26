package com.kiwicorp.dumbdue.ui.addeditreminder

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.recyclerview.widget.RecyclerView
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.data.repeat.RepeatMonthlyByCount.Day
import com.kiwicorp.dumbdue.databinding.ItemChooseMonthlyRepeatByCountBinding
import java.util.*

class ChooseMonthlyRepeatByCountAdapter(private val onDayDeletedListener: OnDayDeletedListener) : RecyclerView.Adapter<ChooseMonthlyRepeatViewHolder>() {

    var daysInMonth = listOf<Day>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseMonthlyRepeatViewHolder {
        return ChooseMonthlyRepeatViewHolder.from(parent)
    }

    override fun getItemCount(): Int = daysInMonth.size

    override fun onBindViewHolder(holder: ChooseMonthlyRepeatViewHolder, position: Int) {
        holder.bind(daysInMonth[position], onDayDeletedListener)
    }

}

class ChooseMonthlyRepeatViewHolder private constructor(val binding: ItemChooseMonthlyRepeatByCountBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(day: Day, onDayDeletedListener: OnDayDeletedListener) {
        binding.day = day
        binding.onDayDeletedListener = onDayDeletedListener

        val daysOfTheWeek = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        val counts = listOf("First", "Second", "Third", "Fourth", "Last")

        val dayOfWeekAdapter = ArrayAdapter(binding.root.context, R.layout.item_drop_down_menu, daysOfTheWeek)
        val countAdapter = ArrayAdapter(binding.root.context, R.layout.item_drop_down_menu, counts)

        (binding.dayOfWeekTextLayout.editText as? AutoCompleteTextView)?.apply {
            setAdapter(dayOfWeekAdapter)
            setOnItemClickListener { parent, view, position, id ->
                day.dayOfWeek = when (position) {
                    0 -> Calendar.SUNDAY
                    1 -> Calendar.MONDAY
                    2 -> Calendar.TUESDAY
                    3 -> Calendar.WEDNESDAY
                    4 -> Calendar.THURSDAY
                    5 -> Calendar.FRIDAY
                    6 -> Calendar.SATURDAY
                    else -> 0
                }
            }
            // in case recycler view reuses view holder
            if (day.dayOfWeek != null) {
                setText(daysOfTheWeek[day.dayOfWeek!!.minus(1)],false)
            } else {
                setText("")
            }
        }

        (binding.countTextLayout.editText as? AutoCompleteTextView)?.apply {
            setAdapter(countAdapter)
            setOnItemClickListener { parent, view, position, id ->
                day.dayOfWeekInMonth = position + 1
            }
            // in case recycler view reuses view holder
            if (day.dayOfWeekInMonth != null) {
                setText(counts[day.dayOfWeekInMonth!!.minus(1)],false)
            } else {
                setText("")
            }
        }
    }

    companion object {
        fun from(parent: ViewGroup): ChooseMonthlyRepeatViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemChooseMonthlyRepeatByCountBinding.inflate(layoutInflater, parent, false)
            return ChooseMonthlyRepeatViewHolder(binding)
        }
    }
}

interface OnDayDeletedListener {
    fun onDayDeleted(day: Day)
}