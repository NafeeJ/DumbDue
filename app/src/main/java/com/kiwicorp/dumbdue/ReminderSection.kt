package com.kiwicorp.dumbdue

import android.view.View
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import java.util.*

class ReminderSection(title: String, list:LinkedList<Reminder>, clickListener: ClickListener) : Section(SectionParameters.builder()
    .itemResourceId(R.layout.layout_reminder_item)
    .headerResourceId(R.layout.section_reminder_header)
    .build()) {

    private val list: LinkedList<Reminder> = list
    private val title: String = title
    private val clickListener : ClickListener = clickListener

    override fun getContentItemsTotal(): Int {
        return list.size
    }

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val reminderHolder : ReminderViewHolder = holder as ReminderViewHolder
        reminderHolder.bind(list[position])

        reminderHolder.rootView.setOnClickListener {
            clickListener.onItemRootViewClicked(title,reminderHolder.adapterPosition)
        }

    }

    override fun getItemViewHolder(view: View): RecyclerView.ViewHolder {
        return ReminderViewHolder(view)
    }

    override fun getEmptyViewHolder(view: View): RecyclerView.ViewHolder {
        return SectionedRecyclerViewAdapter.EmptyViewHolder(view)
    }

    override fun getHeaderViewHolder(view: View): RecyclerView.ViewHolder {
        return HeaderViewHolder(view)
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder?) {
        val headerHolder: HeaderViewHolder = holder as HeaderViewHolder

        headerHolder.sectionTitle.text = title
    }

    @NonNull fun getList(): LinkedList<Reminder> {
        return list
    }

    fun add(reminder: Reminder) {
        list.add(reminder)
    }

    fun remove(reminder: Reminder) {
        list.remove(reminder)
    }

    interface ClickListener {
        fun onItemRootViewClicked(@NonNull sectionTitle: String, itemPosition: Int)
    }

}