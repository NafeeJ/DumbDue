package com.kiwicorp.dumbdue.ui.reminders

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.kiwicorp.dumbdue.NavEventObserver
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.adapters.ReminderAdapter
import com.kiwicorp.dumbdue.databinding.FragmentRemindersBinding
import com.kiwicorp.dumbdue.util.InjectorUtils

class RemindersFragment : Fragment() {

    private lateinit var binding: FragmentRemindersBinding

    private val viewModel: RemindersViewModel by viewModels {
        InjectorUtils.provideRemindersViewModelFactory(requireContext())
    }

    private lateinit var listAdapter: ReminderAdapter

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
        setupListAdapter()
        setupNavigation()
    }

    private fun setupNavigation() {
        viewModel.eventAddReminder.observe(viewLifecycleOwner, NavEventObserver {
            navigateToAddReminder()
        })
        viewModel.eventEditReminder.observe(viewLifecycleOwner, NavEventObserver {
            navigateToEditReminder(it)
        })
    }

    private fun navigateToAddReminder() {
        val action = RemindersFragmentDirections.actionRemindersFragmentDestToNavGraphAdd()
        findNavController().navigate(action)
    }

    private fun navigateToEditReminder(reminderId: String) {
        val action = RemindersFragmentDirections.actionRemindersFragmentDestToNavGraphEdit(reminderId)
        findNavController().navigate(action)
    }

    private fun setupListAdapter() {
        val viewModel = binding.viewmodel
        if (viewModel != null)  {
            listAdapter =
                ReminderAdapter(viewModel)
            binding.remindersRecyclerView.adapter = listAdapter
        } else {
            Log.d("RemindersFragment","ViewModel not initialized when attempting to set up adapter.")
        }
    }

}
