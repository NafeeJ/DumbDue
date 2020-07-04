package com.kiwicorp.dumbdue.ui.reminders

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.databinding.ItemHeaderBinding
import com.kiwicorp.dumbdue.databinding.ItemReminderBinding
import com.kiwicorp.dumbdue.util.timeFromNowString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

class ReminderAdapter(private val viewModel: RemindersViewModel):
    ListAdapter<Item, RecyclerView.ViewHolder>(
        ReminderDiffCallback()
    ) {

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
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(
                parent
            )
            ITEM_VIEW_TYPE_ITEM -> ReminderViewHolder.from(
                parent
            )
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

        val now = ZonedDateTime.now()
        // 23:59:59 today
        val endOfToday = now
            .withHour(23)
            .withMinute(59)
            .withSecond(59)
            .withNano(59)
        // 23:59:59 tomorrow
        val endOfTomorrow = endOfToday.plusDays(1)
        // 23:59:59 in 7 days
        val endOfNext7days = endOfToday.plusDays(7)

        val dateMax = ZonedDateTime.ofInstant(Instant.ofEpochMilli(Long.MAX_VALUE), ZoneId.of("UTC"))

        val dateToTitle: List<Pair<ZonedDateTime,String>> = listOf(
            Pair(now,""),
            Pair(endOfToday,"today"),
            Pair(endOfTomorrow,"tomorrow"),
            Pair(endOfNext7days,"next 7 days"),
            Pair(dateMax,"future"))

        val iter = dateToTitle.listIterator()

        val result = mutableListOf<Item>()
        var currPair = iter.next()
        //check if overdue header needs to be added
        if (list[0].dueDate < ZonedDateTime.now()) {
            result.add(Item.Header("overdue"))
        }
        //for rest, add header and switch calendar if current reminder.calendar > currCalendar
        for (reminder in list) {
           if (reminder.dueDate > currPair.first) {
               //check if this calendar is greater than any of the next calendars
                while(iter.hasNext()) {
                    currPair = iter.next()
                    if (reminder.dueDate <= currPair.first) {
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

            val dueDate = item.dueDate

            binding.timeText.text = findTimeString(dueDate)

            binding.dateRepeatText.text = item.repeatInterval?.toString()
                ?: dueDate.format(DateTimeFormatter.ofPattern("MMM d, h:mm a"))

            binding.dateRepeatText.setTextColor(
                if (item.dueDate.isBefore(ZonedDateTime.now())) {
                    Color.parseColor("#f54242") // red
                } else {
                    Color.parseColor("#525252") // grey
                }
            )

            binding.colorBar.setBackgroundColor(getColorOfColorBar(dueDate))

            binding.executePendingBindings()
        }

        private fun findTimeString(time: ZonedDateTime): String {
            // 23:59:59 tomorrow
            val endOfTomorrow = ZonedDateTime.now()
                .withHour(23)
                .withMinute(59)
                .withSecond(59)
                .withNano(59)
                .plusDays(1)
            // 23:59:59 in 7 days
            val endOfNext7Days = ZonedDateTime.now()
                .withHour(23)
                .withMinute(59)
                .withSecond(59)
                .withNano(59)
                .plusWeeks(1)

            return when {
                // add "ago if time is before now
                time.isBefore(ZonedDateTime.now()) -> {
                    "${time.timeFromNowString(true)} ago"
                }
                // add "in" to time from now string if within 3 hours or more than a week
                (ChronoUnit.HOURS.between(time, ZonedDateTime.now()) <= 3) || time.isAfter(endOfNext7Days) -> {
                    "in ${time.timeFromNowString(true)}"
                }
                // if less than 2 days from today, return the time
                time.isBefore(endOfTomorrow) -> time.format(DateTimeFormatter.ofPattern("h:mm a"))
                // if less than 7 days from today, return the day of the week
                time.isBefore(endOfNext7Days) -> time.format(DateTimeFormatter.ofPattern("EEE"))
                else -> ""
            }
        }

        private fun getColorOfColorBar(time: ZonedDateTime): Int {
            val now = ZonedDateTime.now()
            // 23:59:59 today
            val endOfToday = now
                .withHour(23)
                .withMinute(59)
                .withSecond(59)
                .withNano(59)
            // 23:59:59 tomorrow
            val endOfTomorrow = endOfToday.plusDays(1)
            // 23:59:59 in 7 days
            val endOfNext7days = endOfToday.plusDays(7)

            return when {
                time.isBefore(now) -> Color.parseColor("#f54242") //red
                time.isBefore(endOfToday)-> Color.parseColor("#fff262") //yellow
                time.isBefore(endOfTomorrow) -> Color.parseColor("#3371FF")//blue
                time.isBefore(endOfNext7days) -> Color.parseColor("#6a44b1") //purple
                else -> Color.parseColor("#525252") //grey
            }
        }

        companion object {
            fun from(parent: ViewGroup): ReminderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemReminderBinding.inflate(layoutInflater, parent, false)

                return ReminderViewHolder(
                    binding
                )
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

                return HeaderViewHolder(
                    binding
                )
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