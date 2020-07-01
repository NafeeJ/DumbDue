package com.kiwicorp.dumbdue.util

import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.kiwicorp.dumbdue.R

fun <T> Fragment.getDropDownMenuAdapter(list: List<T>): ArrayAdapter<T> {
    return ArrayAdapter(requireContext(), R.layout.item_drop_down_menu, list)
}

