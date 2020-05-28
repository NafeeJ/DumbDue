package com.kiwicorp.dumbdue.ui.preferences.editquickaccesstimes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
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
        return inflater.inflate(R.layout.fragment_edit_quick_access_times, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(EditQuickAccessTimesViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
