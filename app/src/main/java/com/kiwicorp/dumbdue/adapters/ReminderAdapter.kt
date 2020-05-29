package com.kiwicorp.dumbdue.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.databinding.ItemHeaderBinding
import com.kiwicorp.dumbdue.databinding.ItemReminderBinding
import com.kiwicorp.dumbdue.ui.reminders.RemindersViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ClassCastException
import java.util.*

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

class ReminderAdapter(private val viewModel: RemindersViewModel):
    ListAdapter<Item, RecyclerView.ViewHolder>(ReminderDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ReminderViewHolder -> {
                val reminderItem = getItem(position) as Item.ReminderItem
                holder.bind(viewModel,reminderItem.reminder)
            }
            is HeaderViewHolder -> {
                val headerItem = getItem(position) as Item.Header
                holder.bind(headerItem.title)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ReminderViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    fun addHeadersAndSubmitList(list: List<Reminder>?) {
        adapterScope.launch {
            val items = when(list) {
                null -> listOf()
                else -> addHeaders(list)
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    private fun addHeaders(list: List<Reminder>): List<Item> {
        if (list.isEmpty()) return listOf()
        //calendar with date at 23:59:59 today
        val endOfTodayCalendar = Calendar.getInstance().apply {
            set(Calendar.MILLISECOND,59)
            set(Calendar.SECOND,59)
            set(Calendar.MINUTE,59)
            set(Calendar.HOUR_OF_DAY,23)
        }
        //calendar with date at 23:59:59 tomorrow
        val endOfTomorrowCalendar = Calendar.getInstance().apply {
            set(Calendar.MILLISECOND,59)
            set(Calendar.SECOND,59)
            set(Calendar.MINUTE,59)
            set(Calendar.HOUR_OF_DAY,23)
            add(Calendar.DAY_OF_YEAR,1)
        }
        //calendar with date at 23:59:59 in 7 days
        val endOfNext7daysCalendar = Calendar.getInstance().apply {
            set(Calendar.MILLISECOND,59)
            set(Calendar.SECOND,59)
            set(Calendar.MINUTE,59)
            set(Calendar.HOUR_OF_DAY,23)
            add(Calendar.WEEK_OF_YEAR,1)
        }

        val calendarNow = Calendar.getInstance()

        val calendarMax = Calendar.getInstance().apply {
            timeInMillis = Long.MAX_VALUE
        }

        val calendarAndTitle: List<Pair<Calendar,String>> = listOf(
            Pair(calendarNow,""),
            Pair(endOfTodayCalendar,"today"),
            Pair(endOfTomorrowCalendar,"tomorrow"),
            Pair(endOfNext7daysCalendar,"next 7 days"),
            Pair(calendarMax,"future"))

        val iter = calendarAndTitle.listIterator()

        val result = mutableListOf<Item>()
        var currPair = iter.next()
        //check if overdue header needs to be added
        if (list[0].calendar < calendarNow) {
            result.add(Item.Header("overdue"))
        }
        //for rest, add header and switch calendar if current reminder.calendar > currCalendar
        for (reminder in list) {
           if (reminder.calendar > currPair.first) {
               //check if this calendar is greater than any of the next calendars
                while(iter.hasNext()) {
                    currPair = iter.next()
                    if (reminder.calendar <= currPair.first) {
                        currPair = iter.previous()
                        break
                    }
                }
               result.add(Item.Header(currPair.second))
               result.add(Item.ReminderItem(reminder))
               currPair = iter.next()
           } else {
               result.add(Item.ReminderItem(reminder))
           }
        }
        return result
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Item.Header -> ITEM_VIEW_TYPE_HEADER
            is Item.ReminderItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    class ReminderViewHolder private constructor(val binding: ItemReminderBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: RemindersViewModel, item: Reminder) {
            binding.viewmodel = viewModel
            binding.reminder = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ReminderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemReminderBinding.inflate(layoutInflater, parent, false)

                return ReminderViewHolder(binding)
            }
        }
    }

    class HeaderViewHolder private constructor(val binding: ItemHeaderBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(title: String) {
            binding.title = title
        }

        companion object {
            fun from(parent: ViewGroup): HeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemHeaderBinding.inflate(layoutInflater, parent,false)

                return HeaderViewHolder(binding)
            }
        }
    }
}

/**
 * Callback for finding the difference between two items in a list.
 *
 * Used by ListAdapter to calculate the minimum number of changes between an old list and a new list
 * that's been passed to 'submitList'
 */
class ReminderDiffCallback: DiffUtil.ItemCallback<Item>() {
    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }
}

sealed class Item {
    abstract val id: String

    data class ReminderItem(val reminder: Reminder): Item() {
        override val id: String = reminder.id
    }

    data class Header(val title: String): Item() {
        override val id: String = ""
    }
}