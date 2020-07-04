package com.kiwicorp.dumbdue.ui.reminders

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kiwicorp.dumbdue.data.Reminder

@BindingAdapter("items")
fun RecyclerView.setItems(items: List<Reminder>?) {
    items?.let {
        (adapter as ReminderAdapter).addHeadersAndSubmitList(it)
    }
}