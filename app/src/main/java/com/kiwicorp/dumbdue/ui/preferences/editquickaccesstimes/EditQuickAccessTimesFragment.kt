package com.kiwicorp.dumbdue.ui.preferences.editquickaccesstimes

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kiwicorp.dumbdue.R

class EditQuickAccessTimesFragment : Fragment() {

    companion object {
        fun newInstance() = EditQuickAccessTimesFragment()
    }

    private lateinit var viewModel: EditQuickAccessTimesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.edit_quick_access_times_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(EditQuickAccessTimesViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
