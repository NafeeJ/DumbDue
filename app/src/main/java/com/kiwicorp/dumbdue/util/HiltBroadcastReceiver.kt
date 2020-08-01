package com.kiwicorp.dumbdue.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Abstract class for a workaround for hilt bug with broadcast receivers.
 * https://github.com/google/dagger/issues/1918#issuecomment-644057247
 */
abstract class HiltBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

    }
}