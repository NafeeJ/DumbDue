package com.kiwicorp.dumbdue.ui.preferences.edittimesetbuttons

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kiwicorp.dumbdue.R

class EditTimeSetButtons : Fragment() {

    companion object {
        fun newInstance() = EditTimeSetButtons()
    }

    private lateinit var viewModel: EditTimeSetButtonsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_time_set_buttons, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(EditTimeSetButtonsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
