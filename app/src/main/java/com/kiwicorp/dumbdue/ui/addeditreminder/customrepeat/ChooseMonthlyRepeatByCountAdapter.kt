package com.kiwicorp.dumbdue.ui.addeditreminder.customrepeat

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.recyclerview.widget.RecyclerView
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.data.repeat.RepeatMonthlyByCountInterval.Day
import com.kiwicorp.dumbdue.databinding.ItemChooseMonthlyRepeatByCountBinding
import com.kiwicorp.dumbdue.util.getFullName
import com.kiwicorp.dumbdue.util.sortedSundayFirst
import org.threeten.bp.DayOfWeek

class ChooseMonthlyRepeatByCountAdapter(private val onDayDeletedListener: OnDayDeletedListener) : RecyclerView.Adapter<ChooseMonthlyRepeatViewHolder>() {

    var daysInMonth = listOf<Day>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseMonthlyRepeatViewHolder {
        return ChooseMonthlyRepeatViewHolder.from(
            parent
        )
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

        val daysOfWeek = DayOfWeek.values().toList().sortedSundayFirst()
        val counts = listOf("First", "Second", "Third", "Fourth", "Last")

        val dayOfWeekAdapter = ArrayAdapter(binding.root.context, R.layout.item_drop_down_menu, List(7) { daysOfWeek[it].getFullName() })
        val countAdapter = ArrayAdapter(binding.root.context, R.layout.item_drop_down_menu, counts)

        (binding.dayOfWeekTextLayout.editText as? AutoCompleteTextView)?.apply {
            setAdapter(dayOfWeekAdapter)
            setOnItemClickListener { parent, view, position, id ->
                day.dayOfWeek = daysOfWeek[position]
            }
            setText(day.dayOfWeek.getFullName(),false)
        }

        (binding.countTextLayout.editText as? AutoCompleteTextView)?.apply {
            setAdapter(countAdapter)
            setOnItemClickListener { parent, view, position, id ->
                day.dayOfWeekInMonth = position + 1
            }
            setText(counts[day.dayOfWeekInMonth.minus(1)],false)
        }
    }

    companion object {
        fun from(parent: ViewGroup): ChooseMonthlyRepeatViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemChooseMonthlyRepeatByCountBinding.inflate(layoutInflater, parent, false)
            return ChooseMonthlyRepeatViewHolder(
                binding
            )
        }
    }
}

interface OnDayDeletedListener {
    fun onDayDeleted(day: Day)
}