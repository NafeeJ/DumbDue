package com.kiwicorp.dumbdue.ui.reminders

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kiwicorp.dumbdue.R

class RemindersFragment : Fragment() {

    companion object {
        fun newInstance() = RemindersFragment()
    }

    private lateinit var viewModel: RemindersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.reminders_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RemindersViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
