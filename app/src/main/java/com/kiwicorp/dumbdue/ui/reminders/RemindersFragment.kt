package com.kiwicorp.dumbdue.ui.reminders

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.kiwicorp.dumbdue.EventObserver
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentRemindersBinding
import com.kiwicorp.dumbdue.ui.MainActivity
import com.kiwicorp.dumbdue.ui.reminders.RemindersFragmentDirections.Companion.toNavGraphAdd
import com.kiwicorp.dumbdue.ui.reminders.RemindersFragmentDirections.Companion.toNavGraphEdit
import com.kiwicorp.dumbdue.ui.reminders.RemindersFragmentDirections.Companion.toSettings
import com.kiwicorp.dumbdue.util.DialogNavigator
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import kotlin.concurrent.fixedRateTimer

@AndroidEntryPoint
class RemindersFragment : Fragment(), DialogNavigator {

    override val destId: Int = R.id.navigation_reminders

    private lateinit var binding: FragmentRemindersBinding

    private val args : RemindersFragmentArgs by navArgs()

    private val viewModel: RemindersViewModel by viewModels()

    private lateinit var listAdapter: ReminderAdapter

    private var refreshTimer: Timer? = null

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).bottomAppBar.setOnMenuItemClickListener {
            if (it.itemId == R.id.menu_settings) {
                navigate(toSettings(), findNavController())
                true
            } else {
                false
            }
        }
        (requireActivity() as MainActivity).fab.setOnClickListener {
            viewModel.addReminder()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        handleRequest()
        setupSnackbar()
        setupListAdapter()
        setupNavigation()
        setupRecyclerViewSwiping()
    }

    override fun onResume() {
        super.onResume()
        setupRefreshTimer()
    }

    override fun onPause() {
        super.onPause()
        // Cancels RefreshTimer because, otherwise, the timer will still be running when the user
        // navigates to from RemindersFragment to SettingsFragment and then back to RemindersFragment
        // using the NavigationDrawer. This will cause the app to crash because a new instance of
        // RemindersFragment will be attached the activity while the old one will be detached with
        // the timer still running and thus calling requireActivity() will crash the app.
        cancelRefreshTimer()
    }

    private fun setupNavigation() {
        viewModel.eventAddReminder.observe(viewLifecycleOwner, EventObserver {
            navigate(toNavGraphAdd(), findNavController())
        })
        viewModel.eventEditReminder.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(toNavGraphEdit(it))
        })
    }

    private fun handleRequest() {
        arguments?.let {
            with(args) {
                if (request != 0 && reminderId != "") {
                    viewModel.handleRequest(request,reminderId)
                }
            }
        }
    }

    private fun setupListAdapter() {
        val viewModel = binding.viewmodel
        if (viewModel != null)  {
            listAdapter = ReminderAdapter(viewModel)
            binding.remindersRecyclerView.adapter = listAdapter
        } else {
            Timber.d("ViewModel not initialized when attempting to set up adapter.")
        }
    }

    private fun setupSnackbar() {
        viewModel.snackbarMessage.observe(viewLifecycleOwner, EventObserver { snackbar ->
            with (requireActivity() as MainActivity) {
                snackbar.show(coordinatorLayout, fab)
            }
        })
    }

    private fun setupRecyclerViewSwiping() {
        //create an item touch helper to allow for reminders to be able to be swiped
        val itemTouchHelperCallback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            //doesn't allow reminders to be moved
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean { return false }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (viewHolder is ReminderAdapter.HeaderViewHolder) return

                if (direction == ItemTouchHelper.RIGHT) { //if user swipes right, delete reminder
                    viewModel.delete((viewHolder as ReminderAdapter.ReminderViewHolder).binding.reminder!!)
                } else if ( direction == ItemTouchHelper.LEFT) {//if user swipes left, complete reminder
                    viewModel.complete((viewHolder as ReminderAdapter.ReminderViewHolder).binding.reminder!!)
                }
            }
            //allows for color and icons in the background when reminders are swiped
            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                //do nothing if
                if (viewHolder is ReminderAdapter.HeaderViewHolder) return

                val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete) as Drawable
                val checkIcon = ContextCompat.getDrawable(requireContext(),R.drawable.ic_check) as Drawable
                lateinit var swipeBackground: ColorDrawable

                val itemView = viewHolder.itemView
                val iconMarginVertical = (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

                if (dX > 0) {//if user swiped right
                    swipeBackground = ColorDrawable(Color.parseColor("#ff6961"))//sets swipe background to red
                    swipeBackground.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                    deleteIcon.setBounds(itemView.left + iconMarginVertical, itemView.top + iconMarginVertical,
                        itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth, itemView.bottom - iconMarginVertical)
                    //draw background and delete icon
                    swipeBackground.draw(c)
                    //stops icon from being shown outside of the background
                    c.save()
                    c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                    deleteIcon.draw(c)
                    c.restore()

                } else {//if user swiped left
                    swipeBackground = ColorDrawable(Color.parseColor("#77dd77"))//sets swipe background to green
                    swipeBackground.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    checkIcon.setBounds(itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth, itemView.top + iconMarginVertical,
                        itemView.right - iconMarginVertical, itemView.bottom - iconMarginVertical)
                    checkIcon.level = 0
                    //draw background and check mark icon
                    swipeBackground.draw(c)
                    //stops icon from being shown outside of the background
                    c.save()
                    c.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    checkIcon.draw(c)
                    c.restore()
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.remindersRecyclerView)
    }

    /**
     * Sets up a timer that updates the recycler view every minute on the minute
     */
    private fun setupRefreshTimer() {
        val thisMinuteCalendar = Calendar.getInstance().apply {
            set(Calendar.MILLISECOND, 0)
            set(Calendar.SECOND, 0)
        }
        if (refreshTimer == null) {
            refreshTimer = fixedRateTimer("RefreshTimer", false, thisMinuteCalendar.time, 60000) {
                requireActivity().runOnUiThread {
                    Timber.d("Recycler View Updated id: ${this.hashCode()}")
                    listAdapter.addHeadersAndSubmitList(viewModel.reminders.value)
                    listAdapter.notifyDataSetChanged()
                }
            }
        }
    }
    /**
     * Cancels timer
     */
    private fun cancelRefreshTimer() {
        refreshTimer?.cancel()
        refreshTimer = null
    }

}
