package com.kiwicorp.dumbdue.util

import androidx.navigation.NavController
import androidx.navigation.NavDirections

/**
 * Interface for navigation to dialogs
 *
 * Any ui that navigates to a dialog using the Navigation Component should implement this
 */
interface DialogNavigator {
    /**
     * the id of this destination in the nav graph
     */
    val destId: Int
    /**
     * Navigates to [navDirection] with the provided [navController]
     *
     * Checks if current destination is the correct destination navigate gets called after the
     * destination has changed
     */
    fun navigate(navDirection: NavDirections, navController: NavController) {
        if (navController.currentDestination?.id == destId) {
            navController.navigate(navDirection)
        }
    }
}