package com.kiwicorp.dumbdue.ui.reminders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentRemindersBinding
import com.kiwicorp.dumbdue.util.InjectorUtils

class RemindersFragment : Fragment() {

    private lateinit var binding: FragmentRemindersBinding

    private val viewModel: RemindersViewModel by viewModels {
        InjectorUtils.provideRemindersViewModelFactory(requireContext())
    }

    companion object {
        fun newInstance() = RemindersFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_reminders, container, false)

        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        return binding.root
    }

}
