package com.kiwicorp.dumbdue

import java.util.*

class DumbTimer constructor(private var title: String, private val initialTimeInMillis: Int) {

    companion object val timerList: LinkedList<DumbTimer> = LinkedList()

    private var currentTimeInMillis: Int
    //Boolean states for the timer
    private var initialPaused: Boolean
    private var midPaused: Boolean
    private var counting: Boolean

    init {
        currentTimeInMillis = initialTimeInMillis
        initialPaused = true
        midPaused = false
        counting = false

        timerList.addFirst(this)
    }

    fun getInitialTimeInMillis(): Int { return initialTimeInMillis }
    fun getTitle(): String { return title }
}