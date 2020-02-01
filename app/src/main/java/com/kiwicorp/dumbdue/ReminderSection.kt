package com.kiwicorp.dumbdue

import android.view.View
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import java.util.*
import kotlin.collections.ArrayList

class ReminderSection(title: String, list:LinkedList<Reminder>, clickListener: ClickListener) : Section(SectionParameters.builder()
    .itemResourceId(R.layout.layout_reminder_item)
    .headerResourceId(R.layout.section_reminder_header)
    .build()) {

    companion object {
        val reminderSectionList: ArrayList<ReminderSection> = ArrayList()

        //returns section reminder belongs to
        fun getReminderSection(positionInAdapter: Int): ReminderSection {
            val overdueSection : ReminderSection = reminderSectionList[0]
            val todaySection : ReminderSection = reminderSectionList[1]
            val tomorrowSection : ReminderSection = reminderSectionList[2]
            val next7daysSection : ReminderSection = reminderSectionList[3]
            val futureSection : ReminderSection = reminderSectionList[4]

            val sectionList: ArrayList<ReminderSection> = ArrayList()

            if (overdueSection.isVisible) {
                sectionList.add(overdueSection)
            }

            if (todaySection.isVisible) {
                sectionList.add(todaySection)
            }

            if (tomorrowSection.isVisible) {
                sectionList.add(tomorrowSection)
            }

            if (next7daysSection.isVisible) {
                sectionList.add(next7daysSection)
            }

            if (futureSection.isVisible) {
                sectionList.add(futureSection)
            }

            val iterator = sectionList.listIterator()
            for (element in iterator) {
                val sectionAdapterPosition = MainActivity.sectionAdapter.getSectionPosition(element)
                if (positionInAdapter < sectionAdapterPosition) {
                    return sectionList[iterator.previousIndex() - 1]
                }
            }
            return sectionList.last()
        }

        //returns section reminder belongs to
        fun getReminderSection(reminder: Reminder): ReminderSection {
            val futureSection : ReminderSection = reminderSectionList[4]
            val next7daysSection : ReminderSection = reminderSectionList[3]
            val tomorrowSection : ReminderSection = reminderSectionList[2]
            val todaySection : ReminderSection = reminderSectionList[1]
            val overdueSection : ReminderSection = reminderSectionList[0]


            if (futureSection.getList().indexOf(reminder) != -1) {
                return futureSection
            } else if (next7daysSection.getList().indexOf(reminder) != -1) {
                return next7daysSection
            } else if (tomorrowSection.getList().indexOf(reminder) != -1) {
                return tomorrowSection
            } else if (todaySection.getList().indexOf(reminder) != -1) {
                return todaySection
            } else {
                return overdueSection
            }

        }
    }

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

    @NonNull fun getTitle(): String {
        return title
    }

    interface ClickListener {
        fun onItemRootViewClicked(@NonNull sectionTitle: String, itemPosition: Int)
    }

}