package com.kiwicorp.dumbdue

import androidx.lifecycle.Observer

/**
 * A wrapper class for events that are exposed via LiveData. (Most common events are navigation and
 * displaying snackbar messages)
 *
 * [content] will be passed the handler that completes this event. If no arguments are
 * needed for the handler, [content] should be [Unit].
 */
class Event<out T>(private val content: T) {
    private var hasBeenHandled = false

    /**
     * Returns the [content] and prevents its use again.
     *
     * This function is only used by [EventObserver].
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the [content], even if it's already been handled.
     */
    fun peekContent(): T = content
}

/**
 * An [Observer] for [Event]s (simplifies the pattern of checking if a [Event] has already been
 * handled).
 *
 * [eventHandler] is only called if the event has not already been handled.
 */
class EventObserver<T>(private val eventHandler: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.getContentIfNotHandled()?.let {
            eventHandler(it)
        }
    }
}