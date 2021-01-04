package com.kiwicorp.dumbdue.ui.archive

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.databinding.ItemArchivedReminderBinding
import com.kiwicorp.dumbdue.util.getColorFromAttr
import com.kiwicorp.dumbdue.util.timeFromNowString
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit

class ArchiveListAdapter(private val viewModel: ArchiveViewModel) : ListAdapter<Reminder, ArchiveListAdapter.ReminderViewHolder>(ReminderCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        return ReminderViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(getItem(position), viewModel)
    }

    class ReminderViewHolder private constructor(val binding: ItemArchivedReminderBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(reminder: Reminder, viewModel: ArchiveViewModel) {
            binding.constraintLayout.setOnClickListener {
                viewModel.navigateToEditReminderFragment(reminder)
            }

            binding.reminder = reminder

            val dueDate = reminder.dueDate
            val context = binding.root.context

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

            binding.executePendingBindings()
        }

        private fun findTimeString(time: ZonedDateTime): String {
            // 23:59:59 tomorrow
            val endOfTomorrow = ZonedDateTime.now()
                .withHour(23)
                .withMinute(59)
                .withSecond(59)
                .plusDays(1)
            // 23:59:59 in 7 days
            val endOfNext7Days = ZonedDateTime.now()
                .withHour(23)
                .withMinute(59)
                .withSecond(59)
                .plusWeeks(1)

            return when {
                // add "ago if time is before now
                time.isBefore(ZonedDateTime.now()) -> {
                    "${time.timeFromNowString(true)} ago"
                }
                // add "in" to time from now string if within 3 hours or more than a week
                (ChronoUnit.HOURS.between(ZonedDateTime.now(), time) <= 3) || time.isAfter(
                    endOfNext7Days
                ) -> {
                    "in ${time.timeFromNowString(true)}"
                }
                // if before tomorrow, return the time
                time.isBefore(endOfTomorrow) -> time.format(DateTimeFormatter.ofPattern("h:mm a"))
                // if less than 7 days from today, return the day of the week
                time.isBefore(endOfNext7Days) -> time.format(DateTimeFormatter.ofPattern("EEE"))
                else -> ""
            }
        }

        companion object {
            fun from(parent: ViewGroup): ReminderViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemArchivedReminderBinding.inflate(inflater, parent, false)

                return ReminderViewHolder(binding)
            }
        }
    }

    class ReminderCallback: DiffUtil.ItemCallback<Reminder>() {
        override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
            return oldItem == newItem
        }
    }
}