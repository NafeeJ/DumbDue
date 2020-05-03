package com.kiwicorp.dumbdue

import android.view.View
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.kiwicorp.dumbdue.ui.reminder.Reminder
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import java.util.*

class ReminderSection(
    val title: String,
    private val clickListener: ClickListener):
    Section(SectionParameters.builder()
        .itemResourceId(R.layout.item_reminder)
        .headerResourceId(R.layout.section_header_reminder)
        .build()) {
    private var list : List<Reminder> = ArrayList<Reminder>()

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

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder?) {
        val headerHolder: HeaderViewHolder = holder as HeaderViewHolder
        headerHolder.sectionTitle.text = title
    }

    override fun getHeaderViewHolder(view: View): RecyclerView.ViewHolder {
        return HeaderViewHolder(view)
    }

    override fun getEmptyViewHolder(view: View): RecyclerView.ViewHolder {
        return SectionedRecyclerViewAdapter.EmptyViewHolder(view)
    }

    override fun getContentItemsTotal(): Int { return list.size }

    @NonNull fun getList(): List<Reminder> { return list }

    fun setList(list: List<Reminder>) {this.list = list}

    interface ClickListener {
        fun onItemRootViewClicked(@NonNull sectionTitle: String, itemPosition: Int)
    }
}