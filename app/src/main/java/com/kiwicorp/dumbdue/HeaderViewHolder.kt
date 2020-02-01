package com.kiwicorp.dumbdue

import android.view.View
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView

class HeaderViewHolder(@NonNull view: View) : RecyclerView.ViewHolder(view) {

    val sectionTitle: TextView = view.findViewById(R.id.sectionTitle)
}