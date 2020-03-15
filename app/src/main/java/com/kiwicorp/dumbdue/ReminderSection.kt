package com.kiwicorp.dumbdue

import android.view.View
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import java.util.*
import kotlin.collections.ArrayList

class ReminderSection(
    private val title: String,
    private val list:LinkedList<Reminder>,
    private val clickListener: ClickListener):
    Section(SectionParameters.builder()
        .itemResourceId(R.layout.item_reminder)
        .headerResourceId(R.layout.section_header_reminder)
        .build()) {

    companion object {
        lateinit var reminderSectionList: Array<ReminderSection>

        //returns the section of the reminder at the given position
        fun getReminderSection(positionInAdapter: Int): ReminderSection {
            val overdueSection : ReminderSection = reminderSectionList[0]
            val todaySection : ReminderSection = reminderSectionList[1]
            val tomorrowSection : ReminderSection = reminderSectionList[2]
            val next7daysSection : ReminderSection = reminderSectionList[3]
            val futureSection : ReminderSection = reminderSectionList[4]

            val sectionList: ArrayList<ReminderSection> = ArrayList()
            //adds sections to section list if they're visible
            if (overdueSection.isVisible) { sectionList.add(overdueSection) }
            if (todaySection.isVisible) { sectionList.add(todaySection) }
            if (tomorrowSection.isVisible) { sectionList.add(tomorrowSection) }
            if (next7daysSection.isVisible) { sectionList.add(next7daysSection) }
            if (futureSection.isVisible) { sectionList.add(futureSection) }
            //finds the section of the reminder
            val iterator = sectionList.listIterator()
            for (element in iterator) {
                val sectionAdapterPosition = ReminderActivity.sectionAdapter.getSectionPosition(element)
                if (positionInAdapter < sectionAdapterPosition) {
                    return sectionList[iterator.previousIndex() - 1]
                }
            }
            return sectionList.last()
        }

        //returns the section of the given reminder
        fun getReminderSection(reminder: Reminder): ReminderSection {
            val overdueSection : ReminderSection = reminderSectionList[0]
            val todaySection : ReminderSection = reminderSectionList[1]
            val tomorrowSection : ReminderSection = reminderSectionList[2]
            val next7daysSection : ReminderSection = reminderSectionList[3]
            val futureSection : ReminderSection = reminderSectionList[4]

            when {
                futureSection.getList().indexOf(reminder) != -1 -> return futureSection
                next7daysSection.getList().indexOf(reminder) != -1 -> return next7daysSection
                tomorrowSection.getList().indexOf(reminder) != -1 -> return tomorrowSection
                todaySection.getList().indexOf(reminder) != -1 -> return todaySection
            }
            return overdueSection
        }
    }

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val reminderHolder : ReminderViewHolder = holder as ReminderViewHolder
        reminderHolder.bind(list[position])

        reminderHolder.rootView.setOnClickListener {
            clickListener.onItemRootViewClicked(title,reminderHolder.adapterPosition)
        }
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder?) {
        val headerHolder: HeaderViewHolder = holder as HeaderViewHolder
        headerHolder.sectionTitle.text = title
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

    override fun getContentItemsTotal(): Int { return list.size }

    @NonNull fun getList(): LinkedList<Reminder> { return list }

    @NonNull fun getTitle(): String { return title }

    interface ClickListener {
        fun onItemRootViewClicked(@NonNull sectionTitle: String, itemPosition: Int)
    }
}