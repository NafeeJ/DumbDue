package com.kiwicorp.dumbdue.ui.reminders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentRemindersBinding
import com.kiwicorp.dumbdue.util.InjectorUtils

class RemindersFragment : Fragment() {

    private lateinit var binding: FragmentRemindersBinding

    private val viewModel: RemindersViewModel by viewModels {
        InjectorUtils.provideRemindersViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_reminders,container,false)
        binding = FragmentRemindersBinding.bind(root).apply {
            viewmodel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupNavigation()
    }

    private fun setupNavigation() {
        viewModel.eventAddReminder.observe(viewLifecycleOwner, Observer {
            if (it == true)  {
                navigateToAddReminder()
                viewModel.onAddReminderComplete()
            }
        })
    }

    private fun navigateToAddReminder() {
        val action = RemindersFragmentDirections.actionRemindersFragmentToAddReminderFragment()
        findNavController().navigate(action)
    }

}
