package com.kiwicorp.dumbdue.ui.addreminder

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kiwicorp.dumbdue.R

class AddReminderFragment : Fragment() {

    companion object {
        fun newInstance() = AddReminderFragment()
    }

    private lateinit var viewModel: AddReminderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_reminder_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AddReminderViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
