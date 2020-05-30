package com.kiwicorp.dumbdue

import android.view.View
import com.google.android.material.snackbar.Snackbar

/**
 * Data class with content that can be displayed to a SnackBar
 *
 * Purpose is to wrap this class in LiveData in a ViewModel that a ui class can subscribe to to
 * display a [Snackbar] message with an action if needed
 */
data class SnackbarMessage(
    val text: String,
    val duration: Int,
    val actionText: String = "",
    val action: ((View) -> Unit)? = null)