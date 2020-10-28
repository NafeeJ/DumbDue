package com.kiwicorp.dumbdue.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.findNavController
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kiwicorp.dumbdue.R
import com.kiwicorp.dumbdue.data.repeat.RepeatDailyInterval
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {

    lateinit var bottomAppBar: BottomAppBar
    lateinit var fab: FloatingActionButton
    lateinit var coordinatorLayout: CoordinatorLayout

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        delegate.localNightMode = viewModel.theme
        fab = findViewById(R.id.fab)
        bottomAppBar = findViewById(R.id.bottom_app_bar)
        coordinatorLayout = findViewById(R.id.coordinator_layout)
        setupNavControllerAndFAB()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun setupNavControllerAndFAB() {
        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_add_reminder -> setupBottomAppBarForAddReminder()
                R.id.navigation_reminders -> setupBottomAppBarForReminders()
                R.id.navigation_edit_reminder -> setupBottomAppBarForEditReminder()
                R.id.navigation_settings -> setupBottomAppBarForSettings()
            }
        }
    }

    private fun setupBottomAppBarForAddReminder() {
        fab.hide()
        hideBottomAppBar()
    }

    private fun setupBottomAppBarForReminders() {
        bottomAppBar.visibility = View.VISIBLE
        bottomAppBar.performShow()
        fab.show()
        bottomAppBar.replaceMenu(R.menu.appbar_reminder)
    }

    private fun setupBottomAppBarForEditReminder() {
        fab.hide()
        bottomAppBar.replaceMenu(R.menu.appbar_edit_reminder)
    }

    private fun setupBottomAppBarForSettings() {
        fab.hide()
        hideBottomAppBar()
    }

    private fun hideBottomAppBar() {
        bottomAppBar.performHide()
        // Get a handle on the animator that hides the bottom app bar so we can wait to hide
        // the fab and bottom app bar until after it's exit animation finishes.
        bottomAppBar.animate().setListener(object : AnimatorListenerAdapter() {
            var isCanceled = false
            override fun onAnimationEnd(animation: Animator?) {
                if (isCanceled) return

                // Hide the BottomAppBar to avoid it showing above the keyboard
                // when composing a new email.
                bottomAppBar.visibility = View.GONE
            }
            override fun onAnimationCancel(animation: Animator?) {
                isCanceled = true
            }
        })
    }

}

// Keys for navigation
const val REQUEST_DELETE = Activity.RESULT_FIRST_USER + 1
const val REQUEST_COMPLETE = Activity.RESULT_FIRST_USER + 2