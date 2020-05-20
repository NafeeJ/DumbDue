package com.kiwicorp.dumbdue.ui.editreminder

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kiwicorp.dumbdue.R

class EditReminderFragment : Fragment() {

    companion object {
        fun newInstance() = EditReminderFragment()
    }

    private lateinit var viewModel: EditReminderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.edit_reminder_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(EditReminderViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
