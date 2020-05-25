package com.kiwicorp.dumbdue

import androidx.lifecycle.Observer

/**
 * A wrapper class for events that are exposed via LiveData that a fragment/activity would respond
 * to by navigating to another fragment/activity.
 *
 * [navArg] will be passed to the navigation action that completes this event. If no arguments are
 * needed for the navigation action, [navArg] should be [Unit].
 */
class NavEvent<out T>(private val navArg: T) {
    var hasBeenCompleted = false
        private set //For external read only access

    /**
     * Returns the navArguments and prevents this event from being completed again.
     * This function is only used by [NavEventObserver].
     */
    fun getNavArgIfNotCompleted(): T? {
        return if (hasBeenCompleted) {
            null
        } else {
            hasBeenCompleted = true
            navArg
        }
    }
}

/**
 * An [Observer] for [NavEvent]s (simplifies the pattern of checking if a [NavEvent] has already
 * been completed).
 *
 * [navAction] is a lambda that should complete the event by navigating to a fragment. It is only
 * called if the event has not already been completed.
 */
class NavEventObserver<T>(private val navAction: (T) -> Unit) : Observer<NavEvent<T>> {
    override fun onChanged(navEvent: NavEvent<T>?) {
        navEvent?.getNavArgIfNotCompleted()?.let {
            navAction(it)
        }
    }
}