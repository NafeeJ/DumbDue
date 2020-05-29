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

private val ITEM_VIEW_TYPE_HEADER = 0
private val ITEM_VIEW_TYPE_ITEM = 1

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

        val calendarToTitle: Map<Calendar,String> = mapOf(
            calendarNow to "today",
            endOfTodayCalendar to "tomorrow",
            endOfTomorrowCalendar to "next 7 days",
            endOfNext7daysCalendar to "future",
            calendarMax to "far far future")

        val iter = calendarToTitle.iterator()

        val result = mutableListOf<Item>()
        var currpair = iter.next()
        //check if overdue header needs to be added
        if (list[0].calendar < calendarNow) {
            result.add(Item.Header("overdue"))
            result.add(Item.ReminderItem(list[0]))
        }
        //for rest, add header and switch calendar if current reminder.calendar > currCalendar
        for (i in 1 until list.size) {
           if (list[i].calendar > currpair.key) {
               result.add(Item.Header(currpair.value))
               result.add(Item.ReminderItem(list[i]))
               currpair = iter.next()
           } else {
               result.add(Item.ReminderItem(list[i]))
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
//            binding.executePendingBindings()
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