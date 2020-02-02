package com.kiwicorp.dumbdue

import java.util.*

class DumbTimer(title: String, initialTimeInMillis: Int) {

    companion object val timerList: LinkedList<DumbTimer> = LinkedList()

    private val initialTimeInMillis: Int

    private var title: String
    private var currentTimeInMillis: Int
    //Boolean states for the timer
    private var initialPaused: Boolean
    private var midPaused: Boolean
    private var counting: Boolean

    init {
        this.title = title
        this.initialTimeInMillis = initialTimeInMillis
        currentTimeInMillis = initialTimeInMillis
        initialPaused = true
        midPaused = false
        counting = false

        timerList.addFirst(this)
    }

    fun getInitialTimeInMillis(): Int { return initialTimeInMillis }
    fun getTitle(): String { return title }
}