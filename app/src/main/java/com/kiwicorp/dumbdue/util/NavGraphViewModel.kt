package com.kiwicorp.dumbdue.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels

/**
 * Returns a [ViewModel] scoped to a navigation graph present on the
 * {@link NavController} back stack
 *
 * @param navGraphId ID of a NavGraph that exists on the {@link NavController} back stack
 *
 * This function was created because some fragments (eg: com.kiwicorp.dumbdue.ui.addeditreminder.ChooseAutoSnoozeFragment)
 * receive a navGraphId as an argument which would not be initialized in time to be used by [navGraphViewModels]
 */
inline fun <reified VM : ViewModel> Fragment.getNavGraphViewModel(
    navGraphId: Int,
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): VM {
    val backStackEntry = findNavController().getBackStackEntry(navGraphId)

    val viewModelProvider = ViewModelProvider(
        backStackEntry.viewModelStore,
            factoryProducer?.invoke() ?: defaultViewModelProviderFactory
        )

    return viewModelProvider.get(VM::class.java)
}