package com.kiwicorp.dumbdue.ui.reminders

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.databinding.ItemHeaderBinding
import com.kiwicorp.dumbdue.databinding.ItemReminderBinding
import com.kiwicorp.dumbdue.util.getColorFromAttr
import com.kiwicorp.dumbdue.util.isLightTheme
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
        //todo refactor to make more readable?
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

        fun bind(viewModel: RemindersViewModel, reminder: Reminder) {
            binding.viewmodel = viewModel
            binding.reminder = reminder

            val context = binding.root.context

            val dueDate = reminder.dueDate

            binding.timeText.text = findTimeString(dueDate)

            binding.dateRepeatText.text = reminder.repeatInterval?.toString()
                ?: dueDate.format(DateTimeFormatter.ofPattern("MMM d, h:mm a"))

            binding.dateRepeatText.setTextColor(
                if (dueDate.isBefore(ZonedDateTime.now())) {
                    context.getColorFromAttr(R.attr.colorError)
                } else {
                    context.getColor(R.color.grey)
                }
            )

            binding.timeText.setTextColor(
                if (dueDate.isBefore(ZonedDateTime.now())) {
                    context.getColorFromAttr(R.attr.colorError)
                } else {
                    context.getColorFromAttr(R.attr.colorOnBackground)
                }
            )

            // in case recycler view resuses view holder
            binding.checkbox.isChecked = false

            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.complete(reminder)
                }
            }
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

        private fun getColorOfReminder(dueDate: ZonedDateTime): Int {
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
                dueDate.isBefore(now) -> Color.parseColor("#f54242") //red
                dueDate.isBefore(endOfToday)-> Color.parseColor("#fff262") //yellow
                dueDate.isBefore(endOfTomorrow) -> Color.parseColor("#3371FF")//blue
                dueDate.isBefore(endOfNext7days) -> Color.parseColor("#6a44b1") //purple
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
            val context = binding.root.context
            // hot to cold
            val hotToColdPalette = if (context.theme.isLightTheme()) {
                getPalette(
                    Color.valueOf(Color.parseColor("#FF4C5D")),
                    Color.valueOf(Color.parseColor("#0049ff")),
                    3
                )
            } else {
                getPalette(
                    Color.valueOf(Color.parseColor("#FF4C5D")),
                    Color.valueOf(Color.parseColor("#305074")),
                    3
                )
            }

            binding.divider.setBackgroundColor(when (title) {
                "overdue" -> hotToColdPalette[0].toArgb()
                "today" -> hotToColdPalette[1].toArgb()
                "tomorrow" -> hotToColdPalette[2].toArgb()
                "next 7 days" -> hotToColdPalette[3].toArgb()
                else -> hotToColdPalette[4].toArgb()
            })
//            // secondary to primary
//            val palette = getPalette(
//                Color.valueOf(context.getColorFromAttr(R.attr.colorSecondary)),
//                Color.valueOf(context.getColorFromAttr(R.attr.colorPrimary)),
//                2
//            )
//            binding.divider.setBackgroundColor(when (title) {
//                "overdue" -> Color.parseColor("#FF4C5D")
//                "today" -> palette[0].toArgb()
//                "tomorrow" -> palette[1].toArgb()
//                "next 7 days" -> palette[2].toArgb()
//                else -> palette[3].toArgb()
//            })
//            binding.divider.setBackgroundColor(if (title == "overdue") {
//                context.getColorFromAttr(R.attr.colorError)
//            } else {
//                context.getColorFromAttr(R.attr.colorSecondary)
//            })
        }

        private fun getPalette(color1: Color, color2: Color, midpoints: Int): List<Color> {
            val redStep = (color2.red() - color1.red()) / (midpoints.toFloat() + 1f)
            val greenStep = (color2.green() - color1.green()) / (midpoints.toFloat() + 1f)
            val blueStep = (color2.blue() - color1.blue()) / (midpoints.toFloat() + 1f)

            val palette = mutableListOf(color1)
            var currColor = color1
            repeat(midpoints) {
                currColor = Color.valueOf(
                    currColor.red() + redStep,
                    currColor.green() + greenStep,
                    currColor.blue() + blueStep
                )
                palette.add(currColor)
            }
            palette.add(color2)
            return palette
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