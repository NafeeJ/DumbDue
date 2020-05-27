package com.kiwicorp.dumbdue.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kiwicorp.dumbdue.data.Reminder
import com.kiwicorp.dumbdue.databinding.ItemReminderBinding
import com.kiwicorp.dumbdue.adapters.ReminderAdapter.ViewHolder
import com.kiwicorp.dumbdue.ui.reminders.RemindersViewModel

class ReminderAdapter(private val viewModel: RemindersViewModel):
    ListAdapter<Reminder, ViewHolder>(ReminderDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(viewModel, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemReminderBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: RemindersViewModel, item: Reminder) {
            binding.viewmodel = viewModel
            binding.reminder = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemReminderBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
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
class ReminderDiffCallback: DiffUtil.ItemCallback<Reminder>() {
    override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem.reminderId == newItem.reminderId
    }

    override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem == newItem
    }
}