package com.kiwicorp.dumbdue.ui.reminders

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kiwicorp.dumbdue.EventObserver
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.databinding.FragmentRemindersBinding
import dagger.android.support.DaggerFragment
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer

class RemindersFragment : DaggerFragment() {

    private lateinit var binding: FragmentRemindersBinding

    private val args : RemindersFragmentArgs by navArgs()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: RemindersViewModel by viewModels { viewModelFactory }

    private lateinit var listAdapter: ReminderAdapter

    private var refreshTimer: Timer? = null

    /**
     * Prevents FAB from being clicked twice. (Clicking twice will cause the app to crash since
     * the NavController will have changed and won't have an action of navigating to
     * AddReminderFragment)
     */
    private val onDestinationChangedListener =
        NavController.OnDestinationChangedListener { controller, destination, arguments ->
            Timber.d("Destination Changed ${this.hashCode()}")
            if (destination.id == R.id.add_reminder_fragment_dest) {
                binding.fab.isClickable = false
                binding.fab.hide()
            } else if (destination.id == R.id.reminders_fragment_dest) {
                binding.fab.isClickable = true
                binding.fab.show()
            }
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
        setHasOptionsMenu(true)
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.reminders_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_settings) {
            val action = RemindersFragmentDirections.actionRemindersFragmentDestToSettingsFragmentDest()
            findNavController().navigate(action)
            return true
        }
        return false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupSnackbar()
        setupListAdapter()
        setupNavigation()
        setupRecyclerViewSwiping()
        setupRefreshTimer()
        findNavController().addOnDestinationChangedListener(onDestinationChangedListener)
    }

    override fun onResume() {
        super.onResume()
        setupRefreshTimer()
        findNavController().addOnDestinationChangedListener(onDestinationChangedListener)
    }

    override fun onPause() {
        super.onPause()
        // Cancels RefreshTimer because, otherwise, the timer will still be running when the user
        // navigates to from RemindersFragment to SettingsFragment and then back to RemindersFragment
        // using the NavigationDrawer. This will cause the app to crash because a new instance of
        // RemindersFragment will be attached the activity while the old one will be detached with
        // the timer still running and thus calling requireActivity() will crash the app.
        cancelRefreshTimer()
        findNavController().removeOnDestinationChangedListener(onDestinationChangedListener)
    }

    private fun setupNavigation() {
        viewModel.eventAddReminder.observe(viewLifecycleOwner, EventObserver {
            navigateToAddReminder()
        })
        viewModel.eventEditReminder.observe(viewLifecycleOwner, EventObserver { id ->
            navigateToEditReminder(id)
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
            listAdapter = ReminderAdapter(viewModel)
            binding.remindersRecyclerView.adapter = listAdapter
        } else {
            Timber.d("ViewModel not initialized when attempting to set up adapter.")
        }
    }

    private fun setupSnackbar() {
        viewModel.snackbarMessage.observe(viewLifecycleOwner, EventObserver { snackbarMessage ->
            val snackbar = Snackbar.make(binding.coordinatorLayout,snackbarMessage.text,snackbarMessage.duration)
            if (snackbarMessage.action != null) {
                snackbar.setAction(snackbarMessage.actionText,snackbarMessage.action)
            }
            snackbar.show()
        })
        arguments?.let {
            with(args) {
                if (request != 0 && reminderId != "") {
                    viewModel.handleRequest(request,reminderId)
                }
            }
        }
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

                val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.delete_white) as Drawable
                val checkIcon = ContextCompat.getDrawable(requireContext(),R.drawable.check_white) as Drawable
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
