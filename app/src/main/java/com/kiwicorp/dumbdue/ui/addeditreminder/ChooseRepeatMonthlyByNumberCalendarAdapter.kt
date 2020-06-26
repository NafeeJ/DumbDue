package com.kiwicorp.dumbdue.ui.addeditreminder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.ItemCalendarDayBinding

class ChooseRepeatMonthlyByNumberCalendarAdapter(private val onDayItemClickListener: DayItemClickListener) : RecyclerView.Adapter<DayViewHolder>() {

    var days = listOf<DayItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        return DayViewHolder.from(parent)
    }

    override fun getItemCount() = days.size

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(days[position], onDayItemClickListener)
    }

}

class DayViewHolder private constructor(val binding: ItemCalendarDayBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(dayItem: DayItem, dayItemClickListener: DayItemClickListener) {
        binding.textView.apply {
            if (dayItem.number == 32) {
                text = context.getString(R.string.last_day)
                textSize = 12f
            } else {
                text = dayItem.number.toString()
                textSize = 20f
            }
        }
        binding.circle.visibility = if (dayItem.isChecked) View.VISIBLE else View.INVISIBLE
        binding.dayItem = dayItem
        binding.dayItemClickListener = dayItemClickListener
    }

    companion object {
        fun from(parent: ViewGroup): DayViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemCalendarDayBinding.inflate(layoutInflater,parent,false)
            return DayViewHolder(binding)
        }
    }
}

data class DayItem( val number: Int, var isChecked: Boolean = false)

interface DayItemClickListener {
    fun onDayItemClicked(dayItem: DayItem)
}