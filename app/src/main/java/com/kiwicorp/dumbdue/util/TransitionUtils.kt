package com.kiwicorp.dumbdue.util

import androidx.transition.Transition
import com.google.android.material.transition.FadeProvider
import com.google.android.material.transition.MaterialSharedAxis
import com.google.android.material.transition.ScaleProvider

private const val ELEVATION_SCALE = 0.85F

/**
 * Create an elevation scale transition that, e.g., can be used in conjunction with a container
 * transform to give the effect that the outgoing screen is receding or advancing along the z-axis.
 *
 * TODO: remove once a MaterialElevationScale is available in the library.
 */
fun createMaterialElevationScale(forward: Boolean): Transition {
    return MaterialSharedAxis(MaterialSharedAxis.Z, forward).apply {
        val scaleProvider = primaryAnimatorProvider as ScaleProvider
        scaleProvider.incomingStartScale = ELEVATION_SCALE
        scaleProvider.outgoingEndScale = ELEVATION_SCALE
        secondaryAnimatorProvider = FadeProvider()
    }
}
