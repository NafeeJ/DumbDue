package com.kiwicorp.dumbdue

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity: AppCompatActivity() {

    companion object {const val TAG: String = "MainActivity"}

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.scheduleFAB)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_reminders,R.id.nav_timers,R.id.nav_settings),drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //navigates to intending destination and if navigating to settings, item will not be checked
        navView.setNavigationItemSelectedListener {
            if (it.itemId == R.id.nav_settings) {
                it.isChecked = false
            }
            navController.navigate(it.itemId)
            drawerLayout.closeDrawer(GravityCompat.START,true)
            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d(TAG,"Destination: ${destination.id}")
            Log.d(TAG,"Nav Reminder: ${R.id.nav_reminders}")
            Log.d(TAG,"Nav Timers: ${R.id.nav_timers}")
            Log.d(TAG,"Nav Settings: ${R.id.nav_settings}")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        if (navController.currentDestination!!.id == R.id.nav_settings) {
            Toast.makeText(applicationContext,"GOING FROM SETTINGS", Toast.LENGTH_LONG).show()
        }
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}
